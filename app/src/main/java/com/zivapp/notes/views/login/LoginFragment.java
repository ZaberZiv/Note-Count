package com.zivapp.notes.views.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.zivapp.notes.databinding.FragmentLoginBinding;
import com.zivapp.notes.firebase.FirebaseHelper;
import com.zivapp.notes.views.mainmenu.MenuNotesActivity;

public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment";
    private FragmentLoginBinding mBinding;
    private FirebaseHelper mFirebaseHelper;
    private LoginPresenter mLoginPresenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentLoginBinding.inflate(inflater, container, false);
        mFirebaseHelper = new FirebaseHelper();
        mLoginPresenter = new LoginPresenter();

        setSignInButton();
        editTextListener();
        return mBinding.getRoot();
    }

    private void setSignInButton() {
        mBinding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInUser(mBinding.userEmail.getText().toString().trim(),
                        mBinding.password.getText().toString().trim());
            }
        });
    }

    private void signInUser(String email, String password) {
        if (!mLoginPresenter.validateForm(mBinding.userEmail, mBinding.password)) return;

        showProgressBar();

        mFirebaseHelper.getFirebaseAuth().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mFirebaseHelper.getFirebaseUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                        hideProgressBar();
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user == null) return;

        Intent intent = new Intent(getActivity(), MenuNotesActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void showProgressBar() {
        mBinding.loading.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        mBinding.loading.setVisibility(View.GONE);
    }

    private void editTextListener() {
        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!mBinding.userEmail.getText().toString().trim().equals("") &&
                !mBinding.password.getText().toString().trim().equals("")) {

                    mBinding.loginBtn.setEnabled(true);
                } else {
                    mBinding.loginBtn.setEnabled(false);
                }
            }
        };
        mBinding.password.addTextChangedListener(afterTextChangedListener);
    }
}
