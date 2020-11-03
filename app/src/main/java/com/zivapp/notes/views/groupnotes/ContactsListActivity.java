package com.zivapp.notes.views.groupnotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_list);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_contacts_list);

        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            mContactsList = getAllContacts();
        } else {
            requestPermission();
        }
        loadRecyclerView(mFirebaseUsersList);

        firebaseInstances();

        new Handler().postDelayed((new Runnable() {
            @Override
            public void run() {
                updateUI(findMatchedUsers(mContactsList, mFirebaseUsersList));
            }
        }), 500);

        buttonSelectUsers();
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

    // TODO: refactor this method, add FirebaseHelper
    public void firebaseInstances() {
        mFirebaseHelper = new FirebaseHelper();
        DatabaseReference userReference = mFirebaseHelper.getUsersReference();
        userReference.keepSynced(true);
        mFirebaseUsersList = getDataFromFirebase(userReference);
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
                    Log.v(TAG, "getDataFromFirebase Phone: " + user.getPhone());
                    Log.v(TAG, "getDataFromFirebase ID: " + user.getId());

                    list.add(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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

                // adding users to the Group -> members
                for (User user : listUsers) {
                    mGroupIDReference.child("members").child(user.getId()).setValue(true);
                    Log.v(TAG, "User: " + user.getName() + ", id: " + user.getId());

                    getReferenceUserGroup(user.getId()).child(getGroupKey()).setValue(true);
                    Log.v(TAG, "mGroupIDReference KEY: " + getGroupKey());
                }

                String key = getGroupKey();
                mUserReference.child(key).setValue(true);
                openNewActivityWithData(key);
            }
        });
    }

    // TODO: rework with FirebaseHelper
    private void firebase() {
        FirebaseUser user = mFirebaseHelper.getFirebaseUser();
        // Generate new id for Group
        mGroupIDReference = mFirebaseHelper.getGroupsReference().push();
        // add current user to the members root
        mGroupIDReference.child("members").child(user.getUid()).setValue(true);
        // reference to current user
        mUserReference = getReferenceUserGroup(user.getUid());
        Log.v(TAG, "firebase() worked");
    }

    private String getGroupKey() {
        return mGroupIDReference.getKey();
    }

    // reference to added users
    private DatabaseReference getReferenceUserGroup(String uID) {
        return mFirebaseHelper.getUsersReference().child(uID).child("Group");
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
}