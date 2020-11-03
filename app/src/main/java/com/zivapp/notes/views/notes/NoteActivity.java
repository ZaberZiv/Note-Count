package com.zivapp.notes.views.notes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;

import com.zivapp.notes.R;
import com.zivapp.notes.model.FormatSum;
import com.zivapp.notes.util.UtilConverter;

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