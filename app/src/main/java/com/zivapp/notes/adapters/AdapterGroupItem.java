package com.zivapp.notes.adapters;

import android.app.Activity;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zivapp.notes.R;
import com.zivapp.notes.databinding.ItemGroupMembersBinding;
import com.zivapp.notes.firebase.FirebaseHelper;
import com.zivapp.notes.model.GroupNote;
import com.zivapp.notes.databinding.ItemGroupNoteBinding;
import com.zivapp.notes.model.FormatSum;
import com.zivapp.notes.model.User;
import com.zivapp.notes.util.UtilConverter;
import com.zivapp.notes.util.UtilDate;
import com.zivapp.notes.views.groupnotes.GroupContract;
import com.zivapp.notes.views.groupnotes.GroupNoteActivity;

import java.util.ArrayList;

/**
 * Adapter used for the {@link GroupNoteActivity}.
 */
public class AdapterGroupItem extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "AdapterGroupItem";
    private static final int TYPE_USER = 1;
    private static final int TYPE_MEMBER = 2;
    private ArrayList<GroupNote> list;
    private final Activity activity;
    private final GroupContract.Adapter groupContract;

    public AdapterGroupItem(ArrayList<GroupNote> list, Activity activity, GroupContract.Adapter groupContract) {
        this.list = list;
        this.activity = activity;
        this.groupContract = groupContract;
    }

    public void setNoteList(ArrayList<GroupNote> list) {
        this.list = list;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        private final ItemGroupNoteBinding binding;

        public ItemViewHolder(@NonNull ItemGroupNoteBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        private void setUserDetails(GroupNote note) {
            // setting member and message in XML (item_group_note)
            binding.setNote(note);
            // setting formated sum in XML (item_group_note)
            String formated_sum = UtilConverter.customStringFormat(note.getSum());
            binding.setFormat(new FormatSum(formated_sum));
        }

        private void editTextJob(GroupNote note) {
            String message = binding.etNameOperation.getText().toString().trim();
            String price = binding.etPriceOperation.getText().toString().trim();

            GroupNote gNote = new GroupNote();
            gNote.setMessage(message);
            if (price.equals("")) {
                gNote.setSum(0);
            } else {
                Log.v(TAG, "price before format: " + price);
                String text = price.replace(" ", "");
                Log.v(TAG, "price after format: " + text);

                gNote.setSum(Integer.parseInt(text));
            }

            Log.v(TAG, "Uid: " + note.getUid());
            Log.v(TAG, "id_note: " + note.getId_note());
            Log.v(TAG, "getGroup_id: " + note.getGroup_id());

            new FirebaseHelper()
                    .getGroupNoteReference(note.getGroup_id())
                    .child(note.getId_note())
                    .setValue(gNote);
        }

        // Removing item data from recyclerView and firebase
        public void alert(final ArrayList<GroupNote> list, final int position) {
            binding.cardViewItem.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    new AlertDialog.Builder(activity)
                            .setIcon(android.R.drawable.ic_notification_clear_all)
                            .setTitle(R.string.task_title)
                            .setMessage(R.string.task_message)
                            .setPositiveButton(R.string.task_positive_btn, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.v(TAG, "REMOVING NOTE - id_note: " + list.get(position).getId_note()
                                            + " Uid: " + list.get(position).getUid());

                                    String ID = groupContract.getCurrentNoteID();
                                    new FirebaseHelper().deleteGroupItemFromFirebase(list, position, ID);

                                    list.remove(position);
                                    notifyItemRemoved(position);
                                    Toast.makeText(activity, R.string.task_toast, Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton(R.string.task_negative_btn, null)
                            .show();

                    return false;
                }
            });
        }
    }

    public static class MemberViewHolder extends RecyclerView.ViewHolder {

        private final ItemGroupMembersBinding binding;

        public MemberViewHolder(@NonNull ItemGroupMembersBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        private void setMembersDetails(GroupNote note) {
            // setting member and message in XML (item_group_members)
            binding.setNote(note);
            // setting formated sum in XML (item_group_members)
            String formated_sum = UtilConverter.customStringFormat(note.getSum());
            binding.setFormat(new FormatSum(formated_sum));
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_USER) { // for user layout
            ItemGroupNoteBinding binding = DataBindingUtil.inflate(inflater,
                    R.layout.item_group_note, parent, false);
            return new ItemViewHolder(binding);

        } else { // for member layout
            ItemGroupMembersBinding binding = DataBindingUtil.inflate(inflater,
                    R.layout.item_group_members, parent, false);
            return new MemberViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        if (getItemViewType(position) == TYPE_USER) {
//            ((ItemViewHolder) holder).editTextJob(list.get(position));
            ((ItemViewHolder) holder).setUserDetails(list.get(position));
            ((ItemViewHolder) holder).alert(list, position);
        } else {
            ((MemberViewHolder) holder).setMembersDetails(list.get(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        Log.v(TAG, "getUid(): " + list.get(position).getUid());
        Log.v(TAG, "getFirebaseUser(): " + new FirebaseHelper().getFirebaseUser().getUid());

        if (list.get(position).getUid().equals(new FirebaseHelper().getFirebaseUser().getUid())) {
            return TYPE_USER;
        } else {
            return TYPE_MEMBER;
        }
    }

    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size();
        } else {
            return 0;
        }
    }
}