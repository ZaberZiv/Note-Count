package com.zivapp.notes.views.groupnotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.zivapp.notes.R;
import com.zivapp.notes.adapters.AdapterContacts;
import com.zivapp.notes.adapters.SelectedUsersListener;
import com.zivapp.notes.databinding.ActivityContactsListBinding;
import com.zivapp.notes.firebase.FirebaseHelper;
import com.zivapp.notes.model.User;

import java.util.ArrayList;

public class ContactsListActivity extends AppCompatActivity implements SelectedUsersListener {
    private static final String TAG = "ContactsListActivity";

    public static final int REQUEST_READ_CONTACTS = 79;

    private ActivityContactsListBinding mBinding;
    private RecyclerView mRecyclerView;
    private AdapterContacts mAdapter;

    private ArrayList<User> mContactsList = new ArrayList<>();
    private ArrayList<User> mFirebaseUsersList = new ArrayList<>();

    private FirebaseHelper mFirebaseHelper;
    private DatabaseReference mGroupIDReference;
    private DatabaseReference mUserReference;
    private DatabaseReference mAllUsersReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_list);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_contacts_list);

        checkPermission();
        loadRecyclerView(mFirebaseUsersList);
        firebaseInstances();

        new Handler().postDelayed((new Runnable() {
            @Override
            public void run() {
                updateUI(findMatchedUsers(mContactsList, mFirebaseUsersList));
            }
        }), 500);

        buttonSelectUsers();
        refreshFirebaseCallback();
    }

    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            mContactsList = getAllContacts();
        } else {
            requestPermission();
        }
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
        Log.v(TAG, "Match loop: finished working");

        return listOfMatchedUsers;
    }

    public void firebaseInstances() {
        mFirebaseHelper = new FirebaseHelper();
        mAllUsersReference = mFirebaseHelper.getUsersReference();
        mAllUsersReference.keepSynced(true);
        mFirebaseUsersList = getDataFromFirebase(mAllUsersReference);
    }

    public ArrayList<User> getDataFromFirebase(DatabaseReference reference) {
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

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                android.Manifest.permission.READ_CONTACTS)) {
            // show UI part if you want here to show some rationale !!!
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        }

        if (ActivityCompat.shouldShowRequestPermissionRationale(this
                , android.Manifest.permission.READ_CONTACTS)) {
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mContactsList = getAllContacts();

            } else {
                // permission denied, Disable the
                // functionality that depends on this permission.
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private ArrayList<User> getAllContacts() {

        ArrayList<User> userList = new ArrayList<>();
        ContentResolver cr = getContentResolver();

        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                User user = new User();

                String id = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts._ID));

                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                user.setName(name);

                if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {

                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);

                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        user.setPhone(phoneNo);
                    }
                    pCur.close();
                }
                userList.add(user);
            }
        }

        if (cur != null) {
            cur.close();
        }

        return userList;
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
        mBinding.btnAdd.setOnClickListener(new View.OnClickListener() {
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

    // realized in AdapterContacts
    @Override
    public void onSelectedAction(Boolean isSelected) {
        if (isSelected) {
            mBinding.btnAdd.setVisibility(View.VISIBLE);
        } else {
            mBinding.btnAdd.setVisibility(View.GONE);
        }
    }

    private void openNewActivityWithData(String key) {
        Intent intent = new Intent(this, GroupNoteActivity.class);
        intent.putExtra("key", key);
        startActivity(intent);
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
}