package com.zivapp.notes.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.zivapp.notes.model.GroupNote;
import com.zivapp.notes.model.MainMenuNote;
import com.zivapp.notes.model.Note;
import com.zivapp.notes.model.User;
import com.zivapp.notes.views.groupnotes.GroupContract;
import com.zivapp.notes.views.mainmenu.MenuContract;
import com.zivapp.notes.views.notes.NoteContract;

import java.util.ArrayList;

public class FirebaseCallback {

    private static final String TAG = "FirebaseCallback";

    private NoteContract.Firebase firebaseInterface;
    private MenuContract.Firebase menuFirebaseInterface;
    private MenuContract.Presenter menuPresenterInterface;
    private GroupContract.Firebase groupFirebaseInterface;

    public FirebaseCallback() {
    }

    public FirebaseCallback(NoteContract.Firebase firebaseInterface) {
        this.firebaseInterface = firebaseInterface;
    }

    public FirebaseCallback(MenuContract.Firebase menuFirebaseInterface, MenuContract.Presenter menuPresenterInterface) {
        this.menuFirebaseInterface = menuFirebaseInterface;
        this.menuPresenterInterface = menuPresenterInterface;
    }

    public FirebaseCallback(GroupContract.Firebase groupFirebaseInterface) {
        this.groupFirebaseInterface = groupFirebaseInterface;
    }

    /**
     * MenuPresenter class
     * Get title, total sum, date of all Notes from firebase
     */
    public ArrayList<MainMenuNote> getMenuNoteDataFromFirebase(DatabaseReference reference) {
        menuPresenterInterface.progressbarOn();

        final ArrayList<MainMenuNote> list = new ArrayList<>();

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MainMenuNote mainMenuNote = new MainMenuNote();
                    mainMenuNote.setTitle(snapshot.getValue(MainMenuNote.class).getTitle());
                    mainMenuNote.setTotal_sum(snapshot.getValue(MainMenuNote.class).getTotal_sum());
                    mainMenuNote.setDate(snapshot.getValue(MainMenuNote.class).getDate());
                    mainMenuNote.setId(snapshot.getValue(MainMenuNote.class).getId());
                    mainMenuNote.setGroup(snapshot.getValue(MainMenuNote.class).isGroup());
                    list.add(mainMenuNote);
                }

                menuFirebaseInterface.updateNoteUI(list);
                menuPresenterInterface.progressbarOff();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Object failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        reference.addValueEventListener(postListener);
        return list;
    }

    /**
     * NotePresenter class
     * Get all messages of this Note from firebase
     */
    public ArrayList<Note> getDataFromFirebase(DatabaseReference reference) {
        final ArrayList<Note> noteList = new ArrayList<>();

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                noteList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Note notes = new Note();
                    notes.setUid(snapshot.getKey());
                    notes.setMessage(snapshot.getValue(Note.class).getMessage());
                    notes.setSum(snapshot.getValue(Note.class).getSum());
                    notes.setId_note(snapshot.getValue(Note.class).getId_note());
                    noteList.add(notes);
                }
                firebaseInterface.updateNoteUI(noteList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        reference.addValueEventListener(postListener);
        return noteList;
    }

    /**
     * NotePresenter class
     * Get title of this Note from firebase
     */
    public MainMenuNote getTotalDataFromFirebase(DatabaseReference reference) {
        final MainMenuNote mainMenuNote = new MainMenuNote();

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mainMenuNote.setTitle(dataSnapshot.getValue(MainMenuNote.class).getTitle());
                mainMenuNote.setTotal_sum(dataSnapshot.getValue(MainMenuNote.class).getTotal_sum());
                mainMenuNote.setDate(dataSnapshot.getValue(MainMenuNote.class).getDate());
                mainMenuNote.setId(dataSnapshot.getValue(MainMenuNote.class).getId());

                firebaseInterface.workWithTotalData(mainMenuNote);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        reference.addValueEventListener(postListener);
        return mainMenuNote;
    }

    /**
     * GroupPresenter class
     * User data
     */
    public User getUserFromFirebase(DatabaseReference reference) {
        final User user = new User();

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user.setName(dataSnapshot.getValue(User.class).getName());
                user.setId(dataSnapshot.getValue(User.class).getId());
                user.setEmail(dataSnapshot.getValue(User.class).getEmail());
                user.setPhone(dataSnapshot.getValue(User.class).getPhone());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        reference.addValueEventListener(postListener);
        return user;
    }

    /**
     * GroupPresenter class
     * Get all messages of this Note from firebase
     */
    public ArrayList<GroupNote> getGroupDataFromFirebase(DatabaseReference reference) {
        final ArrayList<GroupNote> noteList = new ArrayList<>();

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                noteList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    GroupNote notes = new GroupNote();
                    notes.setId_note(snapshot.getKey());
                    notes.setUid(snapshot.getValue(Note.class).getUid());
                    notes.setMessage(snapshot.getValue(Note.class).getMessage());
                    notes.setSum(snapshot.getValue(Note.class).getSum());
                    notes.setMember(snapshot.getValue(GroupNote.class).getMember());
                    notes.setDate(snapshot.getValue(GroupNote.class).getDate());
                    noteList.add(notes);
                }

                groupFirebaseInterface.updateGroupNoteUI(noteList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        reference.addValueEventListener(postListener);
        return noteList;
    }
    /**
     * GroupPresenter class
     * Get title, total sum of this Note from firebase
     */
    public MainMenuNote getGroupTotalDataFromFirebase(DatabaseReference reference) {
        final MainMenuNote mainMenuNote = new MainMenuNote();

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mainMenuNote.setTitle(dataSnapshot.getValue(MainMenuNote.class).getTitle());
                mainMenuNote.setTotal_sum(dataSnapshot.getValue(MainMenuNote.class).getTotal_sum());
                mainMenuNote.setId(dataSnapshot.getValue(MainMenuNote.class).getId());
                mainMenuNote.setDate(dataSnapshot.getValue(MainMenuNote.class).getDate());

                groupFirebaseInterface.updateTotalDataUI(mainMenuNote);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        reference.addValueEventListener(postListener);
        return mainMenuNote;
    }

    /**
     * GroupPresenter class
     * Get members (key) of this Group Note from firebase
     */
    public ArrayList<User> getMembersFromFirebase(DatabaseReference reference) {
        final ArrayList<User> list = new ArrayList<>();

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    User user = new User();
                    user.setId(snap.getKey());
                    list.add(user);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        reference.addValueEventListener(postListener);
        return list;
    }

    /**
     * ContactsPresenter
     * This method calls only once
     */
    public ArrayList<User> getUsersDataFromFirebase(DatabaseReference reference) {
        final ArrayList<User> list = new ArrayList<>();

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    User user = new User();
                    user.setName(snap.getValue(User.class).getName());
                    user.setPhone(snap.getValue(User.class).getPhone());
                    user.setEmail(snap.getValue(User.class).getEmail());
                    user.setId(snap.getKey());
                    list.add(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadPost:onCancelled", error.toException());
            }
        });

        return list;
    }
}
