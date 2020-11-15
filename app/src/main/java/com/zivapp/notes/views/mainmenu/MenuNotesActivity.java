package com.zivapp.notes.views.mainmenu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.zivapp.notes.R;
import com.zivapp.notes.views.login.LoginActivity;

public class MenuNotesActivity extends AppCompatActivity {

    private static final String TAG = "MenuNotesActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_notes);

        new MenuPresenter(this, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * Sign Out Button. Redirect to LoginActivity.
     */
    public void signOutButton(MenuItem item) {
        Log.v(TAG, "User signed out!");
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(MenuNotesActivity.this, LoginActivity.class));
    }
}