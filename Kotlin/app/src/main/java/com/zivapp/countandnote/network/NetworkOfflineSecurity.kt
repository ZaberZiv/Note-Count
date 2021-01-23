package com.zivapp.countandnote.network

import android.app.Application
import com.google.firebase.database.FirebaseDatabase

/**
 * Saving data on local database if offline
 */
class NetworkOfflineSecurity : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }
}