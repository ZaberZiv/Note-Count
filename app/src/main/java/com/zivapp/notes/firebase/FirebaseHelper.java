package com.zivapp.notes.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseHelper {
    private static final String TAG = "FirebaseHelper";

    public FirebaseDatabase getFirebaseDatabase() {
        return FirebaseDatabase.getInstance();
    }

    public FirebaseUser getFirebaseUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public DatabaseReference getDatabaseReference() {
        return getFirebaseDatabase().getReference();
    }

    public DatabaseReference getNotesReference() {
        return getDatabaseReference().child("Notes").child(getFirebaseUser().getUid());
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
}