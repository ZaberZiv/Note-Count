package com.zivapp.notes.views.mainmenu;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.zivapp.notes.R;

public class MenuNotesActivity extends AppCompatActivity {

    private static final String TAG = "MenuNotesActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_notes);

        new MenuPresenter(this, this);
    }
}