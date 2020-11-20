package com.zivapp.notes.views.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

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
                animateTextSize(mBinding.signInBtn, 28, 42);
                animateTextSize(mBinding.registerBtn, 42, 28);
                setFragment(new LoginFragment());
                break;
            case R.id.register_btn:
                animateTextSize(mBinding.signInBtn, 42, 28);
                animateTextSize(mBinding.registerBtn, 28, 42);
                setFragment(new RegisterFragment());
        }
    }

    private void setFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                fragment).commit();
    }

    private void animateTextSize(final TextView tv, float startSize, float endSize) {
        long animationDuration = 600; // Animation duration in ms

        ValueAnimator animator = ValueAnimator.ofFloat(startSize, endSize);
        animator.setDuration(animationDuration);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedValue = (float) valueAnimator.getAnimatedValue();
                tv.setTextSize(animatedValue);
            }
        });

        animator.start();
    }
}