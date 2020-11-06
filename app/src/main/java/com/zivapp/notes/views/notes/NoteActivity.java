package com.zivapp.notes.views.notes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.zivapp.notes.R;
import com.zivapp.notes.model.FormatSum;
import com.zivapp.notes.util.UtilConverter;
import com.zivapp.notes.util.UtilIntent;

public class NoteActivity extends AppCompatActivity {

    private static final String TAG = "NoteActivity";
    private NotePresenter mNotePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        mNotePresenter = new NotePresenter(this, this);
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

    public void shareDataButton(MenuItem item) {
        mNotePresenter.shareData();
    }

    @Override
    protected void onDestroy() {
        Log.v(TAG, "onDestroy()");

        if (mNotePresenter.ismFlag()) {
            mNotePresenter.saveMainMenuNoteData();
        } else {
            mNotePresenter.updateMainMenuNoteData();
        }
        super.onDestroy();
    }
}