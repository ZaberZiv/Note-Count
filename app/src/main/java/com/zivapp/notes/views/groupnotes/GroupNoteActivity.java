package com.zivapp.notes.views.groupnotes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.zivapp.notes.util.ShareData;
import com.zivapp.notes.util.UtilConverter;
import com.zivapp.notes.util.UtilDate;
import com.zivapp.notes.util.UtilIntent;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.List;

public class GroupNoteActivity extends AppCompatActivity {

    private static final String TAG = "GroupNoteActivity";
    private GroupPresenter mGroupPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_note);
        mGroupPresenter = new GroupPresenter(this);

        setBackArrow();
    }

    private void setBackArrow() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.note_menu, menu);
        return true;
    }

    /**
     * onClick() from menu res folder
     */
    public void shareDataButton(MenuItem item) {
        mGroupPresenter.shareData();
    }

    @Override
    protected void onDestroy() {
        Log.v(TAG, "onDestroy()");

        if (mGroupPresenter.isFlag()) {
            mGroupPresenter.saveMainMenuNoteData();
        } else {
            mGroupPresenter.updateMainMenuNoteData();
        }
        super.onDestroy();
    }
}