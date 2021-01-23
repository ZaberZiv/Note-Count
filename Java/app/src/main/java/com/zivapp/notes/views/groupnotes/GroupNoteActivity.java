package com.zivapp.notes.views.groupnotes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.zivapp.notes.R;

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