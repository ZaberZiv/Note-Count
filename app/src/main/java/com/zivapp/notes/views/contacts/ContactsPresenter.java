package com.zivapp.notes.views.contacts;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.zivapp.notes.R;
import com.zivapp.notes.adapters.AdapterContacts;
import com.zivapp.notes.adapters.SelectedUsersListener;
import com.zivapp.notes.databinding.ActivityContactsListBinding;
import com.zivapp.notes.firebase.FirebaseCallback;
import com.zivapp.notes.firebase.FirebaseHelper;
import com.zivapp.notes.model.User;
import com.zivapp.notes.views.groupnotes.GroupNoteActivity;

import java.util.ArrayList;

public class ContactsPresenter implements SelectedUsersListener {

    private static final String TAG = "ContactsPresenter";

    private ActivityContactsListBinding mBinding;
    private RecyclerView mRecyclerView;
    private AdapterContacts mAdapter;
    private Activity activity;

    private ArrayList<User> mContactsList;
    private ArrayList<User> mFirebaseUsersList = new ArrayList<>();

    private FirebaseCallback mFirebaseCallback;
    private FirebaseHelper mFirebaseHelper;
    private DatabaseReference mGroupIDReference;
    private DatabaseReference mUserReference;
    private DatabaseReference mAllUsersReference;

    public ContactsPresenter(Activity activity, ArrayList<User> list) {
        this.activity = activity;
        mContactsList = list;

        mBinding = DataBindingUtil.setContentView(activity, R.layout.activity_contacts_list);

        loadRecyclerView(mFirebaseUsersList);
        firebaseInstances();

        new Handler().postDelayed((new Runnable() {
            @Override
            public void run() {
                updateUI(findMatchedUsers(mContactsList, mFirebaseUsersList));
            }
        }), 300);

        buttonSelectUsers();
        refreshFirebaseCallback();
    }

    private ArrayList<User> findMatchedUsers(ArrayList<User> contactsList, ArrayList<User> firebaseUsersList) {
        ArrayList<User> listOfMatchedUsers = new ArrayList<>();
        Log.v(TAG, "Match loop: starts working");

        for (int user = 0; user < firebaseUsersList.size(); user++) {
            for (int contact = 0; contact < contactsList.size(); contact++) {
                if (firebaseUsersList.get(user).getPhone().equals(contactsList.get(contact).getPhone())) {
                    listOfMatchedUsers.add(firebaseUsersList.get(user));
                    Log.v(TAG, "WE HAVE MATCH: " + firebaseUsersList.get(user).getPhone());
                }
            }
        }

        if (listOfMatchedUsers.size() == 0) {
            mBinding.noFriendsLayout.setVisibility(View.VISIBLE);
        } else {
            mBinding.noFriendsLayout.setVisibility(View.GONE);
        }

        Log.v(TAG, "Match loop: finished working");

        return listOfMatchedUsers;
    }

    public void firebaseInstances() {
        mFirebaseCallback = new FirebaseCallback();
        mFirebaseHelper = new FirebaseHelper();
        mAllUsersReference = mFirebaseHelper.getUsersReference();
        mAllUsersReference.keepSynced(true);
        mFirebaseUsersList = getDataFromFirebase(mAllUsersReference);
    }

    public ArrayList<User> getDataFromFirebase(DatabaseReference reference) {
        return mFirebaseCallback.getUsersDataFromFirebase(reference);
    }

    private void loadRecyclerView(ArrayList<User> list) {
        mRecyclerView = mBinding.recyclerContacts;
        mRecyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(
                        1,
                        StaggeredGridLayoutManager.VERTICAL)
        );

        mAdapter = new AdapterContacts(list, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void updateUI(ArrayList<User> list) {
        if (mAdapter == null) {
            mAdapter = new AdapterContacts(list, this);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setNoteList(list);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void buttonSelectUsers() {
        mBinding.btnAddContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firebase();
                ArrayList<User> listUsers = mAdapter.getSelectedUsers();

                // this cycle is for adding data to other members roots
                for (User user : listUsers) {
                    // adding users to the Group | "Groups" -> group id -> "members" -> member uID -> value (true)
                    mFirebaseHelper.getGroupMembersReference(mGroupIDReference, user.getId()).setValue(true);
                    Log.v(TAG, "User: " + user.getName() + ", id: " + user.getId());
                    // adding group key to the members
                    // "users" -> member uID -> "Group" -> group key -> value (true)
                    getReferenceUserGroup(user.getId()).child(getGroupKey()).setValue(true);
                    Log.v(TAG, "mGroupIDReference KEY: " + getGroupKey());
                }

                String key = getGroupKey();
                // adding group key to the current user
                // "users" -> user uID -> "Group" -> group key -> value (true)
                mUserReference.child(key).setValue(true);
                openNewActivityWithData(key);
            }
        });
    }

    private void firebase() {
        FirebaseUser user = mFirebaseHelper.getFirebaseUser();
        // Generate new key for Group
        mGroupIDReference = mFirebaseHelper.getGroupsReference().push();
        // add current user to the members root | "Groups" -> group id -> "members" -> user uID -> value (true)
        mFirebaseHelper.getGroupMembersReference(mGroupIDReference, user.getUid()).setValue(true);
        // reference to current user
        mUserReference = getReferenceUserGroup(user.getUid());
        Log.v(TAG, "firebase() worked");
    }

    private String getGroupKey() {
        return mGroupIDReference.getKey();
    }

    // "users" -> user uID -> "Group"
    private DatabaseReference getReferenceUserGroup(String uID) {
        return mFirebaseHelper.getUsersGroupReference(uID);
    }

    private void openNewActivityWithData(String key) {
        Intent intent = new Intent(activity, GroupNoteActivity.class);
        intent.putExtra("key", key);
        activity.startActivity(intent);
    }

    public void refreshFirebaseCallback() {
        mBinding.refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                getDataFromFirebase(mAllUsersReference);

                mBinding.refreshLayout.setRefreshing(false);
            }
        });
    }

    // realized in AdapterContacts
    @Override
    public void onSelectedAction(Boolean isSelected) {
        if (isSelected) {
            mBinding.btnAddContacts.setTranslationY(-50);
        } else {
            mBinding.btnAddContacts.setTranslationY(50);
        }
    }
}
