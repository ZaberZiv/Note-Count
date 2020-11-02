package com.zivapp.notes.views.userinfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.databinding.DataBindingUtil;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zivapp.notes.R;
import com.zivapp.notes.databinding.ActivityProfileBinding;
import com.zivapp.notes.views.mainmenu.MenuNotesActivity;

public class ProfilePresenter {

    private static final String TAG = "ProfilePresenter";
    private DatabaseReference mDatabase;
    private ActivityProfileBinding mBinding;
    private EditText mEditTextUserName, mEditTextUserPhone;
    private Button mButtonConfirm;
    private Activity mActivity;

    public ProfilePresenter(Activity activity) {
        mActivity = activity;
        mBinding = DataBindingUtil.setContentView(activity, R.layout.activity_profile);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        setViewsByBinding();
        editTextListener();
        buttonConfirmListener();
    }

    private void setViewsByBinding() {
        mEditTextUserName = mBinding.editTextPersonName;
        mEditTextUserPhone = mBinding.editTextPhone;
        mButtonConfirm = mBinding.buttonConfirm;
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
                if (validation(mEditTextUserName.getText().toString().trim(),
                        mEditTextUserPhone.getText().toString().trim())) {
                    mButtonConfirm.setEnabled(true);
                } else {
                    mButtonConfirm.setEnabled(false);
                }
            }
        };
        mEditTextUserName.addTextChangedListener(afterTextChangedListener);
        mEditTextUserPhone.addTextChangedListener(afterTextChangedListener);
    }

    public boolean validation(String name, String phone) {
        boolean valid = true;

        if (name.isEmpty() || name.equals("") || phone.isEmpty() || phone.equals("")) return false;

        if (name.length() >= 2) {
            Log.v(TAG, "Success! NAME length is: " + name.length());

        } else {
            Log.v(TAG, "Failure! NAME length is: " + name.length());
            valid = false;
        }

        if (phone.length() == 11) {
            Log.v(TAG, "Success! PHONE length is: " + phone.length());
        } else {
            Log.v(TAG, "Failure! PHONE length is: " + phone.length());
            valid = false;
        }

        return valid;
    }

    void buttonConfirmListener() {
        mButtonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ID = getExtraDataFromIntent("key");
                String userName = mEditTextUserName.getText().toString().trim();
                String userPhone = mEditTextUserPhone.getText().toString().trim();

                writeNewUser(ID, userName, userPhone);

                Intent intent = new Intent(mActivity, MenuNotesActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                mActivity.startActivity(intent);
            }
        });
    }

    public String getExtraDataFromIntent(String key) {
        Bundle arguments = mActivity.getIntent().getExtras();
        return arguments.getString(key);
    }

    void writeNewUser(String userId, String name, String phone) {
        mDatabase.child("users").child(userId).child("name").setValue(name);
        mDatabase.child("users").child(userId).child("phone").setValue(phone);
    }
}