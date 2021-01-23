package com.zivapp.countandnote.view.login

import android.app.Application
import android.util.Patterns
import android.widget.EditText
import androidx.lifecycle.AndroidViewModel

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG = "LoginViewModel"

    fun validateForm(userEmail: EditText, userPassword: EditText): Boolean {
        var valid = true
        val email = userEmail.text.toString().trim()
        if (isUserEmailValid(email)) {
            userEmail.error = null
        } else {
            userEmail.error = "Required."
            valid = false
        }
        val password = userPassword.text.toString().trim()
        if (isPasswordValid(password)) {
            userPassword.error = null
        } else {
            userPassword.error = "Required."
            valid = false
        }
        return valid
    }

    // A placeholder user email validation check
    private fun isUserEmailValid(userEmail: String?): Boolean {
        if (userEmail == null) {
            return false
        }
        return if (userEmail.contains("@")) {
            Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()
        } else {
            userEmail.trim { it <= ' ' }.isNotEmpty()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String?): Boolean {
        return password != null && password.trim { it <= ' ' }.length > 5
    }

    // Confirmation if second password is equals to first
    fun isPasswordConfirm(password: String, confirmPassword: String): Boolean {
        return confirmPassword == password
    }
}