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

import com.google.firebase.auth.FirebaseUser;
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
    private MainMenuNote mMainMenuNote;
    private User mUser;

    private ActivityGroupNoteBinding mBinding;
    private RecyclerView mRecyclerView;
    private AdapterGroupItem mAdapter;

    private FirebaseHelper mFirebaseHelper;
    private DatabaseReference mGroupIDReference;
    private DatabaseReference mNotesReference;
    private DatabaseReference mTotalDataReference;
    private DatabaseReference mReference;
    private DatabaseReference mUserReference;

    private String KEY;
    private String ID, mTitleName, mDate;
    private int mTotalSum;
    private boolean mFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_note);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_group_note);
        mFirebaseHelper = new FirebaseHelper();

        firebaseInstances();
        mUser = getUserFromFirebase(mUserReference);
        loadRecyclerView(mNoteList);
        getExtraData();

        ID = checkID();
        addNewNoteItem();
        setBackArrow();
    }

    private void getExtraData() {
        Bundle arguments = getIntent().getExtras();

        if (arguments != null) {
            KEY = arguments.getString("key");
        }
    }

    private void setBackArrow() {
        setSupportActionBar(mBinding.toolbar.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void firebaseInstances() {
        mReference = mFirebaseHelper.getDatabaseReference();
        FirebaseUser user = mFirebaseHelper.getFirebaseUser();

        // User
        mUserReference = mFirebaseHelper.getUsersReference().child(user.getUid());
        mUserReference.keepSynced(true);
    }

    private void reference(String id) {
        mGroupIDReference = mReference.child("Groups").child(id);
        mNotesReference = mGroupIDReference.child("Notes");
        mTotalDataReference = mGroupIDReference.child("Total Data");

        mGroupIDReference.keepSynced(true);
        mNotesReference.keepSynced(true);
        mTotalDataReference.keepSynced(true);
    }

    /**
     * Get all messages of this Note from firebase
     */
    private ArrayList<GroupNote> getDataFromFirebase(DatabaseReference reference) {
        final ArrayList<GroupNote> noteList = new ArrayList<>();

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    GroupNote notes = new GroupNote();
                    notes.setUid(snapshot.getKey());
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

            reference(uID);
            mNoteList = getDataFromFirebase(mNotesReference);
            mMainMenuNote = getTotalDataFromFirebase(mTotalDataReference);
            mFlag = false;

        } else { // if not exist in database
            Log.v(TAG, "New Note!");

            uID = KEY;
            reference(uID);
            mNoteList = getDataFromFirebase(mNotesReference);
            mFlag = true;
        }
        return uID;
    }

    /**
     * Get title, total sum of this Note from firebase
     */
    private MainMenuNote getTotalDataFromFirebase(DatabaseReference reference) {
        final MainMenuNote mainMenuNote = new MainMenuNote();

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mainMenuNote.setId(dataSnapshot.getValue(MainMenuNote.class).getId());
                mainMenuNote.setDate(dataSnapshot.getValue(MainMenuNote.class).getDate());
                mainMenuNote.setTitle(dataSnapshot.getValue(MainMenuNote.class).getTitle());
                mainMenuNote.setTotal_sum(dataSnapshot.getValue(MainMenuNote.class).getTotal_sum());

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
        GroupNote gNote = new GroupNote(message, price, mUser.getName(), UtilDate.getGroupDate());
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
            Log.v(TAG, "saveMainMenuNoteData() *** DATE: " + mDate + "; Title: " + mTitleName + "; TotalSUm: " + mTotalSum + "; ID: " + ID);

            MainMenuNote mainMenuNote = new MainMenuNote(mDate, mTitleName, mTotalSum, ID, true);
            mTotalDataReference.setValue(mainMenuNote);
        } else {
            Log.v(TAG, "Empty note haven't saved!");
        }
    }

    /**
     * Updating data, MainMenuNote.class
     */
    private void updateMainMenuNoteData() {
        Log.v(TAG, "updateMainMenuNoteData()");

        // If title or total sum has changed than update data
        if (!mMainMenuNote.getTitle().equals(mTitleName) || mMainMenuNote.getTotal_sum() != mTotalSum) {
            Log.v(TAG, "DATA UPDATED");
            Log.v(TAG, "updateMainMenuNoteData() *** DATE: " + mDate + "; Title: " + mTitleName + "; TotalSUm: " + mTotalSum + "; ID: " + ID);

            MainMenuNote mainMenuNote = new MainMenuNote(mDate, mTitleName, mTotalSum, ID, true);
            mTotalDataReference.setValue(mainMenuNote);
        } else {
            Log.v(TAG, "Note haven't changed!");
        }
    }
}