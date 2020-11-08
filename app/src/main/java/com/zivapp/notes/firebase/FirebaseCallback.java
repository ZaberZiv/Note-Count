package com.zivapp.notes.firebase;

import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.zivapp.notes.model.FormatSum;
import com.zivapp.notes.model.MainMenuNote;
import com.zivapp.notes.model.Note;
import com.zivapp.notes.views.mainmenu.MenuContract;
import com.zivapp.notes.views.notes.NoteContract;

import java.util.ArrayList;

public class FirebaseCallback {

    private static final String TAG = "FirebaseCallback";

    private NoteContract.Firebase firebaseInterface;
    private MenuContract.Firebase menuFirebaseInterface;
    private MenuContract.Presenter menuPresenterInterface;

    public FirebaseCallback(NoteContract.Firebase firebaseInterface) {
        this.firebaseInterface = firebaseInterface;
    }

    public FirebaseCallback(MenuContract.Firebase menuFirebaseInterface, MenuContract.Presenter menuPresenterInterface) {
        this.menuFirebaseInterface = menuFirebaseInterface;
        this.menuPresenterInterface = menuPresenterInterface;
    }

    /**
     * Get all messages of this Note from firebase
     */
    public ArrayList<Note> getDataFromFirebase(DatabaseReference reference) {
        final ArrayList<Note> noteList = new ArrayList<>();

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                noteList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Note notes = new Note();
                    notes.setUid(snapshot.getKey());
                    notes.setMessage(snapshot.getValue(Note.class).getMessage());
                    notes.setSum(snapshot.getValue(Note.class).getSum());
                    notes.setId_note(snapshot.getValue(Note.class).getId_note());
                    noteList.add(notes);
                }
                firebaseInterface.updateNoteUI(noteList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        reference.addValueEventListener(postListener);
        return noteList;
    }

    /**
     * Get title of this Note from firebase
     */
    public MainMenuNote getTotalDataFromFirebase(DatabaseReference reference) {
        final MainMenuNote mainMenuNote = new MainMenuNote();

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mainMenuNote.setTitle(dataSnapshot.getValue(MainMenuNote.class).getTitle());
                mainMenuNote.setTotal_sum(dataSnapshot.getValue(MainMenuNote.class).getTotal_sum());
                mainMenuNote.setDate(dataSnapshot.getValue(MainMenuNote.class).getDate());
                mainMenuNote.setId(dataSnapshot.getValue(MainMenuNote.class).getId());

                firebaseInterface.workWithTotalData(mainMenuNote);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        reference.addValueEventListener(postListener);
        return mainMenuNote;
    }

    public ArrayList<MainMenuNote> getMenuNoteDataFromFirebase(DatabaseReference reference) {
        menuPresenterInterface.progressbarOn();

        final ArrayList<MainMenuNote> list = new ArrayList<>();

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MainMenuNote mainMenuNote = new MainMenuNote();
                    mainMenuNote.setTitle(snapshot.getValue(MainMenuNote.class).getTitle());
                    mainMenuNote.setTotal_sum(snapshot.getValue(MainMenuNote.class).getTotal_sum());
                    mainMenuNote.setDate(snapshot.getValue(MainMenuNote.class).getDate());
                    mainMenuNote.setId(snapshot.getValue(MainMenuNote.class).getId());
                    mainMenuNote.setGroup(snapshot.getValue(MainMenuNote.class).isGroup());
                    list.add(mainMenuNote);
                }

                menuFirebaseInterface.updateNoteUI(list);
                menuPresenterInterface.progressbarOff();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Object failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        reference.addValueEventListener(postListener);

        return list;
    }
}
