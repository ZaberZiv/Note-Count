package com.zivapp.notes.views.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.View;

import com.zivapp.notes.R;
import com.zivapp.notes.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";
    private ActivityLoginBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        mBinding.signInBtn.setOnClickListener(this);
        mBinding.registerBtn.setOnClickListener(this);

        setFragment(new LoginFragment());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_btn:
                setFragment(new LoginFragment());
                break;
            case R.id.register_btn:
                setFragment(new RegisterFragment());
        }
    }

    private void setFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                fragment).commit();
    }
}