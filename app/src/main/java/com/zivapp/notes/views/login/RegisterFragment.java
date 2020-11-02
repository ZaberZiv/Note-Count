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
import com.zivapp.notes.views.login.LoginPresenter;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zivapp.notes.databinding.FragmentRegisterBinding;
import com.zivapp.notes.model.User;
import com.zivapp.notes.views.userinfo.ProfileActivity;

public class RegisterFragment extends Fragment {

    private static final String TAG = "RegisterFragment";

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private FragmentRegisterBinding mBinding;
    private LoginPresenter mLoginPresenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentRegisterBinding.inflate(inflater, container, false);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mLoginPresenter = new LoginPresenter();

        editTextListener();
        setRegisterButton();
        return mBinding.getRoot();
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
                if (mLoginPresenter.isPasswordConfirm(mBinding.password.getText().toString().trim(),
                        mBinding.confirmPassword.getText().toString().trim())) {

                    mBinding.registerBtn.setEnabled(true);
                } else {
                    mBinding.registerBtn.setEnabled(false);
                }
            }
        };
        mBinding.confirmPassword.addTextChangedListener(afterTextChangedListener);
    }

    private void setRegisterButton() {
        mBinding.registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerNewUser(mBinding.usernameEmail.getText().toString().trim(),
                        mBinding.password.getText().toString().trim());
            }
        });
    }

    private void registerNewUser(String email, String password) {
        if (!mLoginPresenter.validateForm(mBinding.usernameEmail, mBinding.password)) return;

        showProgressBar();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.v(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.v(TAG, "createUserWithEmail:failure", task.getException());
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

        String UID = user.getUid();
        String email = mBinding.usernameEmail.getText().toString().trim();
        writeNewUser(UID, email);

        Intent intent = new Intent(getActivity(), ProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("key", UID);
        intent.putExtra("email", email);
        startActivity(intent);
    }

    private void showProgressBar() {
        mBinding.loading.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        mBinding.loading.setVisibility(View.GONE);
    }

    private void writeNewUser(String userId, String email) {
        User user = new User("", email, "");
        mDatabase.child("users").child(userId).setValue(user);
    }
}