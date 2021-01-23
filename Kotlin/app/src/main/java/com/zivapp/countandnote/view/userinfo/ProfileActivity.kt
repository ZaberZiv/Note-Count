package com.zivapp.countandnote.view.userinfo

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.zivapp.countandnote.R
import com.zivapp.countandnote.databinding.ActivityProfileBinding
import com.zivapp.countandnote.firebase.FirebaseHelper
import com.zivapp.countandnote.view.mainmenu.MainActivity

class ProfileActivity : AppCompatActivity() {

    private val TAG = "ProfileActivity"

    private var mFirebaseHelper = FirebaseHelper()
    private lateinit var mBinding: ActivityProfileBinding

    private var mEditTextUserName: EditText? = null
    private var mEditTextUserPhone: EditText? = null
    private var mButtonConfirm: Button? = null

    private val viewModel = ProfileViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile)

        setViewsByBinding()
        editTextListener()
        buttonConfirmListener()
    }

    private fun setViewsByBinding() {
        mEditTextUserName = mBinding.editTextPersonName
        mEditTextUserPhone = mBinding.editTextPhone
        mButtonConfirm = mBinding.buttonConfirm
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
                mButtonConfirm?.isEnabled = viewModel.validation(
                    mEditTextUserName?.text.toString().trim { it <= ' ' },
                    mEditTextUserPhone?.text.toString().trim { it <= ' ' })
            }
        }
        mEditTextUserName?.addTextChangedListener(afterTextChangedListener)
        mEditTextUserPhone?.addTextChangedListener(afterTextChangedListener)
    }

    private fun buttonConfirmListener() {
        mButtonConfirm?.setOnClickListener {
            val ID = getExtraDataFromIntent()
            val userName = mEditTextUserName?.text.toString().trim { it <= ' ' }
            val userPhone = mEditTextUserPhone?.text.toString().trim { it <= ' ' }
            writeNewUser(ID, userName, userPhone)
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun getExtraDataFromIntent(): String? {
        val arguments = intent.extras
        return arguments?.getString("key")
    }

    private fun writeNewUser(userId: String?, name: String, phone: String) {
        if (userId != null) {
            mFirebaseHelper.getUserNameReference(userId).setValue(name)
            mFirebaseHelper.getUserPhoneReference(userId).setValue(phone)
        }
    }
}