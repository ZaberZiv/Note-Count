package com.zivapp.notes.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zivapp.notes.model.GroupNote;
import com.zivapp.notes.model.MainMenuNote;
import com.zivapp.notes.model.Note;

import java.util.ArrayList;

public class FirebaseHelper {

    private static final String TAG = "FirebaseHelper";

    private static final String USERS = "Users";
    private static final String NOTES = "Notes";
    private static final String TOTAL_DATA = "Total Data";
    private static final String GROUPS = "Groups";
    private static final String GROUPS_MESSAGES = "messages";
    private static final String GROUPS_MEMBERS = "members";
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
    // Get all notes reference of current user
    public DatabaseReference getNotesReference() {
        return getDatabaseReference().child(NOTES).child(getFirebaseUser().getUid());
    }
    // Get current Note reference of current user
    public DatabaseReference getCurrentNoteReference(String id) {
        return getNotesReference().child(id);
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
        return getTotalDataReference().child(getFirebaseUser().getUid());
    }
    // Get total data reference of current note of current User
    public DatabaseReference getTotalDataRefCurrentNote(String id) {
        return getTotalDataRefCurrentUser().child(id);
    }
    // Saving data of current user to the branch "Total Data" -> note id -> data
    public void saveTotalDataCurrentUser(String id, MainMenuNote mainMenuNote) {
        getTotalDataRefCurrentNote(id).setValue(mainMenuNote);
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
    // Get "Notes" from "Groups" -> group id -> "messages"
    public DatabaseReference getGroupNoteReference(String id) {
      return getGroupReferenceByID(id).child(GROUPS_MESSAGES);
    }
    // Get "members" from "Groups" -> group id -> "members"
    public DatabaseReference getGroupMembersDataReference(String id) {
        return getGroupReferenceByID(id).child(GROUPS_MEMBERS);
    }
    // Get "members" from "Groups" -> group id -> "members" -> user id
    public DatabaseReference getGroupMembersReference(DatabaseReference reference, String id) {
        return reference.child(GROUPS_MEMBERS).child(id);
    }


    // "Users" branch
    public DatabaseReference getUsersReference() {
        return getDatabaseReference().child(USERS);
    }

    public DatabaseReference getCurrentUserReferenceByID() {
        return getUsersReference().child(getFirebaseUser().getUid());
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
        getTotalDataRefCurrentUser()
                .child(list.get(position).getId())
                .removeValue();

        getNotesReference()
                .child(list.get(position).getId())
                .removeValue();
    }
    // Removing Group data from Firebase in MenuNotesActivity
    public void deleteGroupNoteFromFirebase(ArrayList<MainMenuNote> list, int position) {
        getTotalDataRefCurrentUser()
                .child(list.get(position).getId())
                .removeValue();

        getGroupReferenceByID(list.get(position).getId())
                .child(GROUPS_MEMBERS)
                .child(getFirebaseUser().getUid())
                .removeValue();
    }
    // Removing item data from Firebase in NoteActivity
    public void deleteNoteFromFirebase(ArrayList<Note> list, int position) {
        getNotesReference()
                .child(list.get(position).getId_note())
                .child(list.get(position).getUid())
                .removeValue();
    }
    // Removing item data from Firebase in GroupNoteActivity
    public void deleteGroupItemFromFirebase(ArrayList<GroupNote> list, int position, String id) {
        getGroupNoteReference(id)
                .child(list.get(position).getId_note())
                .removeValue();
    }
}