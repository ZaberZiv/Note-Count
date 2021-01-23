package com.zivapp.countandnote.view.userinfo

import android.util.Log

class ProfileViewModel {

    private val TAG = "ProfileViewModel"

    fun validation(name: String, phone: String): Boolean {
        var valid = true
        if (name.isEmpty() || name == "" || phone.isEmpty() || phone == "") return false

        if (name.length >= 2) {
            Log.v(TAG, "Success! NAME length is: " + name.length)
        } else {
            Log.v(TAG, "Failure! NAME length is: " + name.length)
            valid = false
        }

        if (phone.length == 11) {
            Log.v(TAG, "Success! PHONE length is: " + phone.length)
        } else {
            Log.v(TAG, "Failure! PHONE length is: " + phone.length)
            valid = false
        }

        return valid
    }
}