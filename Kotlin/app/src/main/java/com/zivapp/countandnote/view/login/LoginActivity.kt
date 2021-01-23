package com.zivapp.countandnote.view.login

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.zivapp.countandnote.R
import com.zivapp.countandnote.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private val TAG = "LoginActivity"
    private lateinit var mBinding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        mBinding.signInBtn.setOnClickListener(this)
        mBinding.registerBtn.setOnClickListener(this)

        setFragment(LoginFragment())
    }

    private fun setFragment(fragment: Fragment) {
        supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_left_to_right)
                .replace(R.id.fragment_container, fragment)
                .commit()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.sign_in_btn -> {
                animateTextSize(mBinding.signInBtn, 38f, 42f)
                animateTextSize(mBinding.registerBtn, 42f, 38f)
                mBinding.registerBtn.setTextColor(ContextCompat.getColor(this, R.color.grey))
                mBinding.signInBtn.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
                setFragment(LoginFragment())
            }
            R.id.register_btn -> {
                animateTextSize(mBinding.signInBtn, 42f, 38f)
                animateTextSize(mBinding.registerBtn, 38f, 42f)
                mBinding.signInBtn.setTextColor(ContextCompat.getColor(this, R.color.grey))
                mBinding.registerBtn.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
                setFragment(RegisterFragment())
            }
        }
    }

    private fun animateTextSize(tv: TextView, startSize: Float, endSize: Float) {
        val animationDuration: Long = 300 // Animation duration in ms
        val animator = ValueAnimator.ofFloat(startSize, endSize)
        animator.duration = animationDuration
        animator.addUpdateListener { valueAnimator ->
            val animatedValue = valueAnimator.animatedValue as Float
            tv.textSize = animatedValue
        }
        animator.start()
    }
}