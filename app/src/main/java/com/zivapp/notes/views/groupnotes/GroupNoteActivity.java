package com.zivapp.notes.views.groupnotes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.zivapp.notes.R;
import com.zivapp.notes.adapters.AdapterGroupItem;
import com.zivapp.notes.firebase.FirebaseHelper;
import com.zivapp.notes.model.GroupNote;
import com.zivapp.notes.model.MainMenuNote;
import com.zivapp.notes.model.Note;
import com.zivapp.notes.databinding.ActivityGroupNoteBinding;
import com.zivapp.notes.model.FormatSum;
import com.zivapp.notes.model.User;
import com.zivapp.notes.util.UtilConverter;
import com.zivapp.notes.util.UtilDate;

import java.util.ArrayList;
import java.util.List;

public class GroupNoteActivity extends AppCompatActivity {

    private static final String TAG = "GroupNoteActivity";

    private ArrayList<GroupNote> mNoteList = new ArrayList<>();
    private ArrayList<User> mMembersList = new ArrayList<>();

    private MainMenuNote mMainMenuNote;
    private User mUser;

    private ActivityGroupNoteBinding mBinding;
    private RecyclerView mRecyclerView;
    private AdapterGroupItem mAdapter;

    private FirebaseHelper mFirebaseHelper;
    private DatabaseReference mNotesReference;
    private DatabaseReference mTotalDataReference;
    private DatabaseReference mUserReference;
    private DatabaseReference mMembersReference;

    private String ID, mTitleName, mDate;
    private int mTotalSum;
    private boolean mFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_note);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_group_note);

        firebaseInstances();
        mUser = getUserFromFirebase(mUserReference);
        loadRecyclerView(mNoteList);
        ID = checkID();

        addNewNoteItem();
        setBackArrow();
    }

    private void setBackArrow() {
        setSupportActionBar(mBinding.toolbar.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void firebaseInstances() {
        mFirebaseHelper = new FirebaseHelper();

        // Current User reference
        mUserReference = mFirebaseHelper.getCurrentUserReferenceByID();
        mUserReference.keepSynced(true);
    }

    private void references(String id) {
        mNotesReference = mFirebaseHelper.getGroupNoteReference(id);
        mTotalDataReference = mFirebaseHelper.getGroupTotalDataReference(id);
        mMembersReference = mFirebaseHelper.getGroupMembersDataReference(id);

        mNotesReference.keepSynced(true);
        mTotalDataReference.keepSynced(true);
        mMembersReference.keepSynced(true);
    }

    // User data
    private User getUserFromFirebase(DatabaseReference reference) {
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
     * Getting intent Extra ID from NotesActivity
     * and checking if there is note in database or not
     * if Yes - loading data from DB
     * if No - generate new ID
     */
    private String checkID() {
        Bundle arguments = getIntent().getExtras();
        String uID = arguments.getString("id_note");

        // if already exist in database
        if (uID != null) {
            Log.v(TAG, "This note is already exist! ID: " + uID);

            references(uID);
            mNoteList = getDataFromFirebase(mNotesReference);
            mMainMenuNote = getTotalDataFromFirebase(mTotalDataReference);
            mMembersList = getMembersFromFirebase(mMembersReference);
            mFlag = false;

        } else { // if not exist in database
            Log.v(TAG, "New Note!");

            uID = getExtraData();
            references(uID);
            mNoteList = getDataFromFirebase(mNotesReference);
            mMembersList = getMembersFromFirebase(mMembersReference);
            mFlag = true;
        }
        return uID;
    }

    private String getExtraData() {
        Bundle arguments = getIntent().getExtras();
        String key = "";
        if (arguments != null) {
            key = arguments.getString("key");
        }
        return key;
    }

    /**
     * Get all messages of this Note from firebase
     */
    private ArrayList<GroupNote> getDataFromFirebase(DatabaseReference reference) {
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
     * Get title, total sum of this Note from firebase
     */
    private MainMenuNote getTotalDataFromFirebase(DatabaseReference reference) {
        final MainMenuNote mainMenuNote = new MainMenuNote();

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mainMenuNote.setTitle(dataSnapshot.getValue(MainMenuNote.class).getTitle());
                mainMenuNote.setTotal_sum(dataSnapshot.getValue(MainMenuNote.class).getTotal_sum());
                mainMenuNote.setId(dataSnapshot.getValue(MainMenuNote.class).getId());
                mainMenuNote.setDate(dataSnapshot.getValue(MainMenuNote.class).getDate());

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
        mBinding.toolbar.setMainNote(mainMenuNote);
    }

    private void setFormatSumDataInLayout(int totalSum) {
        mBinding.toolbar.setFormat(new FormatSum(UtilConverter.customStringFormat(totalSum)));
    }

    /**
     * Get members (key) of this Group Note from firebase
     */
    private ArrayList<User> getMembersFromFirebase(DatabaseReference reference) {
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
     * changing focus on EditText "Add name" if Title exist.
     */
    private void changeFocusIfTitleExist(MainMenuNote mainMenuNote) {
        if (mainMenuNote == null) return;
        if (!mainMenuNote.getTitle().equals(""))
            mBinding.includeInterface.etNameOperation.requestFocus();
    }

    /**
     * Count getSum() from every Note.class object
     * and bind total sum to XML
     */
    private int getTotalSum(List<GroupNote> list, int price) {
        int totalSum = price;
        for (int i = 0; i < list.size(); i++) {
            totalSum += list.get(i).getSum();
        }
        setFormatSumDataInLayout(totalSum);

        return totalSum;
    }

    private void loadRecyclerView(ArrayList<GroupNote> list) {
        mRecyclerView = mBinding.recyclerGroups;
        mRecyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(
                        1,
                        StaggeredGridLayoutManager.VERTICAL)
        );

        mAdapter = new AdapterGroupItem(list, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void updateUI(ArrayList<GroupNote> list) {
        if (mAdapter == null) {
            mAdapter = new AdapterGroupItem(list, this);
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
        final EditText editTextName = mBinding.includeInterface.etNameOperation;
        final EditText editTextPrice = mBinding.includeInterface.etPriceOperation;
        editTextName.requestFocus();

        // Add button
        mBinding.includeInterface.buttonAddNewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // checking if edit text not empty
                if (editTextName.getText().toString().trim().equals("")
                        || editTextPrice.getText().toString().trim().equals("")) {
                    Toast.makeText(GroupNoteActivity.this,
                            "Please add both Sum and Name of operation",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                String message = editTextName.getText().toString().trim();
                int price = Integer.parseInt(editTextPrice.getText().toString().trim());
                mTotalSum = getTotalSum(mNoteList, price);

                // Saving message
                saveGroupNoteInFirebase(message, price);

                editTextName.getText().clear();
                editTextPrice.getText().clear();
                editTextName.requestFocus();
            }
        });
    }

    private void saveGroupNoteInFirebase(String message, int price) {
        GroupNote gNote = new GroupNote();
        gNote.setMessage(message);
        gNote.setSum(price);
        gNote.setMember(mUser.getName());
        gNote.setDate(UtilDate.getGroupDate());
        gNote.setUid(mFirebaseHelper.getFirebaseUser().getUid());
        mNotesReference.push().setValue(gNote);
    }

    @Override
    protected void onDestroy() {
        Log.v(TAG, "onDestroy()");

        mTitleName = mBinding.toolbar.etNoteTitleName.getText().toString().trim();
        mDate = UtilDate.getCurrentDate();

        if (mFlag) {
            saveMainMenuNoteData();
        } else {
            updateMainMenuNoteData();
        }
        super.onDestroy();
    }

    /**
     * Saving data, MainMenuNote.class;
     */
    private void saveMainMenuNoteData() {
        Log.v(TAG, "saveMainMenuNoteData()");

        if (mTotalSum != 0) {
            Log.v(TAG, "DATA SAVED for the first time!");
            Log.v(TAG, "saveMainMenuNoteData() *** DATE: " + mDate + "; Title: "
                    + mTitleName + "; TotalSUm: " + mTotalSum + "; ID: " + ID);

            saveTotalData();

        } else {
            Log.v(TAG, "Empty note haven't saved!");
        }
    }

    private void saveTotalData() {
        MainMenuNote mainMenuNote = new MainMenuNote(mDate, mTitleName, mTotalSum, ID, true);

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

    /**
     * Updating data, MainMenuNote.class
     */
    private void updateMainMenuNoteData() {
        Log.v(TAG, "updateMainMenuNoteData()");

        // If title or total sum has changed than update data
        if (mMainMenuNote.getTitle() == null) {
            Log.v(TAG, "mMainMenuNote is NULL");

            saveTotalData();

        } else if (!mMainMenuNote.getTitle().equals(mTitleName)
                || mMainMenuNote.getTotal_sum() != mTotalSum) {
            Log.v(TAG, "DATA UPDATED");
            Log.v(TAG, "updateMainMenuNoteData() *** DATE: " + mDate + "; Title: "
                    + mTitleName + "; TotalSUm: " + mTotalSum + "; ID: " + ID);

            saveTotalData();

        } else {
            Log.v(TAG, "Note haven't changed!");
        }
    }
}