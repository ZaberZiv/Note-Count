package com.zivapp.notes.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zivapp.notes.model.MainMenuNote;
import com.zivapp.notes.model.Note;

import java.util.ArrayList;

public class FirebaseHelper {
    private static final String TAG = "FirebaseHelper";

    /**
    * Base methods
     */
    public FirebaseDatabase getFirebaseDatabase() {
        return FirebaseDatabase.getInstance();
    }

    public DatabaseReference getDatabaseReference() {
        return getFirebaseDatabase().getReference();
    }

    public FirebaseUser getFirebaseUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    /**
     * Branch methods
     */
    public DatabaseReference getNotesReference() {
        return getDatabaseReference().child("Notes").child(getFirebaseUser().getUid());
    }

    public DatabaseReference getTotalDataReference(String id) {
        return getDatabaseReference().child("Total Data").child(id);
    }

    public DatabaseReference getTotalDataReference() {
        return getDatabaseReference().child("Total Data").child(getFirebaseUser().getUid());
    }


    public DatabaseReference getGroupsReference() {
        return getDatabaseReference().child("Groups");
    }

    public DatabaseReference getUsersReference() {
        return getDatabaseReference().child("users");
    }

    /**
     * Removing data methods
     */
    // Removing data from Firebase in MenuNotesActivity
    public void deleteMenuNoteFromFirebase(ArrayList<MainMenuNote> list, int position) {
        getTotalDataReference(getFirebaseUser().getUid())
                .child(list.get(position).getId())
                .removeValue();

        getNotesReference()
                .child(list.get(position).getId())
                .removeValue();
    }
    // Removing data from Firebase in NoteActivity
    public void deleteNoteFromFirebase(ArrayList<Note> list, int position) {
        getNotesReference()
                .child(list.get(position).getId_note())
                .child(list.get(position).getUid())
                .removeValue();
    }
}