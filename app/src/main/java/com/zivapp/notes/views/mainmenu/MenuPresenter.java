package com.zivapp.notes.views.mainmenu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.firebase.database.DatabaseReference;
import com.zivapp.notes.R;
import com.zivapp.notes.adapters.AdapterMenu;
import com.zivapp.notes.databinding.ActivityMenuNotesBinding;
import com.zivapp.notes.firebase.FirebaseCallback;
import com.zivapp.notes.firebase.FirebaseHelper;
import com.zivapp.notes.model.FormatSum;
import com.zivapp.notes.model.MainMenuNote;
import com.zivapp.notes.views.contacts.ContactsListActivity;
import com.zivapp.notes.views.notes.NoteActivity;

import java.util.ArrayList;

/**
 * A ViewModel used for the {@link MenuNotesActivity}.
 */
public class MenuPresenter implements MenuContract.Firebase, MenuContract.Presenter {
    private static final String TAG = "MenuPresenter";

    private Context context;
    private ActivityMenuNotesBinding mBinding;
    private RecyclerView mRecyclerView;
    private AdapterMenu mAdapter;
    private ArrayList<MainMenuNote> mMainMenuNoteList = new ArrayList<>();
    private FirebaseCallback mFirebaseCallback;

    public MenuPresenter(final Context context, Activity activity) {
        this.context = context;
        mBinding = DataBindingUtil.setContentView(activity, R.layout.activity_menu_notes);

        loadRecyclerView(mMainMenuNoteList);
        firebaseInstances();
        noteButton();
        groupNoteButton();
    }

    public void firebaseInstances() {
        mFirebaseCallback = new FirebaseCallback(this, this);
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        DatabaseReference totalDataReference = firebaseHelper.getTotalDataRefCurrentUser();
        totalDataReference.keepSynced(true);
        mMainMenuNoteList = getDataFromFirebase(totalDataReference);
    }

    public ArrayList<MainMenuNote> getDataFromFirebase(DatabaseReference reference) {
        return mFirebaseCallback.getMenuNoteDataFromFirebase(reference);
    }

    @Override
    public void updateNoteUI(ArrayList<MainMenuNote> list) {
        updateUI(list);
        mBinding.setNote(new FormatSum(list.size()));
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

    @Override
    public void progressbarOn() {
        mBinding.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void progressbarOff() {
        mBinding.progressBar.setVisibility(View.GONE);
    }
}