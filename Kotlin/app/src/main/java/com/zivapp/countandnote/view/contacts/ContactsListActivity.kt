package com.zivapp.countandnote.view.contacts

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.zivapp.countandnote.R
import com.zivapp.countandnote.adapters.AdapterContacts
import com.zivapp.countandnote.databinding.ActivityContactsListBinding
import com.zivapp.countandnote.model.User
import com.zivapp.countandnote.view.groups.GroupNoteActivity
import java.util.*

class ContactsListActivity : AppCompatActivity(), SelectedUsersListener {

    private val TAG = "ContactsListActivity"
    private val REQUEST_READ_CONTACTS = 79

    private var mContactsList = listOf<User>()
    private var mFirebaseUsersList = listOf<User>()

    private lateinit var mBinding: ActivityContactsListBinding
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: AdapterContacts

    private val viewModel: ContactsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts_list)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_contacts_list)

        loadRecyclerView()
        firebaseInstances()

        Handler().postDelayed(
            { updateUI(findMatchedUsers(mContactsList, mFirebaseUsersList)) },
            300
        )

        setBackArrow()
        checkPermission()
        buttonSelectUsers()
        refreshFirebaseCallback()
    }

    private fun setBackArrow() {
        setSupportActionBar(findViewById<View>(R.id.toolbar) as Toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun checkPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mContactsList = getAllContacts()
        } else {
            requestPermission()
        }
    }

    private fun requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.READ_CONTACTS
            )
        ) {
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.READ_CONTACTS),
                REQUEST_READ_CONTACTS
            )
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.READ_CONTACTS
            )
        ) {
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.READ_CONTACTS),
                REQUEST_READ_CONTACTS
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mContactsList = getAllContacts()
            } else {
                // permission denied, Disable the
                // functionality that depends on this permission.
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getAllContacts(): MutableList<User> {
        val userList = ArrayList<User>()
        val cr = contentResolver
        val cur = cr.query(
            ContactsContract.Contacts.CONTENT_URI,
            null, null, null, null
        )
        if (cur?.count ?: 0 > 0) {
            while (cur != null && cur.moveToNext()) {
                val user = User()
                val id = cur.getString(
                    cur.getColumnIndex(
                        ContactsContract.Contacts._ID
                    )
                )
                val name = cur.getString(
                    cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME
                    )
                )
                if (name != null) {
                    user.name = name
                }
                if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    val pCur = cr.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        arrayOf(id),
                        null
                    )
                    while (pCur!!.moveToNext()) {
                        val phoneNo = pCur.getString(
                            pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER
                            )
                        )
                        if (phoneNo != null) {
                            user.phone = phoneNo
                        }
                    }
                    pCur.close()
                }
                if (user.name != "") {
                    userList.add(user)
                }
            }
        }
        cur?.close()
        return userList
    }

    private fun findMatchedUsers(
        contactsList: List<User>,
        firebaseUsersList: List<User>
    ): List<User> {
        val listOfMatchedUsers = ArrayList<User>()
        Log.v(TAG, "Match loop: starts working")
        for (user in firebaseUsersList.indices) {
            for (contact in contactsList.indices) {
                if (firebaseUsersList[user].phone == contactsList[contact].phone) {
                    listOfMatchedUsers.add(firebaseUsersList[user])
                    Log.v(
                        TAG, "WE HAVE MATCH: " + firebaseUsersList[user].phone
                    )
                }
            }
        }
        if (listOfMatchedUsers.size == 0) {
            mBinding.noFriendsLayout.visibility = View.VISIBLE
        } else {
            mBinding.noFriendsLayout.visibility = View.GONE
        }
        Log.v(TAG, "Match loop: finished working")
        return listOfMatchedUsers
    }

    private fun loadRecyclerView() {
        mRecyclerView = mBinding.recyclerContacts
        mRecyclerView.layoutManager = StaggeredGridLayoutManager(
            1,
            StaggeredGridLayoutManager.VERTICAL
        )
        mAdapter = AdapterContacts(this)
        mRecyclerView.adapter = mAdapter
    }

    private fun updateUI(list: List<User>) {
        mAdapter.list = list
    }

    private fun firebaseInstances() {
        mFirebaseUsersList = viewModel.getDataFromFirebase()
    }

    private fun buttonSelectUsers() {
        mBinding.btnAddContacts.setOnClickListener {
            viewModel.firebase()
            val listUsers = mAdapter.getSelectedUsers()

            // this cycle is for adding data to other members roots
            for (user in listUsers) {
                // adding users to the Group | "Groups" -> group id -> "members" -> member uID -> value (false - for group members)
                viewModel.addUserToTheGroup(user.id)

                Log.v(TAG,"User: " + user.name + ", id: " + user.id)
            }
            Log.v(TAG, "mGroupIDReference KEY: " + viewModel.getGroupKey())
            openNewActivityWithData(viewModel.getGroupKey())
        }
    }

    private fun openNewActivityWithData(key: String?) {
        val intent = Intent(this, GroupNoteActivity::class.java)
        intent.putExtra("key", key)
        startActivity(intent)
    }

    //TODO: doesn't work
    private fun refreshFirebaseCallback() {
        mBinding.refreshLayout.setOnRefreshListener {
            Log.v(TAG, "Page Refreshed")
            mFirebaseUsersList = viewModel.getDataFromFirebase()
            updateUI(findMatchedUsers(mContactsList, mFirebaseUsersList))
            mBinding.refreshLayout.isRefreshing = false
        }
    }

    // realized in AdapterContacts
    override fun onSelectedAction(isSelected: Boolean) {
        if (isSelected) {
            showOrHideButton(-1)
        } else {
            showOrHideButton(150)
        }
    }

    private fun showOrHideButton(translationY: Int, duration: Int = 300) {
        mBinding.btnAddContacts.animate().translationY(translationY.toFloat()).duration =
            duration.toLong()
    }
}