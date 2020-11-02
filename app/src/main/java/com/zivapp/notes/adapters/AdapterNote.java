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
import com.zivapp.notes.databinding.ItemNotesBinding;
import com.zivapp.notes.model.FormatSum;
import com.zivapp.notes.model.Note;
import com.zivapp.notes.util.UtilConverter;
import com.zivapp.notes.views.notes.NoteActivity;

import java.util.ArrayList;

/**
 * Adapter used for the {@link NoteActivity}.
 */
public class AdapterNote extends RecyclerView.Adapter<AdapterNote.ItemViewHolder> {

    private static final String TAG = "AdapterNote";

    private ArrayList<Note> list;
    private Activity activity;

    public AdapterNote(ArrayList<Note> list, Activity activity) {
        this.list = list;
        this.activity = activity;
    }

    public void setNoteList(ArrayList<Note> list) {
        this.list = list;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        private ItemNotesBinding binding;

        public ItemViewHolder(@NonNull ItemNotesBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemNotesBinding binding = DataBindingUtil.inflate(inflater,
                R.layout.item_notes, parent, false);
        return new ItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, final int position) {
        final Note note = list.get(position);
        holder.binding.setNote(note);

        String formated_sum = UtilConverter.customStringFormat(list.get(position).getSum());
        holder.binding.setFormat(new FormatSum(formated_sum));

        // TODO: one click for change settings
        holder.binding.cardViewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "POSITION ITEM: " + position + " and id: " + list.get(position).getUid());

//                final EditNoteBinding dialogBinding = DataBindingUtil.setContentView(activity, R.layout.dialog_edit_note);
//                final EditText nameEditText = dialogBinding.changeName;
//                final EditText sumEditText = dialogBinding.changeSum;
//                dialogBinding.setNote(new Note(note.getName(), note.getSum()));
//
//                DialogDeleteNote.onCreateDialogBuilder(activity)
//                        .setNegativeButton("Cancel", null)
//                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//
//                                String name = nameEditText.getText().toString().trim();
//                                int sum = Integer.parseInt(sumEditText.getText().toString().trim());
//
//                                list.remove(position);
//                                list.add(position, new Note(note.getId(), name, sum, note.getId_note()));
//                                notifyItemChanged(position);
//                                mRepoCreation.update(new Note(note.getId(), name, sum, note.getId_note()));
//                                Log.v(TAG, "Data Updated: name: " + name + " sum: " + sum + " and id: " + list.get(position).getId());
//
//                            }
//                        }).show();
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

//                                mRepoCreation.deleteNote(list.get(position));
                                FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                // TODO: add an message id to remove one item
                                databaseReference.child("Notes")
                                        .child(mUser.getUid())
                                        .child(list.get(position).getId_note())
                                        .child(list.get(position).getUid())
                                        .removeValue();

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