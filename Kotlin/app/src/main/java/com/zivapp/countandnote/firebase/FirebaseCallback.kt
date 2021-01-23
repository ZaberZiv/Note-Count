package com.zivapp.countandnote.firebase

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.zivapp.countandnote.model.GroupNote
import com.zivapp.countandnote.model.MainMenuNote
import com.zivapp.countandnote.model.Note
import com.zivapp.countandnote.model.User
import java.util.*

class FirebaseCallback {

    private val TAG = "FirebaseCallback"

    val mutableListOfMainMenuNote = MutableLiveData<MutableList<MainMenuNote>>()
    val mutableListOfNotes = MutableLiveData<MutableList<Note>>()
    val mutableMainMenuNote = MutableLiveData<MainMenuNote>()
    val mutableListOfGroupNotes = MutableLiveData<MutableList<GroupNote>>()
    val mutableTotalDataMainMenuNote = MutableLiveData<MainMenuNote>()
    val mutableListOfUsers = MutableLiveData<MutableList<User>>()

    /**
     * MenuPresenter class
     * Get title, total sum, date of all Notes from firebase
     */
    fun getMenuNoteDataFromFirebase(reference: DatabaseReference): MutableList<MainMenuNote> {
//        menuPresenterInterface.progressbarOn()
        val list: MutableList<MainMenuNote> = ArrayList<MainMenuNote>()

        val postListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                list.clear()
                for (snapshot in dataSnapshot.children) {
                    val mainMenuNote = MainMenuNote()
                    mainMenuNote.title = snapshot.getValue(MainMenuNote::class.java)?.title
                            ?: "No title"
                    mainMenuNote.total_sum = snapshot.getValue(MainMenuNote::class.java)?.total_sum
                            ?: 0
                    mainMenuNote.date = snapshot.getValue(MainMenuNote::class.java)?.date
                            ?: "No date"
                    mainMenuNote.id = snapshot.getValue(MainMenuNote::class.java)?.id
                            ?: "No ID"
                    mainMenuNote.group = snapshot.getValue(MainMenuNote::class.java)?.group
                            ?: false

                    list.add(mainMenuNote)
                }

                mutableListOfMainMenuNote.postValue(list)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Object failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        reference.addValueEventListener(postListener)
        return list
    }

    /**
     * NotePresenter class
     * Get all messages of this Note from firebase
     */
    fun getDataFromFirebase(reference: DatabaseReference): MutableList<Note> {
        val noteList: MutableList<Note> = ArrayList<Note>()

        val postListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                noteList.clear()

                for (snapshot in dataSnapshot.children) {
                    val notes = Note()
                    notes.Uid = snapshot?.key
                            ?: "No Uid"
                    notes.message = snapshot.getValue(Note::class.java)?.message
                            ?: "No Message"
                    notes.sum = snapshot.getValue(Note::class.java)?.sum
                            ?: 0
                    notes.id_note = snapshot.getValue(Note::class.java)?.id_note
                            ?: "No ID Note"
                    noteList.add(notes)
                }
                mutableListOfNotes.postValue(noteList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
                // ...
            }
        }
        reference.addValueEventListener(postListener)
        return noteList
    }

    /**
     * NotePresenter class
     * Get title of this Note from firebase
     */
    fun getTotalDataFromFirebase(reference: DatabaseReference): MainMenuNote {
        val mainMenuNote = MainMenuNote()

        val postListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mainMenuNote.title = dataSnapshot.getValue(MainMenuNote::class.java)?.title
                        ?: "No title"
                mainMenuNote.total_sum = dataSnapshot.getValue(MainMenuNote::class.java)?.total_sum
                        ?: 0
                mainMenuNote.date = dataSnapshot.getValue(MainMenuNote::class.java)?.date
                        ?: "No date"
                mainMenuNote.id = dataSnapshot.getValue(MainMenuNote::class.java)?.id
                        ?: "No ID"
                mainMenuNote.group = dataSnapshot.getValue(MainMenuNote::class.java)?.group
                        ?: false
                mainMenuNote.message = dataSnapshot.getValue(MainMenuNote::class.java)?.message
                        ?: "No Message"

                mutableMainMenuNote.postValue(mainMenuNote)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
                // ...
            }
        }
        reference.addValueEventListener(postListener)
        return mainMenuNote
    }

    /**
     * GroupPresenter class
     * User data
     */
    fun getUserFromFirebase(reference: DatabaseReference): User {
        val user = User()
        val postListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                user.name = dataSnapshot.getValue(User::class.java)?.name ?: "No Name"
                user.id = dataSnapshot.getValue(User::class.java)?.id ?: "No ID"
                user.email = dataSnapshot.getValue(User::class.java)?.email ?: "No Email"
                user.phone = dataSnapshot.getValue(User::class.java)?.phone ?: "No Phone"
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
                // ...
            }
        }
        reference.addValueEventListener(postListener)
        return user
    }

    /**
     * GroupPresenter class
     * Get all messages of this Note from firebase
     */
    fun getGroupDataFromFirebase(reference: DatabaseReference): MutableList<GroupNote> {
        val noteList: MutableList<GroupNote> = ArrayList<GroupNote>()

        val postListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                noteList.clear()
                for (snapshot in dataSnapshot.children) {
                    val notes = GroupNote()
                    notes.id_note = snapshot?.key
                            ?: "No ID Note"
                    notes.Uid = snapshot.getValue(GroupNote::class.java)?.Uid
                            ?: "No Uid"
                    notes.message = snapshot.getValue(GroupNote::class.java)?.message
                            ?: "No Message"
                    notes.sum = snapshot.getValue(GroupNote::class.java)?.sum
                            ?: 0
                    notes.group_id = snapshot.getValue(GroupNote::class.java)?.group_id
                            ?: "No Group ID"
                    notes.member = snapshot.getValue(GroupNote::class.java)?.member
                            ?: "No Member"
                    notes.date = snapshot.getValue(GroupNote::class.java)?.date
                            ?: "No Date"

                    noteList.add(notes)
                }
                mutableListOfGroupNotes.postValue(noteList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
                // ...
            }
        }
        reference.addValueEventListener(postListener)
        return noteList
    }

    /**
     * GroupPresenter class
     * Get title, total sum of this Note from firebase
     */
    fun getGroupTotalDataFromFirebase(reference: DatabaseReference): MainMenuNote {
        val mainMenuNote = MainMenuNote()
        val postListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mainMenuNote.title = dataSnapshot.getValue(MainMenuNote::class.java)?.title
                        ?: "No title"
                mainMenuNote.total_sum = dataSnapshot.getValue(MainMenuNote::class.java)?.total_sum
                        ?: 0
                mainMenuNote.date = dataSnapshot.getValue(MainMenuNote::class.java)?.date
                        ?: "No date"
                mainMenuNote.id = dataSnapshot.getValue(MainMenuNote::class.java)?.id
                        ?: "No ID"
                mainMenuNote.group = dataSnapshot.getValue(MainMenuNote::class.java)?.group
                        ?: false
                mainMenuNote.message = dataSnapshot.getValue(MainMenuNote::class.java)?.message
                        ?: "No Message"

                mutableTotalDataMainMenuNote.postValue(mainMenuNote)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
                // ...
            }
        }
        reference.addValueEventListener(postListener)
        return mainMenuNote
    }

    /**
     * GroupPresenter class
     * Get members (key) of this Group Note from firebase
     */
    fun getMembersFromFirebase(reference: DatabaseReference): MutableLiveData<MutableList<User>> {
        val list: MutableList<User> = ArrayList<User>()

        val postListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                list.clear()
                for (snap in dataSnapshot.children) {
                    val user = User()
                    user.id = snap?.key ?: "No KEY"
                    list.add(user)
                    mutableListOfUsers.postValue(list)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
                // ...
            }
        }
        reference.addValueEventListener(postListener)
        return mutableListOfUsers
    }

    /**
     * ContactsPresenter
     * This method calls only once
     */
    fun getUsersDataFromFirebase(reference: DatabaseReference): MutableList<User> {
        val list: MutableList<User> = ArrayList<User>()
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (snap in snapshot.children) {
                    val user = User()
                    user.id = snap?.key ?: "No KEY"
                    user.name = snap.getValue(User::class.java)?.name ?: "No Name"
                    user.email = snap.getValue(User::class.java)?.email ?: "No Email"
                    user.phone = snap.getValue(User::class.java)?.phone ?: "No Phone"

                    list.add(user)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "loadPost:onCancelled", error.toException())
            }
        })
        return list
    }
}