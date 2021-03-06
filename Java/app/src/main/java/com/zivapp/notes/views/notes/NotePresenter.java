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

import com.google.firebase.database.DatabaseReference;
import com.zivapp.notes.R;
import com.zivapp.notes.adapters.AdapterNote;
import com.zivapp.notes.databinding.ActivityNoteBinding;
import com.zivapp.notes.firebase.FirebaseCallback;
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
import java.util.UUID;

public class NotePresenter implements NoteContract.Firebase {
    private static final String TAG = "NotePresenter";

    private final Context context;
    private final Activity activity;
    private final ActivityNoteBinding mBinding;

    private final String ID;
    private int mTotalSum;
    private boolean mFlag;
    private MainMenuNote mMainMenuNote;
    private ArrayList<Note> mNoteList = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private AdapterNote mAdapter;

    private FirebaseCallback mFirebaseCallback;
    private FirebaseHelper mFirebaseHelper;

    public NotePresenter(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;

        mBinding = DataBindingUtil.setContentView(activity, R.layout.activity_note);

        loadRecyclerView(mNoteList);
        firebase();
        ID = checkID();
        addNewNoteItemButton();
    }

    private void firebase() {
        mFirebaseHelper = new FirebaseHelper();
        mFirebaseCallback = new FirebaseCallback(this);
    }

    private DatabaseReference getNoteRef(String id) {
        DatabaseReference reference = mFirebaseHelper.getCurrentNoteReference(id);
        reference.keepSynced(true);
        return reference;
    }

    private DatabaseReference getTotalDataRef(String id) {
        DatabaseReference reference = mFirebaseHelper.getTotalDataRefCurrentNote(id);
        reference.keepSynced(true);
        return reference;
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
        return mFirebaseCallback.getDataFromFirebase(reference);
    }

    /**
     * NoteContract interface
     * Realized in FirebaseCallback class
     */
    @Override
    public void updateNoteUI(ArrayList<Note> noteList) {
        setTotalSum(noteList, 0);
        updateUI(noteList);
    }

    /**
     * Get title of this Note from firebase
     */
    private MainMenuNote getTotalDataFromFirebase(DatabaseReference reference) {
        return mFirebaseCallback.getTotalDataFromFirebase(reference);
    }

    /**
     * NoteContract interface
     * Realized in FirebaseCallback class
     */
    @Override
    public void workWithTotalData(MainMenuNote mainMenuNote) {
        setMainMenuNoteDataInLayout(mainMenuNote);
        changeFocusIfTitleExist(mainMenuNote);
    }

    /**
     * update XML file
     */
    private void setMainMenuNoteDataInLayout(MainMenuNote mainMenuNote) {
        mBinding.toolbar.setMainNote(mainMenuNote);
        mBinding.includeHintMessage.setMainNote(mainMenuNote);
    }

    /**
     * changing focus on EditText "Add name" if Title exist.
     */
    private void changeFocusIfTitleExist(MainMenuNote mainMenuNote) {
        if (mainMenuNote.getTitle() == null) return;
        if (!mainMenuNote.getTitle().equals(""))
            mBinding.includeInterface.etNameOperation.requestFocus();
    }

    /**
     * Count getSum() from every Note.class object
     * and bind total sum to XML
     */
    private void setTotalSum(List<Note> list, int price) {
        int totalSum = price;
        for (Note note : list) {
            totalSum += note.getSum();
        }
        mTotalSum = totalSum;
        setFormatTotalSum(totalSum);
    }

    /**
     * set formated total sum
     */
    private void setFormatTotalSum(int totalSum) {
        mBinding.toolbar.setFormat(new FormatSum(UtilConverter.customStringFormat(totalSum)));
    }

    /**
     * "Add" button - get data from all EditTexts
     * and add data to the recyclerView and bind to XML file,
     * notify adapter that data has changed.
     * EditText initialized here and made some work with it.
     */
    private void addNewNoteItemButton() {
        final EditText editTextTitleName = mBinding.toolbar.etNoteTitleName;
        final EditText editTextName = mBinding.includeInterface.etNameOperation;
        final EditText editTextPrice = mBinding.includeInterface.etPriceOperation;
        editTextTitleName.requestFocus();

        // Add button
        mBinding.includeInterface.buttonAddNewItem.setOnClickListener(new View.OnClickListener() {
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

                String message = editTextName.getText().toString().trim();
                int sum = Integer.parseInt(editTextPrice.getText().toString().trim());
                setTotalSum(mNoteList, sum);

                // saving message
                saveNote(message, sum, ID);
                // saving total data
                saveTotalData();

                editTextName.getText().clear();
                editTextPrice.getText().clear();
                editTextName.requestFocus();
            }
        });
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

        if (mTotalSum != 0) {
            Log.v(TAG, "DATA SAVED for the first time!");
            saveTotalData();
        } else {
            Log.v(TAG, "Empty note haven't saved!");
        }
    }

    /**
     * Updating data, MainMenuNote.class
     */
    void updateMainMenuNoteData() {
        Log.v(TAG, "updateMainMenuNoteData()");

        // If title or total sum has changed than update data
        if (mMainMenuNote.getTitle() == null || !getMessage().equals(mMainMenuNote.getMessage())) {
            Log.v(TAG, "mainMenuNote is NULL");
            saveTotalData();

        } else if (!mMainMenuNote.getTitle().equals(getTitleName()) || mMainMenuNote.getTotal_sum() != mTotalSum) {
            Log.v(TAG, "DATA UPDATED");
            saveTotalData();

        } else {
            Log.v(TAG, "Note haven't changed!");
        }
    }

    private void saveNote(String message, int sum, String id_note) {
        Note note = new Note();
        note.setMessage(message);
        note.setSum(sum);
        note.setId_note(id_note);
        saveNoteInFirebase(id_note, note);
    }

    private void saveNoteInFirebase(String id_note, Note note) {
        getNoteRef(id_note).push().setValue(note);
    }

    private void saveTotalData() {
        MainMenuNote mainMenuNote = new MainMenuNote();
        mainMenuNote.setDate(getCurrentDate());
        mainMenuNote.setTitle(getTitleName());
        mainMenuNote.setTotal_sum(mTotalSum);
        mainMenuNote.setId(ID);
        mainMenuNote.setGroup(false);
        mainMenuNote.setMessage(getMessage());
        saveTotalDataInFirebase(ID, mainMenuNote);
    }

    private void saveTotalDataInFirebase(String id, MainMenuNote mainMenuNote) {
        getTotalDataRef(id).setValue(mainMenuNote);
    }

    public String getTitleName() {
        return mBinding.toolbar.etNoteTitleName.getText().toString().trim();
    }

    public String getMessage() {
        return mBinding.includeHintMessage.editTextMessage.getText().toString().trim();
    }

    public boolean ismFlag() {
        return mFlag;
    }

    public void setFlag(boolean mFlag) {
        this.mFlag = mFlag;
    }

    public String getCurrentDate() {
        return UtilDate.getCurrentDate();
    }

    /**
     * method for share button in NoteActivity
     */
    public void shareData() {
        UtilIntent.shareDataByIntent(context,
                ShareData.formatStringData(mNoteList, mMainMenuNote));
    }
}