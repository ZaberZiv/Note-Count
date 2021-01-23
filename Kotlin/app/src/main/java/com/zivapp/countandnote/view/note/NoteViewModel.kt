package com.zivapp.countandnote.view.note

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DatabaseReference
import com.zivapp.countandnote.firebase.FirebaseCallback
import com.zivapp.countandnote.firebase.FirebaseHelper
import com.zivapp.countandnote.model.MainMenuNote
import com.zivapp.countandnote.model.Note
import com.zivapp.countandnote.util.ShareData
import com.zivapp.countandnote.util.UtilIntent

class NoteViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG = "NoteViewModel"
    private val mFirebaseCallback = FirebaseCallback()
    private val mFirebaseHelper = FirebaseHelper()
    private var mNoteList = listOf<Note>()
    private var mMainMenuNote = MainMenuNote()

    /**
     * Get all messages of this Note from firebase
     */
    fun getDataFromFirebase(id: String): MutableLiveData<MutableList<Note>> {
        mNoteList = mFirebaseCallback.getDataFromFirebase(getNoteRef(id))
        return mFirebaseCallback.mutableListOfNotes
    }

    private fun getNoteRef(id: String): DatabaseReference {
        val reference = mFirebaseHelper.getCurrentNoteReference(id)
        reference.keepSynced(true)
        return reference
    }

    /**
     * Get title of this Note from firebase
     */
    fun getTotalDataFromFirebase(id: String): MutableLiveData<MainMenuNote> {
        mMainMenuNote = mFirebaseCallback.getTotalDataFromFirebase(getTotalDataRef(id))
        return mFirebaseCallback.mutableMainMenuNote
    }

    private fun getTotalDataRef(id: String): DatabaseReference {
        val reference = mFirebaseHelper.getTotalDataRefCurrentNote(id)
        reference.keepSynced(true)
        return reference
    }

    fun saveTotalDataInFirebase(id: String, mainMenuNote: MainMenuNote) {
        getTotalDataRef(id).setValue(mainMenuNote)
    }

    fun saveNoteInFirebase(id_note: String, note: Note) {
        getNoteRef(id_note).push().setValue(note)
    }

    /**
     * method for share button in NoteFragment
     */
    fun shareData(context: Context) {
        val share = ShareData()
        UtilIntent.shareDataByIntent(context,
                share.formatStringData(mNoteList, mMainMenuNote))
    }
}