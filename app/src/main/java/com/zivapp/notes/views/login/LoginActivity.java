package com.zivapp.notes.views.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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
                animateTextSize(mBinding.signInBtn, 38, 42);
                animateTextSize(mBinding.registerBtn, 42, 38);
                mBinding.registerBtn.setTextColor(getResources().getColor(R.color.grey));
                mBinding.signInBtn.setTextColor(getResources().getColor(R.color.colorAccent));

                setFragment(new LoginFragment());
                break;
            case R.id.register_btn:
                animateTextSize(mBinding.signInBtn, 42, 38);
                animateTextSize(mBinding.registerBtn, 38, 42);
                mBinding.signInBtn.setTextColor(getResources().getColor(R.color.grey));
                mBinding.registerBtn.setTextColor(getResources().getColor(R.color.colorAccent));

                setFragment(new RegisterFragment());
        }
    }

    private void setFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_left_to_right)
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void animateTextSize(final TextView tv, float startSize, float endSize) {
        long animationDuration = 300; // Animation duration in ms

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