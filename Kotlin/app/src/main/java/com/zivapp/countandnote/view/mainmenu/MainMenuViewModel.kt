package com.zivapp.countandnote.view.mainmenu

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DatabaseReference
import com.zivapp.countandnote.firebase.FirebaseCallback
import com.zivapp.countandnote.firebase.FirebaseHelper
import com.zivapp.countandnote.model.MainMenuNote

class MainMenuViewModel : ViewModel() {

    private val TAG = "MainMenuViewModel"
    private val mFirebaseCallback = FirebaseCallback()

    fun getMutableListOfMainMenuNote() : MutableLiveData<MutableList<MainMenuNote>> {
        val firebaseHelper = FirebaseHelper()
        val totalDataReference: DatabaseReference = firebaseHelper.getTotalDataRefCurrentUser()
        totalDataReference.keepSynced(true)

        return getDataFromFirebase(totalDataReference)
    }

    private fun getDataFromFirebase(reference: DatabaseReference)
            : MutableLiveData<MutableList<MainMenuNote>> {
        mFirebaseCallback.getMenuNoteDataFromFirebase(reference)
        return mFirebaseCallback.mutableListOfMainMenuNote
    }
}