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

    private static final String USERS = "users";
    private static final String GROUPS = "Groups";
    private static final String NOTES = "Notes";
    private static final String TOTAL_DATA = "Total Data";
    private static final String MEMBERS = "members";
    private static final String GROUP = "Group";
    private static final String USER_NAME = "name";
    private static final String USER_EMAIL = "email";
    private static final String USER_PHONE = "phone";

    /**
    * Base methods
     */
    public FirebaseDatabase getFirebaseDatabase() {
        return FirebaseDatabase.getInstance();
    }

    public DatabaseReference getDatabaseReference() {
        return getFirebaseDatabase().getReference();
    }

    public FirebaseAuth getFirebaseAuth() {
        return FirebaseAuth.getInstance();
    }

    public FirebaseUser getFirebaseUser() {
        return getFirebaseAuth().getCurrentUser();
    }


    /**
     * Branch methods
     */
    // "Notes" branch
    public DatabaseReference getNotesReference() {
        return getDatabaseReference().child(NOTES).child(getFirebaseUser().getUid());
    }


    // "Total Data" branch
    public DatabaseReference getTotalDataReference() {
        return getDatabaseReference().child(TOTAL_DATA);
    }
    // Get specific total data by ID from "Total Data" -> id
    public DatabaseReference getTotalDataReferenceByID(String id) {
        return getTotalDataReference().child(id);
    }
    // "Total Data" reference of current User
    public DatabaseReference getTotalDataRefCurrentUser() {
        return getDatabaseReference().child(TOTAL_DATA).child(getFirebaseUser().getUid());
    }
    // Saving data of current user to the branch "Total Data" -> note id -> data
    public void saveTotalDataCurrentUser(String id, MainMenuNote mainMenuNote) {
        getTotalDataRefCurrentUser().child(id).setValue(mainMenuNote);
    }
    // Saving data of members to the branch "Total Data" -> member id -> note id -> data
    public void saveTotalDataMembers(String uID, String childID, MainMenuNote mainMenuNote) {
        getTotalDataReferenceByID(uID).child(childID).setValue(mainMenuNote);
    }


    // "Groups" branch
    // Get all groups form "Groups"
    public DatabaseReference getGroupsReference() {
        return getDatabaseReference().child(GROUPS);
    }
    // Get specific group by ID from "Groups" -> group id
    public DatabaseReference getGroupReferenceByID(String id) {
       return getGroupsReference().child(id);
    }
    // Get "Notes" from "Groups" -> group id -> "Notes"
    public DatabaseReference getGroupNoteReference(String id) {
      return getGroupReferenceByID(id).child(NOTES);
    }
    // Get "Total Data" from "Groups" -> group id -> "Total Data"
    public DatabaseReference getGroupTotalDataReference(String id) {
        return getGroupReferenceByID(id).child(TOTAL_DATA);
    }
    // Get "members" from "Groups" -> group id -> "members" -> user id
    public DatabaseReference getGroupMembersReference(DatabaseReference reference, String id) {
        return reference.child(MEMBERS).child(id);
    }
    // Add current user to the "members" child
    public DatabaseReference getGroupCurrentMemberReference(String id) {
        return getGroupsReference().child(MEMBERS).child(id);
    }


    // "users" branch
    public DatabaseReference getUsersReference() {
        return getDatabaseReference().child(USERS);
    }

    public DatabaseReference getCurrentUserReferenceByID() {
        return getUsersReference().child(getFirebaseUser().getUid());
    }

    public DatabaseReference getUsersGroupReference(String uID) {
       return getUsersReference().child(uID).child(GROUP);
    }

    public DatabaseReference getUserNameReference(String uID) {
        return getUsersReference().child(uID).child(USER_NAME);
    }
    public DatabaseReference getUserEmailReference(String uID) {
        return getUsersReference().child(uID).child(USER_EMAIL);
    }
    public DatabaseReference getUserPhoneReference(String uID) {
        return getUsersReference().child(uID).child(USER_PHONE);
    }


    /**
     * Removing data methods
     */
    // Removing data from Firebase in MenuNotesActivity
    public void deleteMenuNoteFromFirebase(ArrayList<MainMenuNote> list, int position) {
        getTotalDataReferenceByID(getFirebaseUser().getUid())
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