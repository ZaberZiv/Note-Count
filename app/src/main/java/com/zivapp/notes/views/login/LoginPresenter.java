package com.zivapp.notes.views.login;

import android.util.Patterns;
import android.widget.EditText;

public class LoginPresenter {

    public boolean validateForm(EditText userEmail, EditText userPassword) {
        boolean valid = true;

        String email = userEmail.getText().toString().trim();
        if (isUserEmailValid(email)) {
            userEmail.setError(null);
        } else {
            userEmail.setError("Required.");
            valid = false;
        }

        String password = userPassword.getText().toString().trim();
        if (isPasswordValid(password)) {
            userPassword.setError(null);
        } else {
            userPassword.setError("Required.");
            valid = false;
        }

        return valid;
    }

    // A placeholder user email validation check
    public boolean isUserEmailValid(String userEmail) {
        if (userEmail == null) {
            return false;
        }
        if (userEmail.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(userEmail).matches();
        } else {
            return !userEmail.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    public boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }

    // Confirmation if second password is equals to first
    public boolean isPasswordConfirm(String password, String confirmPassword) {
        return confirmPassword.equals(password);
    }
}
