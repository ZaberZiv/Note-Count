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
import com.zivapp.countandnote.databinding.FragmentRegisterBinding
import com.zivapp.countandnote.firebase.FirebaseHelper
import com.zivapp.countandnote.view.userinfo.ProfileActivity

class RegisterFragment : Fragment() {

    private val TAG = "RegisterFragment"

    private lateinit var mBinding: FragmentRegisterBinding
    private val mFirebaseHelper = FirebaseHelper()
    private val viewModel: LoginViewModel by activityViewModels()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        mBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_register,
                container, false
        )
        val view = mBinding.root

        editTextListener()
        setRegisterButton()

        return view
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
                mBinding.registerBtn.isEnabled = viewModel.isPasswordConfirm(
                        mBinding.includeLoginEditText.password.text.toString().trim(),
                        mBinding.confirmPassword.text.toString().trim()
                )
            }
        }
        mBinding.confirmPassword.addTextChangedListener(afterTextChangedListener)
    }

    private fun setRegisterButton() {
        mBinding.registerBtn.setOnClickListener {
            registerNewUser(
                    mBinding.includeLoginEditText.userEmail.text.toString().trim(),
                    mBinding.includeLoginEditText.password.text.toString().trim()
            )
        }
    }

    private fun registerNewUser(email: String, password: String) {
        if (!viewModel.validateForm(
                        mBinding.includeLoginEditText.userEmail,
                        mBinding.includeLoginEditText.password
                )) return
        showProgressBar()
        mFirebaseHelper.getFirebaseAuth().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                        requireActivity()
                ) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.v(TAG, "createUserWithEmail:success")
                        val user = mFirebaseHelper.getFirebaseUser()
                        updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.v(TAG, "createUserWithEmail:failure", task.exception)
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
        val uID = user.uid
        val email = mBinding.includeLoginEditText.userEmail.text.toString().trim()
        writeNewUser(uID, email)
        val intent = Intent(activity, ProfileActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra("key", uID)
        startActivity(intent)
    }

    private fun showProgressBar() {
        mBinding.loading.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        mBinding.loading.visibility = View.GONE
    }

    private fun writeNewUser(userId: String, email: String) {
        mFirebaseHelper.getUserEmailReference(userId).setValue(email)
    }
}