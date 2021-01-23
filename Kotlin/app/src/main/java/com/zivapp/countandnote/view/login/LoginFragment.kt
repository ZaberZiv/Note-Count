package com.zivapp.countandnote.view.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.firebase.auth.FirebaseUser
import com.zivapp.countandnote.R
import com.zivapp.countandnote.databinding.FragmentLoginBinding
import com.zivapp.countandnote.firebase.FirebaseHelper
import com.zivapp.countandnote.view.mainmenu.MainActivity
import java.lang.NullPointerException

class LoginFragment : Fragment() {

    private val TAG = "LoginFragment"
    private lateinit var mBinding: FragmentLoginBinding
    private val mFirebaseHelper = FirebaseHelper()

    private val viewModel: LoginViewModel by activityViewModels()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        mBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_login,
                container, false
        )
        val view = mBinding.root

        setSignInButton()
        editTextListener()

        return view
    }

    private fun setSignInButton() {
        mBinding.loginBtn.setOnClickListener {
            signInUser(
                    mBinding.includeLoginEditText.userEmail.text.toString().trim(),
                    mBinding.includeLoginEditText.password.text.toString().trim()
            )
        }
    }

    private fun signInUser(email: String, password: String) {
        if (!viewModel.validateForm(
                        mBinding.includeLoginEditText.userEmail,
                        mBinding.includeLoginEditText.password
                )) return

        showProgressBar()

        mFirebaseHelper.getFirebaseAuth().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success")

                        val user: FirebaseUser
                        try {
                            user = mFirebaseHelper.getFirebaseUser()
                            updateUI(user)

                        } catch (ex: NullPointerException) {
                            println(ex.message)
                            updateUI(null)
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(
                                activity, "Authentication failed.",
                                Toast.LENGTH_SHORT
                        ).show()
                        updateUI(null)
                    }
                    hideProgressBar()
                }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user == null) return
        val intent = Intent(activity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun showProgressBar() {
        mBinding.loading.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        mBinding.loading.visibility = View.GONE
    }

    private fun editTextListener() {
        val afterTextChangedListener: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // ignore
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // ignore
            }

            override fun afterTextChanged(s: Editable) {
                mBinding.loginBtn.isEnabled =
                        mBinding.includeLoginEditText.userEmail.text.toString().trim() != "" &&
                        mBinding.includeLoginEditText.password.text.toString().trim() != ""
            }
        }
        mBinding.includeLoginEditText.password.addTextChangedListener(afterTextChangedListener)
    }
}