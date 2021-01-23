package com.zivapp.countandnote.view.groups

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DatabaseReference
import com.zivapp.countandnote.firebase.FirebaseCallback
import com.zivapp.countandnote.firebase.FirebaseHelper
import com.zivapp.countandnote.model.GroupNote
import com.zivapp.countandnote.model.MainMenuNote
import com.zivapp.countandnote.model.User
import com.zivapp.countandnote.util.ShareData
import com.zivapp.countandnote.util.UtilIntent

class GroupNoteViewModel : ViewModel() {

    private val TAG = "GroupNoteViewModel"

    private lateinit var mFirebaseCallback: FirebaseCallback
    private lateinit var  mFirebaseHelper: FirebaseHelper

    private lateinit var  mNotesReference: DatabaseReference
    private lateinit var  mTotalDataReference: DatabaseReference
    private lateinit var  mUserReference: DatabaseReference
    private lateinit var  mMembersReference: DatabaseReference

    private var mNoteList = listOf<GroupNote>()
    private lateinit var mMainMenuNote: MainMenuNote

    init {
        firebase()
    }

    private fun firebase() {
        mFirebaseCallback = FirebaseCallback()
        mFirebaseHelper = FirebaseHelper()

        // Current User reference
        mUserReference = mFirebaseHelper.getCurrentUserReferenceByID()
        mUserReference.keepSynced(true)
    }

    fun setReferences(id: String) {
        mNotesReference = mFirebaseHelper.getGroupNoteReference(id)
        mTotalDataReference = mFirebaseHelper.getTotalDataRefCurrentNote(id)
        mMembersReference = mFirebaseHelper.getGroupMembersDataReference(id)

        mNotesReference.keepSynced(true)
        mTotalDataReference.keepSynced(true)
        mMembersReference.keepSynced(true)
    }

    /**
     * Get User data from firebase
     */
    fun getUserFromFirebase(): User {
        return mFirebaseCallback.getUserFromFirebase(mUserReference)
    }

    /**
     * Get all messages of this Note from firebase
     */
    fun getDataFromFirebase(): MutableLiveData<MutableList<GroupNote>> {
        mNoteList = mFirebaseCallback.getGroupDataFromFirebase(mNotesReference)
        return mFirebaseCallback.mutableListOfGroupNotes
    }

    /**
     * Get title, total sum of this Note from firebase
     */
    fun getTotalDataFromFirebase(): MutableLiveData<MainMenuNote> {
        mMainMenuNote = mFirebaseCallback.getGroupTotalDataFromFirebase(mTotalDataReference)
        return mFirebaseCallback.mutableTotalDataMainMenuNote
    }

    /**
     * Get members (key) of this Group Note from firebase
     */
    fun getMembersFromFirebase(): MutableLiveData<MutableList<User>> {
        return mFirebaseCallback.getMembersFromFirebase(mMembersReference)
    }

    fun getFirebaseUserID() : String {
       return mFirebaseHelper.getFirebaseUser().uid
    }

    fun saveGroupNoteInFirebase(gNote: GroupNote) {
        mNotesReference.push().setValue(gNote)
    }

    fun saveTotalDataInFirebase(uID: String, childID: String, mainMenuNote: MainMenuNote) {
        mFirebaseHelper.saveTotalDataMembers(uID, childID, mainMenuNote)
    }

    fun saveTotalDataCurrentUser(childID: String, mainMenuNote: MainMenuNote) {
        mFirebaseHelper.saveTotalDataCurrentUser(childID, mainMenuNote)
    }

    // Saving data to the current "Groups" -> "Total Data"
    fun saveGroupsInTotalData(mainMenuNote: MainMenuNote) {
        mTotalDataReference.setValue(mainMenuNote)
    }
    /**
     * method for share button in GroupNoteFragment
     */
    fun shareData(context: Context) {
        val share = ShareData()
        UtilIntent.shareDataByIntent(context,
                share.formatStringDataGroup(mNoteList, mMainMenuNote))
    }
}