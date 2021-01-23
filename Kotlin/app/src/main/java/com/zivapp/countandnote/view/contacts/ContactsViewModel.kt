package com.zivapp.countandnote.view.contacts

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DatabaseReference
import com.zivapp.countandnote.firebase.FirebaseCallback
import com.zivapp.countandnote.firebase.FirebaseHelper
import com.zivapp.countandnote.model.User

class ContactsViewModel() : ViewModel() {
    private val TAG = "ContactsViewModel"

    private lateinit var mFirebaseCallback: FirebaseCallback
    private lateinit var mFirebaseHelper: FirebaseHelper
    private lateinit var mGroupIDReference: DatabaseReference
    private lateinit var mAllUsersReference: DatabaseReference

    init {
        firebaseInstances()
    }

    private fun firebaseInstances() {
        mFirebaseCallback = FirebaseCallback()
        mFirebaseHelper = FirebaseHelper()
        mAllUsersReference = mFirebaseHelper.getUsersReference()
        mAllUsersReference.keepSynced(true)
    }

    fun getDataFromFirebase(): MutableList<User> {
        return mFirebaseCallback.getUsersDataFromFirebase(mAllUsersReference)
    }

    fun firebase() {
        val user = mFirebaseHelper.getFirebaseUser()
        // Generate new key for Group
        mGroupIDReference = mFirebaseHelper.getGroupsReference().push()
        // add current user to the members root | "Groups" -> group id -> "members" -> user uID -> value (true - for group leader)
        mFirebaseHelper.getGroupMembersReference(mGroupIDReference, user.uid).setValue(true)
        Log.v(TAG, "firebase() worked")
    }

    fun addUserToTheGroup(id: String) {
        // adding users to the Group | "Groups" -> group id -> "members" -> member uID -> value (false - for group members)
        mFirebaseHelper.getGroupMembersReference(mGroupIDReference, id).setValue(false)
    }

    fun getGroupKey(): String? {
        return mGroupIDReference.key
    }
}