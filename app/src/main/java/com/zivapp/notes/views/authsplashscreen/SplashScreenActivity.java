package com.zivapp.notes.views.authsplashscreen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.zivapp.notes.R;
import com.zivapp.notes.firebase.FirebaseHelper;
import com.zivapp.notes.views.login.LoginActivity;
import com.zivapp.notes.views.mainmenu.MenuNotesActivity;

public class SplashScreenActivity extends AppCompatActivity {
    private static final String TAG = "SplashScreenActivity";
    private final int SPLASH_DISPLAY_LENGTH = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        final FirebaseHelper firebaseHelper = new FirebaseHelper();
//        AppNotifications notifications = new AppNotifications(this);
//        notifications.createNotificationChannel();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser currentUser = firebaseHelper.getFirebaseUser();
                updateUI(currentUser);
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    /**
     * Check if user is signed in (non-null) and update UI accordingly.
     */
    private void updateUI(FirebaseUser user) {
        if (user == null) {
            startNewActivity(LoginActivity.class);
        } else {
            Log.v(TAG, "User already sign in");
            startNewActivity(MenuNotesActivity.class);
        }
    }

    void startNewActivity(Class clazz) {
        Intent intent = new Intent(this, clazz);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        SplashScreenActivity.this.startActivity(intent);
        SplashScreenActivity.this.finish();
    }
}