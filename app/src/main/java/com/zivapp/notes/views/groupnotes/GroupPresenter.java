package com.zivapp.notes.views.groupnotes;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.firebase.database.DatabaseReference;
import com.zivapp.notes.R;
import com.zivapp.notes.adapters.AdapterGroupItem;
import com.zivapp.notes.databinding.ActivityGroupNoteBinding;
import com.zivapp.notes.firebase.FirebaseCallback;
import com.zivapp.notes.firebase.FirebaseHelper;
import com.zivapp.notes.model.FormatSum;
import com.zivapp.notes.model.GroupNote;
import com.zivapp.notes.model.MainMenuNote;
import com.zivapp.notes.model.Note;
import com.zivapp.notes.model.User;
import com.zivapp.notes.util.ShareData;
import com.zivapp.notes.util.UtilConverter;
import com.zivapp.notes.util.UtilDate;
import com.zivapp.notes.util.UtilIntent;

import java.util.ArrayList;
import java.util.List;

public class GroupPresenter implements GroupContract.Firebase, GroupContract.Adapter {

    private static final String TAG = "GroupPresenter";

    private ArrayList<GroupNote> mNoteList = new ArrayList<>();
    private ArrayList<User> mMembersList = new ArrayList<>();

    private MainMenuNote mMainMenuNote;
    private final User mUser;

    private final ActivityGroupNoteBinding mBinding;
    private final Activity activity;

    private RecyclerView mRecyclerView;
    private AdapterGroupItem mAdapter;

    private FirebaseCallback mFirebaseCallback;
    private FirebaseHelper mFirebaseHelper;
    private DatabaseReference mNotesReference;
    private DatabaseReference mTotalDataReference;
    private DatabaseReference mUserReference;
    private DatabaseReference mMembersReference;

    private final String ID;
    private int mTotalSum;
    private boolean mFlag;

    public GroupPresenter(Activity activity) {
        this.activity = activity;
        mBinding = DataBindingUtil.setContentView(activity, R.layout.activity_group_note);

        firebase();
        mUser = getUserFromFirebase(mUserReference);
        loadRecyclerView(mNoteList);
        ID = checkID();
        addNewNoteItem();
    }

    private void firebase() {
        mFirebaseCallback = new FirebaseCallback(this);
        mFirebaseHelper = new FirebaseHelper();

        // Current User reference
        mUserReference = mFirebaseHelper.getCurrentUserReferenceByID();
        mUserReference.keepSynced(true);
    }

    private void setReferences(String id) {
        mNotesReference = mFirebaseHelper.getGroupNoteReference(id);
        mTotalDataReference = mFirebaseHelper.getTotalDataRefCurrentNote(id);
        mMembersReference = mFirebaseHelper.getGroupMembersDataReference(id);

        mNotesReference.keepSynced(true);
        mTotalDataReference.keepSynced(true);
        mMembersReference.keepSynced(true);
    }

    /**
     * Getting intent Extra ID from NotesActivity
     * and checking if there is note in database or not
     * if Yes - loading data from DB
     * if No - generate new ID
     */
    private String checkID() {
        Bundle arguments = activity.getIntent().getExtras();
        String uID = arguments.getString("id_note");

        if (uID != null) {// if exist in database
            Log.v(TAG, "This note is already exist! ID: " + uID);

            setReferences(uID);
            mNoteList = getDataFromFirebase(mNotesReference);
            mMainMenuNote = getTotalDataFromFirebase(mTotalDataReference);
            mMembersList = getMembersFromFirebase(mMembersReference);
            mFlag = false;

        } else { // if created in contacts
            Log.v(TAG, "New Note!");

            uID = getExtraData();
            setReferences(uID);
            mNoteList = getDataFromFirebase(mNotesReference);
            mMembersList = getMembersFromFirebase(mMembersReference);
            mFlag = true;
        }
        return uID;
    }

    private String getExtraData() {
        Bundle arguments = activity.getIntent().getExtras();
        String key = "";
        if (arguments != null) {
            key = arguments.getString("key");
        }
        return key;
    }

    /**
     * Get User data from firebase
     */
    private User getUserFromFirebase(DatabaseReference reference) {
        return mFirebaseCallback.getUserFromFirebase(reference);
    }

    /**
     * Get all messages of this Note from firebase
     */
    private ArrayList<GroupNote> getDataFromFirebase(DatabaseReference reference) {
        return mFirebaseCallback.getGroupDataFromFirebase(reference);
    }

    /**
     * Get title, total sum of this Note from firebase
     */
    private MainMenuNote getTotalDataFromFirebase(DatabaseReference reference) {
        return mFirebaseCallback.getGroupTotalDataFromFirebase(reference);
    }

    /**
     * Get members (key) of this Group Note from firebase
     */
    private ArrayList<User> getMembersFromFirebase(DatabaseReference reference) {
        return mFirebaseCallback.getMembersFromFirebase(reference);
    }

    /**
     * Count getSum() from every Note.class object
     * and bind total sum to XML
     */
    private void setTotalSum(List<GroupNote> list, int price) {
        int totalSum = price;
        for (Note note : list) {
            totalSum += note.getSum();
        }
        mTotalSum = totalSum;
        setFormatTotalSum(totalSum);
    }

    private void setFormatTotalSum(int totalSum) {
        mBinding.toolbar.setFormat(new FormatSum(UtilConverter.customStringFormat(totalSum)));
    }

    private void loadRecyclerView(ArrayList<GroupNote> list) {
        mRecyclerView = mBinding.recyclerGroups;
        mRecyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(
                        1,
                        StaggeredGridLayoutManager.VERTICAL)
        );

        mAdapter = new AdapterGroupItem(list, activity, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void updateUI(ArrayList<GroupNote> list) {
        if (mAdapter == null) {
            mAdapter = new AdapterGroupItem(list, activity, this);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setNoteList(list);
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * "Add" button - get data from all EditTexts
     * and add data to the recyclerView and bind to XML file,
     * notify adapter that data has changed.
     * EditText initialized here and made some work with it.
     */
    private void addNewNoteItem() {
        final EditText editTextTitle = mBinding.toolbar.etNoteTitleName;
        final EditText editTextName = mBinding.includeInterface.etNameOperation;
        final EditText editTextPrice = mBinding.includeInterface.etPriceOperation;
        editTextTitle.requestFocus();

        // Add button
        mBinding.includeInterface.buttonAddNewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // checking if edit text not empty
                if (editTextName.getText().toString().trim().equals("")
                        || editTextPrice.getText().toString().trim().equals("")) {
                    Toast.makeText(activity,
                            R.string.no_data,
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                String message = editTextName.getText().toString().trim();
                int sum = Integer.parseInt(editTextPrice.getText().toString().trim());
                setTotalSum(mNoteList, sum);

                // Saving message
                saveGroupNote(message, sum);
                //Saving total sum
                saveTotalData();

                editTextName.getText().clear();
                editTextPrice.getText().clear();
                editTextName.requestFocus();
            }
        });
    }

    /**
     * Saving data, in GroupNoteActivity;
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
     * Updating data, in GroupNoteActivity;
     */
    void updateMainMenuNoteData() {
        Log.v(TAG, "updateMainMenuNoteData()");

        // If title or total sum has changed than update data
        if (mMainMenuNote.getTitle() == null || !getMessage().equals(mMainMenuNote.getMessage())) {
            Log.v(TAG, "mMainMenuNote is NULL");

            saveTotalData();

        } else if (!mMainMenuNote.getTitle().equals(getTitleName())
                || mMainMenuNote.getTotal_sum() != mTotalSum) {
            Log.v(TAG, "DATA UPDATED");

            saveTotalData();

        } else {
            Log.v(TAG, "Note haven't changed!");
        }
    }

    private void saveGroupNote(String message, int price) {
        GroupNote gNote = new GroupNote();
        gNote.setMessage(message);
        gNote.setSum(price);
        gNote.setMember(mUser.getName());
        gNote.setDate(UtilDate.getGroupDate());
        gNote.setUid(mFirebaseHelper.getFirebaseUser().getUid());
        gNote.setGroup_id(ID);
        saveGroupNoteInFirebase(gNote);
    }

    private void saveGroupNoteInFirebase(GroupNote gNote) {
        mNotesReference.push().setValue(gNote);
    }

    private void saveTotalData() {
        MainMenuNote mainMenuNote = new MainMenuNote();
        mainMenuNote.setDate(getCurrentDate());
        mainMenuNote.setTitle(getTitleName());
        mainMenuNote.setTotal_sum(mTotalSum);
        mainMenuNote.setId(ID);
        mainMenuNote.setGroup(true);
        mainMenuNote.setMessage(getMessage());

        saveTotalDataInFirebase(mainMenuNote);
    }

    private void saveTotalDataInFirebase(MainMenuNote mainMenuNote) {
        // Saving data of members to the branch "Total Data" -> member id -> note id -> data
        for (User user : mMembersList) {
            mFirebaseHelper.saveTotalDataMembers(user.getId(), ID, mainMenuNote);
            Log.v(TAG, "user.getId(): " + user.getId() + ", ID: " + ID);
        }
        // Saving data of current user to the branch "Total Data" -> note id -> data
        mFirebaseHelper.saveTotalDataCurrentUser(ID, mainMenuNote);

        // Saving data to the current "Groups" -> "Total Data"
        mTotalDataReference.setValue(mainMenuNote);
    }

    String getCurrentDate() {
        return UtilDate.getCurrentDate();
    }

    public String getTitleName() {
        return mBinding.toolbar.etNoteTitleName.getText().toString().trim();
    }

    public boolean isFlag() {
        return mFlag;
    }

    public void setFlag(boolean mFlag) {
        this.mFlag = mFlag;
    }

    public String getMessage() {
        return mBinding.includeHintMessage.editTextMessage.getText().toString().trim();
    }

    @Override
    public void updateGroupNoteUI(ArrayList<GroupNote> noteList) {
        setTotalSum(noteList, 0);
        updateUI(noteList);
    }

    @Override
    public void updateTotalDataUI(MainMenuNote mainMenuNote) {
        setMainMenuNoteDataInLayout(mainMenuNote);
        changeFocusIfTitleExist(mainMenuNote);
    }

    /**
     * set data in xml file
     */
    private void setMainMenuNoteDataInLayout(MainMenuNote mainMenuNote) {
        mBinding.toolbar.setMainNote(mainMenuNote);
        mBinding.includeHintMessage.setMainNote(mainMenuNote);
    }

    /**
     * changing focus on EditText "Add name" if Title exist.
     */
    private void changeFocusIfTitleExist(MainMenuNote mainMenuNote) {
        if (mainMenuNote == null) return;
        if (!mainMenuNote.getTitle().equals(""))
            mBinding.includeInterface.etNameOperation.requestFocus();
    }

    @Override
    public String getCurrentNoteID() {
        return ID;
    }

    /**
     * share icon in GroupNoteActivity
     */
    void shareData() {
        UtilIntent.shareDataByIntent(activity,
                ShareData.formatStringDataGroup(mNoteList, mMainMenuNote));
    }
}
