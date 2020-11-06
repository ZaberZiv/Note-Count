package com.zivapp.notes.views.notes;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.zivapp.notes.R;
import com.zivapp.notes.adapters.AdapterNote;
import com.zivapp.notes.databinding.ActivityNoteBinding;
import com.zivapp.notes.firebase.FirebaseHelper;
import com.zivapp.notes.model.FormatSum;
import com.zivapp.notes.model.MainMenuNote;
import com.zivapp.notes.model.Note;
import com.zivapp.notes.util.ShareData;
import com.zivapp.notes.util.UtilConverter;
import com.zivapp.notes.util.UtilDate;
import com.zivapp.notes.util.UtilIntent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * A ViewModel used for the {@link NoteActivity}.
 */
public class NotePresenter {
    private static final String TAG = "NotePresenter";

    private Context context;
    private Activity activity;

    private String ID, mTitleName, mDate;
    private int mTotalSum;
    private boolean mFlag;
    private MainMenuNote mMainMenuNote;
    private ArrayList<Note> mNoteList = new ArrayList<>();

    private ActivityNoteBinding mBinding;
    private RecyclerView mRecyclerView;
    private AdapterNote mAdapter;

    private FirebaseHelper mFirebaseHelper;
    private DatabaseReference mNotesIDReference;
    private DatabaseReference mTotalDataReference;

    public NotePresenter(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;

        mBinding = DataBindingUtil.setContentView(activity, R.layout.activity_note);
        mDate = UtilDate.getCurrentDate();

        loadRecyclerView(mNoteList);
        firebaseReferences();
        ID = checkID();
        addNewNoteItem();
    }

    private void firebaseReferences() {
        mFirebaseHelper = new FirebaseHelper();

        mNotesIDReference = mFirebaseHelper.getNotesReference();
        mTotalDataReference = mFirebaseHelper.getTotalDataRefCurrentUser();

        mNotesIDReference.keepSynced(true);
        mTotalDataReference.keepSynced(true);
    }

    private DatabaseReference getNoteRef(String id) {
        return mNotesIDReference.child(id);
    }

    private DatabaseReference getTotalDataRef(String id) {
        return mTotalDataReference.child(id);
    }

    private void saveTotalData(String id, MainMenuNote mainMenuNote) {
        getTotalDataRef(id).setValue(mainMenuNote);
    }

    public void shareData() {
        UtilIntent.shareDataByIntent(context,
                ShareData.formatStringData(mNoteList, mMainMenuNote.getTitle()));
    }

    /**
     * Getting intent Extra ID from MenuNotesActivity
     * and checking if there is note in database or not
     * if Yes - loading data from Firebase
     * if No - generate new ID
     */
    private String checkID() {
        Bundle arguments = activity.getIntent().getExtras();
        String uID;

        // if already exist in database
        if (arguments != null) {
            uID = arguments.getString("id_note");
            Log.v(TAG, "This note is already exist! ID: " + uID);

            mNoteList = getDataFromFirebase(getNoteRef(uID));
            mMainMenuNote = getTotalDataFromFirebase(getTotalDataRef(uID));

            mFlag = false;
        } else { // if not exist in database
            Log.v(TAG, "New Note!");

            // generate new ID
            uID = UUID.randomUUID().toString();
            mNoteList = getDataFromFirebase(getNoteRef(uID));

            mFlag = true;
        }
        return uID;
    }

    /**
     * Get all messages of this Note from firebase
     */
    private ArrayList<Note> getDataFromFirebase(DatabaseReference reference) {
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
                mTotalSum = getTotalSum(noteList, 0);
                updateUI(noteList);
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
     * Get title of this Note from firebase
     */
    private MainMenuNote getTotalDataFromFirebase(DatabaseReference reference) {
        final MainMenuNote mainMenuNote = new MainMenuNote();

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mainMenuNote.setTitle(dataSnapshot.getValue(MainMenuNote.class).getTitle());
                mainMenuNote.setTotal_sum(dataSnapshot.getValue(MainMenuNote.class).getTotal_sum());
                mainMenuNote.setDate(dataSnapshot.getValue(MainMenuNote.class).getDate());
                mainMenuNote.setId(dataSnapshot.getValue(MainMenuNote.class).getId());

                setMainMenuNoteDataInLayout(mainMenuNote);
                changeFocusIfTitleExist(mainMenuNote);
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

    private void setMainMenuNoteDataInLayout(MainMenuNote mainMenuNote) {
        mBinding.setMainNote(mainMenuNote);
    }

    /**
     * changing focus on EditText "Add name" if Title exist.
     */
    private void changeFocusIfTitleExist(MainMenuNote mainMenuNote) {
        if (mainMenuNote.getTitle() == null) return;
        if (!mainMenuNote.getTitle().equals("")) mBinding.etNameOperation.requestFocus();
    }

    /**
     * Count getSum() from every Note.class object
     * and bind total sum to XML
     */
    private int getTotalSum(List<Note> list, int price) {
        int totalSum = price;
        for (Note note : list) {
            totalSum += note.getSum();
        }
        setFormatTotalSum(totalSum);
        return totalSum;
    }

    /**
     * set formated total sum
     */
    private void setFormatTotalSum(int totalSum) {
        mBinding.setFormat(new FormatSum(UtilConverter.customStringFormat(totalSum)));
    }

    /**
     * "Add" button - get data from all EditTexts
     * and add data to the recyclerView and bind to XML file,
     * notify adapter that data has changed.
     * EditText initialized here and made some work with it.
     */
    private void addNewNoteItem() {
        final EditText editTextTitleName = mBinding.etNoteTitleName;
        final EditText editTextName = mBinding.etNameOperation;
        final EditText editTextPrice = mBinding.etPriceOperation;
        editTextTitleName.requestFocus();

        // Add button
        mBinding.buttonAddNewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // checking if edit text not empty
                if (editTextName.getText().toString().trim().equals("")
                        || editTextPrice.getText().toString().trim().equals("")) {
                    Toast.makeText(context,
                            R.string.no_data,
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                String name = editTextName.getText().toString().trim();
                int price = Integer.parseInt(editTextPrice.getText().toString().trim());
                mTotalSum = getTotalSum(mNoteList, price);

                // saving message
                saveNoteInFirebase(name, price, ID);

                editTextName.getText().clear();
                editTextPrice.getText().clear();
                editTextName.requestFocus();
            }
        });
    }

    private void saveNoteInFirebase(String name, int price, String id) {
        Note note = new Note(name, price, id);
        getNoteRef(id).push().setValue(note);
    }

    private void loadRecyclerView(ArrayList<Note> list) {
        mRecyclerView = mBinding.recyclerCreation;
        mRecyclerView.setLayoutManager(
                new LinearLayoutManager(context,
                        LinearLayoutManager.VERTICAL, false)
        );
        mAdapter = new AdapterNote(list, activity);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void updateUI(ArrayList<Note> list) {
        if (mAdapter == null) {
            mAdapter = new AdapterNote(list, activity);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setNoteList(list);
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Saving data, MainMenuNote.class;
     */
    void saveMainMenuNoteData() {
        Log.v(TAG, "saveMainMenuNoteData()");

        getTitleName();

        if (mTotalSum != 0) {
            Log.v(TAG, "DATA SAVED for the first time!");
            saveData();
        } else {
            Log.v(TAG, "Empty note haven't saved!");
        }
    }

    private void saveData() {
        MainMenuNote mainMenuNote = new MainMenuNote();
        mainMenuNote.setDate(mDate);
        mainMenuNote.setTitle(mTitleName);
        mainMenuNote.setTotal_sum(mTotalSum);
        mainMenuNote.setId(ID);
        mainMenuNote.setGroup(false);
        saveTotalData(ID, mainMenuNote);
    }

    /**
     * Updating data, MainMenuNote.class
     */
    void updateMainMenuNoteData() {
        Log.v(TAG, "updateMainMenuNoteData()");

        getTitleName();

        // If title or total sum has changed than update data
        if (mMainMenuNote.getTitle() == null) {
            Log.v(TAG, "mainMenuNote is NULL");
            saveData();

        } else if (!mMainMenuNote.getTitle().equals(mTitleName) || mMainMenuNote.getTotal_sum() != mTotalSum) {
            Log.v(TAG, "DATA UPDATED");
            saveData();

        } else {
            Log.v(TAG, "Note haven't changed!");
        }
    }

    public String getTitleName() {
        return mTitleName = mBinding.etNoteTitleName.getText().toString().trim();
    }

    public void setTitleName(String mTitleName) {
        this.mTitleName = mTitleName;
    }

    public boolean ismFlag() {
        return mFlag;
    }

    public void setFlag(boolean mFlag) {
        this.mFlag = mFlag;
    }
}