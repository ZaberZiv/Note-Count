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
import com.zivapp.countandnote.databinding.ItemGroupMembersBinding
import com.zivapp.countandnote.databinding.ItemGroupNoteBinding
import com.zivapp.countandnote.firebase.FirebaseHelper
import com.zivapp.countandnote.model.FormatSum
import com.zivapp.countandnote.model.GroupNote
import com.zivapp.countandnote.util.UtilConverter
import com.zivapp.countandnote.view.groups.GroupIDProvider
import java.util.*

class AdapterGroupNote(val groupIDProvider: GroupIDProvider) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TAG = "AdapterGroupNote"
    private val TYPE_USER = 1
    private val TYPE_MEMBER = 2

    var list: List<GroupNote> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class GroupNoteViewHolder(private val binding: ItemGroupNoteBinding,
                                    val context: Context) :
            RecyclerView.ViewHolder(binding.root) {
        private val TAG = "AdapterGroupNote -> GroupNoteViewHolder"

        fun setUserDetails(note: GroupNote) {
            // setting member and message in XML (item_group_note)
            binding.includeGroupChat.note = note
            // setting formatted sum in XML (item_group_note)
            val formattedSum: String = UtilConverter.customStringFormat(note.sum)
            binding.includeGroupChat.format = FormatSum(formattedSum)
        }

        //        private void editTextJob(GroupNote note) {
        //            String message = binding.includeGroupChatItem.etNameOperation.getText().toString().trim();
        //            String price = binding.etPriceOperation.getText().toString().trim();
        //
        //            GroupNote gNote = new GroupNote();
        //            gNote.setMessage(message);
        //            if (price.equals("")) {
        //                gNote.setSum(0);
        //            } else {
        //                Log.v(TAG, "price before format: " + price);
        //                String text = price.replace(" ", "");
        //                Log.v(TAG, "price after format: " + text);
        //
        //                gNote.setSum(Integer.parseInt(text));
        //            }
        //
        //            Log.v(TAG, "Uid: " + note.getUid());
        //            Log.v(TAG, "id_note: " + note.getId_note());
        //            Log.v(TAG, "getGroup_id: " + note.getGroup_id());
        //
        //            new FirebaseHelper()
        //                    .getGroupNoteReference(note.getGroup_id())
        //                    .child(note.getId_note())
        //                    .setValue(gNote);
        //        }
        // Removing item data from recyclerView and firebase
        fun alert(list: MutableList<GroupNote>, position: Int) {
            binding.cardViewItem.setOnLongClickListener {
                AlertDialog.Builder(context)
                        .setIcon(android.R.drawable.ic_notification_clear_all)
                        .setTitle(R.string.task_title)
                        .setMessage(R.string.task_message)
                        .setPositiveButton(R.string.task_positive_btn) { dialog, which ->
                            Log.v(TAG, "REMOVING NOTE - id_note: " +
                                    "${list[position].id_note}, " +
                                    "Uid: ${list[position].Uid}")

                            val ID: String = groupIDProvider.getCurrentNoteID()
                            FirebaseHelper().deleteGroupItemFromFirebase(list, position, ID)
                            list.removeAt(position)
                            notifyItemRemoved(position)
                            Toast.makeText(context, R.string.task_toast, Toast.LENGTH_SHORT).show()
                        }
                        .setNegativeButton(R.string.task_negative_btn, null)
                        .show()
                false
            }
        }
    }

    class MemberViewHolder(private val binding: ItemGroupMembersBinding) :
            RecyclerView.ViewHolder(binding.root) {

        fun setMembersDetails(note: GroupNote) {
            // setting member and message in XML (item_group_members)
            binding.includeGroupChat.note = note
            // setting formatted sum in XML (item_group_members)
            val formated_sum: String = UtilConverter.customStringFormat(note.sum)
            binding.includeGroupChat.format = FormatSum(formated_sum)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return if (viewType == TYPE_USER) { // for user layout
            val binding: ItemGroupNoteBinding = DataBindingUtil.inflate(inflater,
                    R.layout.item_group_note, parent, false)
            GroupNoteViewHolder(binding, parent.context)
        } else { // for member layout
            val binding: ItemGroupMembersBinding = DataBindingUtil.inflate(inflater,
                    R.layout.item_group_members, parent, false)
            MemberViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == TYPE_USER) {
//            ((ItemViewHolder) holder).editTextJob(list.get(position));
            (holder as GroupNoteViewHolder).setUserDetails(list[position])
            holder.alert(list as MutableList<GroupNote>, position)
        } else {
            (holder as MemberViewHolder).setMembersDetails(list[position])
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (list[position].Uid == FirebaseHelper().getFirebaseUser().uid) {
            TYPE_USER
        } else {
            TYPE_MEMBER
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}