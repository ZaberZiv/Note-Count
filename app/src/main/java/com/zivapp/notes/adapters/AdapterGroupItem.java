package com.zivapp.notes.adapters;

import android.app.Activity;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zivapp.notes.R;
import com.zivapp.notes.model.GroupNote;
import com.zivapp.notes.databinding.ItemGroupNoteBinding;
import com.zivapp.notes.model.FormatSum;
import com.zivapp.notes.util.UtilConverter;
import com.zivapp.notes.views.groupnotes.GroupNoteActivity;

import java.util.ArrayList;

/**
 * Adapter used for the {@link GroupNoteActivity}.
 */
public class AdapterGroupItem extends RecyclerView.Adapter<AdapterGroupItem.ItemViewHolder> {

    private static final String TAG = "AdapterGroupItem";
    private ArrayList<GroupNote> list;
    private Activity activity;

    public AdapterGroupItem(ArrayList<GroupNote> list, Activity activity) {
        this.list = list;
        this.activity = activity;
    }

    public void setNoteList(ArrayList<GroupNote> list) {
        this.list = list;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        private ItemGroupNoteBinding binding;

        public ItemViewHolder(@NonNull ItemGroupNoteBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemGroupNoteBinding binding = DataBindingUtil.inflate(inflater,
                R.layout.item_group_note, parent, false);
        return new ItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, final int position) {
        // setting member and message in XML (item_group_note)
        final GroupNote note = list.get(position);
        holder.binding.setNote(note);
        // setting formated sum in XML (item_group_note)
        String formated_sum = UtilConverter.customStringFormat(list.get(position).getSum());
        holder.binding.setFormat(new FormatSum(formated_sum));

        // TODO: one click for change settings
        holder.binding.cardViewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "POSITION ITEM: " + position + " and id: " + list.get(position).getUid());

            }
        });

        // Removing item data from recyclerView and table (notes_table);
        holder.binding.cardViewItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                new AlertDialog.Builder(activity)
                        .setIcon(android.R.drawable.ic_notification_clear_all)
                        .setTitle(R.string.task_title)
                        .setMessage(R.string.task_message)
                        .setPositiveButton(R.string.task_positive_btn, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.v(TAG, "REMOVING NOTE: " + list.get(position)
                                        + " Uid: " + list.get(position).getUid());

                                FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                // TODO: add an message id to remove one item
//                                databaseReference.child("Notes")
//                                        .child(mUser.getUid())
//                                        .child(list.get(position).getId_note())
//                                        .child(list.get(position).getUid())
//                                        .removeValue();

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

    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size();
        } else {
            return 0;
        }
    }
}