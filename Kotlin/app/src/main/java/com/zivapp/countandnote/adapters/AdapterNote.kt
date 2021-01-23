package com.zivapp.countandnote.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.zivapp.countandnote.R
import com.zivapp.countandnote.databinding.ItemNotesBinding
import com.zivapp.countandnote.firebase.FirebaseHelper
import com.zivapp.countandnote.model.FormatSum
import com.zivapp.countandnote.model.Note
import com.zivapp.countandnote.util.UtilConverter

class AdapterNote : RecyclerView.Adapter<AdapterNote.ItemViewHolder>() {

    private val TAG = "AdapterNote"
    var list = listOf<Note>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class ItemViewHolder(private val binding: ItemNotesBinding, val context: Context)
        : RecyclerView.ViewHolder(binding.root) {
        private val TAG = "AdapterNote -> ItemViewHolder"

        fun setData(list: MutableList<Note>, position: Int) {
            val note = list[position]
            binding.note = note

            // Setting format sum into recycler item
            val formattedSum: String = UtilConverter.customStringFormat(note.sum)
            binding.format = FormatSum(formattedSum)

            // TODO: one click to change settings (name, sum)
            binding.cardViewItem.setOnClickListener {
                Log.v(TAG, "POSITION ITEM: " + position + " and id: " + note.Uid)
            }

            // Removing item data from RecyclerView and Firebase;
            binding.cardViewItem.setOnLongClickListener {
                AlertDialog.Builder(context)
                        .setIcon(android.R.drawable.ic_notification_clear_all)
                        .setTitle(R.string.task_title)
                        .setMessage(R.string.task_message)
                        .setPositiveButton(R.string.task_positive_btn) {
                            dialog, which ->

                            Log.v(TAG, "REMOVING NOTE: " + list[position]
                                    + " Uid: " + list[position].Uid)

                            FirebaseHelper().deleteNoteFromFirebase(list, position)
                            list.removeAt(position)
                            notifyItemRemoved(position)
                            showToast()
                        }
                        .setNegativeButton(R.string.task_negative_btn, null)
                        .show()
                false
            }
        }

        private fun showToast() {
            Toast.makeText(context, R.string.task_toast, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ItemNotesBinding>(
                inflater,
                R.layout.item_notes, parent, false
        )
        return ItemViewHolder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.setData(list as MutableList<Note>, position)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}