package com.zivapp.notes.views.mainmenu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.zivapp.notes.R;
import com.zivapp.notes.adapters.AdapterMenu;
import com.zivapp.notes.databinding.ActivityMenuNotesBinding;
import com.zivapp.notes.firebase.FirebaseHelper;
import com.zivapp.notes.model.FormatSum;
import com.zivapp.notes.model.MainMenuNote;
import com.zivapp.notes.views.groupnotes.ContactsListActivity;
import com.zivapp.notes.views.login.LoginActivity;
import com.zivapp.notes.views.notes.NoteActivity;

import java.util.ArrayList;
import java.util.Objects;

/**
 * A ViewModel used for the {@link MenuNotesActivity}.
 */
public class MenuPresenter {
    private static final String TAG = "MenuPresenter";
    private Context context;

    private ActivityMenuNotesBinding mBinding;
    private RecyclerView mRecyclerView;
    private AdapterMenu mAdapter;

    private ArrayList<MainMenuNote> mArrayList = new ArrayList<>();
//    private ArrayList<String> keys = new ArrayList<>();

    private FirebaseHelper mFirebaseHelper;
//    private DatabaseReference mGroupsReference;

    public MenuPresenter(final Context context, Activity activity) {
        this.context = context;

        mBinding = DataBindingUtil.setContentView(activity, R.layout.activity_menu_notes);

        loadRecyclerView(mArrayList);
        firebaseInstances();
        signOutButton();
        fabButton();
        contactsButton();
    }

    public void firebaseInstances() {
        mFirebaseHelper = new FirebaseHelper();
//        FirebaseUser user = mFirebaseHelper.getFirebaseUser();

//        mGroupsReference = mFirebaseHelper.getGroupsReference();
//        mGroupsReference.keepSynced(true);

        DatabaseReference totalDataReference = mFirebaseHelper.getTotalDataReference();
        totalDataReference.keepSynced(true);

//        DatabaseReference userGroupRef = mFirebaseHelper.getUsersGroupReference(user.getUid());
//        userGroupRef.keepSynced(true);

//        getUserData(userGroupRef);
        getDataFromFirebase(totalDataReference);
    }

    public void getDataFromFirebase(DatabaseReference reference) {
        mBinding.progressBar.setVisibility(View.VISIBLE);

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mArrayList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MainMenuNote mainMenuNote = new MainMenuNote();
                    mainMenuNote.setTitle(snapshot.getValue(MainMenuNote.class).getTitle());
                    mainMenuNote.setTotal_sum(snapshot.getValue(MainMenuNote.class).getTotal_sum());
                    mainMenuNote.setDate(snapshot.getValue(MainMenuNote.class).getDate());
                    mainMenuNote.setId(snapshot.getValue(MainMenuNote.class).getId());
                    mainMenuNote.setGroup(snapshot.getValue(MainMenuNote.class).isGroup());

                    mArrayList.add(mainMenuNote);
                }

                updateUI(mArrayList);
                mBinding.setNote(new FormatSum(mArrayList.size()));
                mBinding.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Object failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        reference.addValueEventListener(postListener);

//        new Handler().postDelayed((new Runnable() {
//            @Override
//            public void run() {
//                loadGroupNotes();
//            }
//        }), 500);
    }

//    public void loadGroupNotes() {
//        for (String key : keys) {
//            Log.v(TAG, "loadGroupNotes(), key: " + key);
//
//            DatabaseReference mReferenceGroup = getTotalGroupRef(key);
//            mReferenceGroup.keepSynced(true);
////            getGroupsNotes(mReferenceGroup);
//        }
//    }
//
//    private DatabaseReference getTotalGroupRef(String key) {
//        return mGroupsReference.child(key).child("Total Data");
//    }
//
//    public void getGroupsNotes(DatabaseReference reference) {
//        mBinding.progressBar.setVisibility(View.VISIBLE);
//
//        ValueEventListener postListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Log.v(TAG, "getGroupsNotes works");
//
//                MainMenuNote mainMenuNote = new MainMenuNote();
//                mainMenuNote.setTitle(dataSnapshot.getValue(MainMenuNote.class).getTitle());
//                mainMenuNote.setTotal_sum(dataSnapshot.getValue(MainMenuNote.class).getTotal_sum());
//                mainMenuNote.setDate(dataSnapshot.getValue(MainMenuNote.class).getDate());
//                mainMenuNote.setId(dataSnapshot.getValue(MainMenuNote.class).getId());
//                mainMenuNote.setGroup(dataSnapshot.getValue(MainMenuNote.class).isGroup());
//                mArrayList.add(mainMenuNote);
//
//                updateUI(mArrayList);
//                mBinding.setNote(new FormatSum(mArrayList.size()));
//                mBinding.progressBar.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // Getting Object failed, log a message
//                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
//            }
//        };
//        reference.addValueEventListener(postListener);
//    }

//    public void getUserData(DatabaseReference reference) {
//        ValueEventListener postListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Log.v(TAG, "getUserData works");
//
//                for (DataSnapshot snap : dataSnapshot.getChildren()) {
//                    keys.add(snap.getKey());
//                    Log.v(TAG, "KEYS: " + snap.getKey());
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // Getting Object failed, log a message
//                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
//            }
//        };
//        reference.addValueEventListener(postListener);
//    }

    private void loadRecyclerView(ArrayList<MainMenuNote> list) {
        mRecyclerView = mBinding.recycler;
        mRecyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(
                        2,
                        StaggeredGridLayoutManager.VERTICAL)
        );

        mAdapter = new AdapterMenu(list, context);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void updateUI(ArrayList<MainMenuNote> list) {
        if (mAdapter == null) {
            mAdapter = new AdapterMenu(list, context);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setNoteList(list);
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Sign Out Button. Redirect to LoginActivity.
     */
    private void signOutButton() {
        mBinding.signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Log.v(TAG, "User signed out!");
                context.startActivity(new Intent(context, LoginActivity.class));
            }
        });
    }

    /**
     * Floating Action Button. Redirect to CreationActivity.
     */
    private void fabButton() {
        mBinding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, NoteActivity.class));
            }
        });
    }

    public void contactsButton() {
        mBinding.groupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ContactsListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                context.startActivity(intent);
            }
        });
    }
}
