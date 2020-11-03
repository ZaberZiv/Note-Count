package com.zivapp.notes.network;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Saving data on local database if offline
 */
public class NetworkOfflineSecurity extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
