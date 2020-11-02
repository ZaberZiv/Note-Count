package com.zivapp.notes.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.zivapp.notes.databinding.ItemMainMenuBinding;
import com.zivapp.notes.model.FormatSum;
import com.zivapp.notes.model.MainMenuNote;
import com.zivapp.notes.model.Note;
import com.zivapp.notes.util.UtilConverter;
import com.zivapp.notes.views.groupnotes.GroupNoteActivity;
import com.zivapp.notes.views.notes.NoteActivity;
import com.zivapp.notes.views.mainmenu.MenuNotesActivity;

import java.util.ArrayList;

/**
 * A ViewModel used for the {@link MenuNotesActivity}.
 */
public class AdapterMenu extends RecyclerView.Adapter<AdapterMenu.NoteViewHolder> {

    private static final String TAG = "AdapterMenu";

    private ArrayList<MainMenuNote> list;
    private Context context;

    public AdapterMenu(ArrayList<MainMenuNote> list, Context context) {
        this.list = list;
        this.context = context;
    }

    public void setNoteList(ArrayList<MainMenuNote> list) {
        this.list = list;
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {

        private ItemMainMenuBinding binding;

        public NoteViewHolder(@NonNull ItemMainMenuBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemMainMenuBinding binding = DataBindingUtil.inflate(inflater,
                R.layout.item_main_menu, parent, false);

        return new NoteViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final NoteViewHolder holder, final int position) {
        // Binding data to XML file: item_main_menu.xml
        MainMenuNote note = list.get(position);
        holder.binding.setNote(note);

        // If User didn't set title name the "New note" will be added by default
        String name = "New Note " + list.size();
        holder.binding.setItem(new Note(name));

        // Binding formated total sum to XML file: item_main_menu.xml
        String formated_total_sum = UtilConverter.customStringFormat(list.get(position).getTotal_sum());
        holder.binding.setFormat(new FormatSum(formated_total_sum));

        // Opening CreationActivity and send ID
        holder.binding.cardViewNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "Opening NoteActivity and send ID_note: " + list.get(position).getId());

                if (list.get(position).isGroup()) {
                    Intent intent = new Intent(context, GroupNoteActivity.class);
                    intent.putExtra("id_note", list.get(position).getId());
                    context.startActivity(intent);
                } else {
                    Intent intent = new Intent(context, NoteActivity.class);
                    intent.putExtra("id_note", list.get(position).getId());
                    context.startActivity(intent);
                }
            }
        });

        // Removing item data from recyclerView and both tables (notes_table and menu_table);
        holder.binding.cardViewNote.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                new AlertDialog.Builder(context)
                        .setIcon(android.R.drawable.ic_notification_clear_all)
                        .setTitle(R.string.note_title)
                        .setMessage(R.string.note_message)
                        .setPositiveButton(R.string.note_positive_btn, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.v(TAG, "REMOVING NOTE: "
                                        + list.get(position).getTitle()
                                        + " ID_note: " + list.get(position).getId());

                                FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                databaseReference.child("Total Data").child(mUser.getUid()).child(list.get(position).getId()).removeValue();
                                databaseReference.child("Notes").child(mUser.getUid()).child(list.get(position).getId()).removeValue();

                                list.remove(position);
                                notifyItemRemoved(position);

                                Toast.makeText(context, R.string.note_toast, Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton(R.string.note_negative_btn, null)
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