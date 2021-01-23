package com.zivapp.countandnote.view.splashscreen

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseUser
import com.zivapp.countandnote.R
import com.zivapp.countandnote.firebase.FirebaseHelper
import com.zivapp.countandnote.view.login.LoginActivity
import com.zivapp.countandnote.view.mainmenu.MainActivity
import java.lang.NullPointerException

class SplashScreenActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "SplashScreenActivity"
        private const val SPLASH_DISPLAY_LENGTH = 1000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler().postDelayed({
            try {
                val firebaseHelper = FirebaseHelper()
                val currentUser = firebaseHelper.getFirebaseUser()
                updateUI(currentUser)
            } catch (ex: NullPointerException) {
                println(ex.message)
                updateUI(null)
            }

        }, SPLASH_DISPLAY_LENGTH.toLong())
    }

    /**
     * Check if user is signed in (non-null) and update UI accordingly.
     */
    private fun updateUI(user: FirebaseUser?) {
        if (user == null) {
            startNewActivity(LoginActivity::class.java)
        } else {
            Log.v(TAG, "User already sign in")
            startNewActivity(MainActivity::class.java)
        }
    }

    private fun startNewActivity(clazz: Class<*>?) {
        val intent = Intent(this, clazz)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}