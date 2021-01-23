package com.zivapp.countandnote.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.zivapp.countandnote.model.GroupNote
import com.zivapp.countandnote.model.MainMenuNote
import com.zivapp.countandnote.model.Note

class FirebaseHelper {

    companion object {
        private const val USERS = "Users"
        private const val NOTES = "Notes"
        private const val TOTAL_DATA = "Total Data"
        private const val GROUPS = "Groups"
        private const val GROUPS_MESSAGES = "messages"
        private const val GROUPS_MEMBERS = "members"
        private const val USER_NAME = "name"
        private const val USER_EMAIL = "email"
        private const val USER_PHONE = "phone"
    }

    /**
     * Base methods
     */
    fun getFirebaseDatabase(): FirebaseDatabase {
        return FirebaseDatabase.getInstance()
    }

    fun getDatabaseReference(): DatabaseReference {
        return getFirebaseDatabase().reference
    }

    fun getFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    fun getFirebaseUser(): FirebaseUser {
        return getFirebaseAuth().currentUser!!
    }

    /**
     * Branch methods
     */
    // "Notes" branch
    // Get all notes reference of current user
    fun getNotesReference(): DatabaseReference {
        return getDatabaseReference().child(NOTES).child(getFirebaseUser().uid)
    }

    // Get current Note reference of current user
    fun getCurrentNoteReference(id: String): DatabaseReference {
        return getNotesReference().child(id)
    }

    // "Total Data" branch
    fun getTotalDataReference(): DatabaseReference {
        return getDatabaseReference().child(TOTAL_DATA)
    }

    // Get specific total data by ID from "Total Data" -> id
    fun getTotalDataReferenceByID(id: String): DatabaseReference {
        return getTotalDataReference().child(id)
    }

    // "Total Data" reference of current User
    fun getTotalDataRefCurrentUser(): DatabaseReference {
        return getTotalDataReference().child(getFirebaseUser().uid)
    }

    // Get total data reference of current note of current User
    fun getTotalDataRefCurrentNote(id: String): DatabaseReference {
        return getTotalDataRefCurrentUser().child(id)
    }

    // Saving data of current user to the branch "Total Data" -> note id -> data
    fun saveTotalDataCurrentUser(id: String, mainMenuNote: MainMenuNote) {
        getTotalDataRefCurrentNote(id).setValue(mainMenuNote)
    }

    // Saving data of members to the branch "Total Data" -> member id -> note id -> data
    fun saveTotalDataMembers(uID: String, childID: String, mainMenuNote: MainMenuNote) {
        getTotalDataReferenceByID(uID).child(childID).setValue(mainMenuNote)
    }


    // "Groups" branch
    // Get all groups form "Groups"
    fun getGroupsReference(): DatabaseReference {
        return getDatabaseReference().child(GROUPS)
    }

    // Get specific group by ID from "Groups" -> group id
    fun getGroupReferenceByID(id: String): DatabaseReference {
        return getGroupsReference().child(id)
    }

    // Get "Notes" from "Groups" -> group id -> "messages"
    fun getGroupNoteReference(id: String): DatabaseReference {
        return getGroupReferenceByID(id).child(GROUPS_MESSAGES)
    }

    // Get "members" from "Groups" -> group id -> "members"
    fun getGroupMembersDataReference(id: String): DatabaseReference {
        return getGroupReferenceByID(id).child(GROUPS_MEMBERS)
    }

    // Get "members" from "Groups" -> group id -> "members" -> user id
    fun getGroupMembersReference(reference: DatabaseReference, id: String): DatabaseReference {
        return reference.child(GROUPS_MEMBERS).child(id)
    }


    // "Users" branch
    fun getUsersReference(): DatabaseReference {
        return getDatabaseReference().child(USERS)
    }

    fun getCurrentUserReferenceByID(): DatabaseReference {
        return getFirebaseUser().uid.let { getUsersReference().child(it) }
    }

    fun getUserNameReference(uID: String): DatabaseReference {
        return getUsersReference().child(uID).child(USER_NAME)
    }

    fun getUserEmailReference(uID: String): DatabaseReference {
        return getUsersReference().child(uID).child(USER_EMAIL)
    }

    fun getUserPhoneReference(uID: String): DatabaseReference {
        return getUsersReference().child(uID).child(USER_PHONE)
    }


    /**
     * Removing data methods
     */
    // Removing data from Firebase in MainMenuFragment
    fun deleteMenuNoteFromFirebase(list: List<MainMenuNote>, position: Int) {
        getTotalDataRefCurrentUser()
                .child(list[position].id)
                .removeValue()

        getNotesReference()
                .child(list[position].id)
                .removeValue()
    }

    // Removing Group data from Firebase in MainMenuFragment
    fun deleteGroupNoteFromFirebase(list: List<MainMenuNote>, position: Int) {
        getTotalDataRefCurrentUser()
                .child(list[position].id)
                .removeValue()

        getGroupReferenceByID(list[position].id)
                .child(GROUPS_MEMBERS)
                .child(getFirebaseUser().uid)
                .removeValue()
    }

    // Removing item data from Firebase in NoteFragment
    fun deleteNoteFromFirebase(list: List<Note>, position: Int) {
        getNotesReference()
                .child(list[position].id_note)
                .child(list[position].Uid)
                .removeValue()
    }

    // Removing item data from Firebase in GroupNoteFragment
    fun deleteGroupItemFromFirebase(list: List<GroupNote>, position: Int, id: String) {
        getGroupNoteReference(id)
                .child(list[position].id_note)
                .removeValue()
    }
}