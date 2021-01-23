package com.zivapp.countandnote.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.zivapp.countandnote.R
import com.zivapp.countandnote.databinding.ItemGroupMenuBinding
import com.zivapp.countandnote.databinding.ItemMainMenuBinding
import com.zivapp.countandnote.firebase.FirebaseHelper
import com.zivapp.countandnote.model.FormatSum
import com.zivapp.countandnote.model.MainMenuNote
import com.zivapp.countandnote.model.Note
import com.zivapp.countandnote.util.UtilConverter
import com.zivapp.countandnote.util.UtilFormatting
import com.zivapp.countandnote.view.groups.GroupNoteActivity
import com.zivapp.countandnote.view.note.NoteActivity

class AdapterMenu(val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TAG = "AdapterMenu"
    private val TYPE_NOTE = 1
    private val TYPE_GROUP = 2

    var notesList: List<MainMenuNote> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class NoteViewHolder(private val binding: ItemMainMenuBinding, val context: Context) :
            RecyclerView.ViewHolder(binding.root) {

        private val TAG = "NoteViewHolder in AdapterMenu"

        fun setNoteDetails(list: List<MainMenuNote>, position: Int) {
            // Binding data to XML file: item_main_menu.xml
            binding.note = (list[position])

            // If User didn't set title name the "New note" will be added by default
            val defaultName: String = UtilFormatting.getDefaultName(position)
            val note = Note()
            note.message = defaultName
            binding.item = note

            // Binding formatted total sum to XML file: item_main_menu.xml
            val formattedTotalSum: String =
                    UtilConverter.customStringFormat(list[position].total_sum)
            binding.format = FormatSum(formattedTotalSum)
        }

        fun cardClickListener(list: List<MainMenuNote>, position: Int) {
            cardViewClickListener(binding.cardViewNote, list, position)
        }

        fun cardLongClickListener(list: MutableList<MainMenuNote>, position: Int) {
            cardViewLongClickListener(binding.cardViewNote, list, position)
        }
    }

    inner class GroupViewHolder(private val binding: ItemGroupMenuBinding, val context: Context) :
            RecyclerView.ViewHolder(binding.root) {

        fun setNoteDetails(list: List<MainMenuNote>, position: Int) {
            // Binding data to XML file: item_main_menu.xml
            binding.note = list[position]

            // If User didn't set title name the "New note" will be added by default
            val defaultName: String = UtilFormatting.getDefaultName(position)
            val note = Note()
            note.message = defaultName
            binding.item = note

            // Binding formatted total sum to XML file: item_main_menu.xml
            val formattedTotalSum: String =
                    UtilConverter.customStringFormat(list[position].total_sum)
            binding.format = FormatSum(formattedTotalSum)
        }

        fun cardClickListener(list: List<MainMenuNote>, position: Int) {
            cardViewClickListener(binding.cardViewNote, list, position)
        }

        fun cardLongClickListener(list: MutableList<MainMenuNote>, position: Int) {
            cardViewLongClickListener(binding.cardViewNote, list, position)
        }
    }

    fun cardViewLongClickListener(view: CardView, list: MutableList<MainMenuNote>, position: Int) {
        view.setOnLongClickListener {
            AlertDialog.Builder(context)
                    .setIcon(android.R.drawable.ic_notification_clear_all)
                    .setTitle(R.string.note_title)
                    .setMessage(R.string.note_message)
                    .setPositiveButton(R.string.note_positive_btn)
                    { _, _ ->
                        Log.v(TAG, "REMOVING NOTE: ${list[position].title}" +
                                " ID_note: ${list[position].id}")

                        val firebaseHelper = FirebaseHelper()

                        if (list[position].group) {
                            firebaseHelper.deleteGroupNoteFromFirebase(list, position)
                        } else {
                            firebaseHelper.deleteMenuNoteFromFirebase(list, position)
                        }

                        list.removeAt(position)
                        notifyItemRemoved(position)
                        showToast(context)
                    }
                    .setNegativeButton(R.string.note_negative_btn, null)
                    .show()
            false
        }
    }

    private fun showToast(context: Context) {
        Toast.makeText(context, R.string.note_toast, Toast.LENGTH_SHORT).show()
    }

    fun cardViewClickListener(cardView: CardView, list: List<MainMenuNote>, position: Int) {
        // Opening new Activity (group note or single note) and send ID
        cardView.setOnClickListener {
            if (list[position].group) {
                Log.v(TAG, "Opening GroupNoteActivity and send ID: "
                        + list[position].id)
                openNewActivity(GroupNoteActivity::class.java, context, list, position)
            } else {
                Log.v(TAG, "Opening NoteActivity and send ID: "
                        + list[position].id)
                openNewActivity(NoteActivity::class.java, context, list, position)
            }
        }
    }

    private fun openNewActivity(clazz: Class<*>, context: Context,
                                list: List<MainMenuNote>, position: Int) {
        val intent = Intent(context, clazz)
        intent.putExtra("id_note", list[position].id)
        context.startActivity(intent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return if (viewType == TYPE_NOTE) {
            val binding = DataBindingUtil.inflate<ItemMainMenuBinding>(
                    inflater, R.layout.item_main_menu, parent, false
            )
            NoteViewHolder(binding, context)

        } else {
            val binding = DataBindingUtil.inflate<ItemGroupMenuBinding>(
                    inflater, R.layout.item_group_menu, parent, false
            )
            GroupViewHolder(binding, context)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == TYPE_NOTE) {
            (holder as NoteViewHolder).setNoteDetails(notesList, position)
            holder.cardClickListener(notesList, position)
            holder.cardLongClickListener(notesList as MutableList<MainMenuNote>, position)
        } else {
            (holder as GroupViewHolder).setNoteDetails(notesList, position)
            holder.cardClickListener(notesList, position)
            holder.cardLongClickListener(notesList as MutableList<MainMenuNote>, position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (notesList[position].group) {
            TYPE_GROUP
        } else {
            TYPE_NOTE
        }
    }

    override fun getItemCount(): Int {
        return notesList.size
    }
}