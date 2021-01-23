package com.zivapp.notes.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;

import com.zivapp.notes.R;
import com.zivapp.notes.firebase.FirebaseHelper;
import com.zivapp.notes.model.MainMenuNote;
import com.zivapp.notes.util.UtilConverter;
import com.zivapp.notes.views.groupnotes.GroupNoteActivity;
import com.zivapp.notes.views.notes.NoteActivity;

import java.util.ArrayList;

public class MenuItemsBinding {
    private static final String TAG = "MenuItemsBinding";

    // Binding data to XML file: item_main_menu.xml
    MainMenuNote getMainMenuNote(ArrayList<MainMenuNote> list, int position) {
        return list.get(position);
    }
    // If User didn't set title name the "New note" will be added by default
    String getDefaultName(int position) {
        return "New Note " + (position + 1);
    }
    // Binding formated total sum to XML file: item_main_menu.xml
    String getFormatTotalSum(ArrayList<MainMenuNote> list, int position) {
        return UtilConverter.customStringFormat(list.get(position).getTotal_sum());
    }

    void cardClickListener(CardView cardView,
                           final Context context,
                           final ArrayList<MainMenuNote> list,
                           final int position) {
        // Opening new Activity (group note or single note) and send ID
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (list.get(position).isGroup()) {
                    Log.v(TAG, "Opening GroupNoteActivity and send ID: "
                            + list.get(position).getId());

                    openNewActivity(GroupNoteActivity.class, context, list, position);
                } else {
                    Log.v(TAG, "Opening NoteActivity and send ID: "
                            + list.get(position).getId());

                    openNewActivity(NoteActivity.class, context, list, position);
                }
            }
        });
    }

    void cardLongClickListener(CardView cardView,
                               final Context context,
                               final AdapterMenu adapter,
                               final ArrayList<MainMenuNote> list,
                               final int position) {
        // Removing item data from RecyclerView and Firebase;
        cardView.setOnLongClickListener(new View.OnLongClickListener() {
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

                                FirebaseHelper firebaseHelper = new FirebaseHelper();
                                firebaseHelper.deleteMenuNoteFromFirebase(list, position);
                                firebaseHelper.deleteGroupNoteFromFirebase(list, position);

                                list.remove(position);
                                adapter.notifyItemRemoved(position);
                                showToast(context);
                            }
                        })
                        .setNegativeButton(R.string.note_negative_btn, null)
                        .show();

                return false;
            }
        });
    }

    private void showToast(Context context) {
        Toast.makeText(context, R.string.note_toast, Toast.LENGTH_SHORT).show();
    }

    private void openNewActivity(Class clazz, Context context, ArrayList<MainMenuNote> list, int position) {
        Intent intent = new Intent(context, clazz);
        intent.putExtra("id_note", list.get(position).getId());
        context.startActivity(intent);
    }
}
