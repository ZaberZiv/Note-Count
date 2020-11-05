package com.zivapp.notes.views.mainmenu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
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

    public MenuPresenter(final Context context, Activity activity) {
        this.context = context;
        mBinding = DataBindingUtil.setContentView(activity, R.layout.activity_menu_notes);

        loadRecyclerView(mArrayList);
        firebaseInstances();
        signOutButton();
        noteButton();
        groupNoteButton();
    }

    public void firebaseInstances() {
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        DatabaseReference totalDataReference = firebaseHelper.getTotalDataRefCurrentUser();
        totalDataReference.keepSynced(true);
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
    }

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
     * Floating Action Button. Redirect to NoteActivity.
     */
    private void noteButton() {
        mBinding.noteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, NoteActivity.class));
            }
        });
    }

    public void groupNoteButton() {
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