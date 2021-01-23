package com.zivapp.countandnote.view.mainmenu

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.zivapp.countandnote.R
import com.zivapp.countandnote.adapters.AdapterMenu
import com.zivapp.countandnote.databinding.FragmentMainMenuBinding
import com.zivapp.countandnote.model.FormatSum
import com.zivapp.countandnote.model.MainMenuNote
import com.zivapp.countandnote.view.contacts.ContactsListActivity
import com.zivapp.countandnote.view.login.LoginActivity
import com.zivapp.countandnote.view.note.NoteActivity

class MainMenuFragment : Fragment() {

    private val TAG = "MainMenuFragment"

    companion object {
        fun newInstance() = MainMenuFragment()
    }

    private var mBinding: FragmentMainMenuBinding? = null
    private val binding get() = mBinding!!
    private lateinit var viewModel: MainMenuViewModel
    private var mRecyclerView: RecyclerView? = null
    private lateinit var mAdapter: AdapterMenu

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentMainMenuBinding.inflate(inflater, container, false)
        val view = binding.root

        menu()
        loadRecyclerView()
        noteButton()
        groupNoteButton()

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainMenuViewModel::class.java)

        firebaseInstances()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }

    private fun firebaseInstances() {
        progressbarOn()
        viewModel.getMutableListOfMainMenuNote().observe(viewLifecycleOwner, {
            updateNoteUI(it)
            progressbarOff()
        })
    }

    private fun updateNoteUI(list: List<MainMenuNote>) {
        updateUI(list)
        mBinding?.formatSum = FormatSum(countNotes = list.size)
    }

    private fun progressbarOn() {
        mBinding?.progressBar?.visibility = View.VISIBLE
    }

    private fun progressbarOff() {
        mBinding?.progressBar?.visibility = View.GONE
    }

    private fun menu() {
        setHasOptionsMenu(true)
        val toolbar = mBinding!!.toolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.v(TAG, "onOptionsItemSelected OUT")
        return when (item.itemId) {
            R.id.menu_sign_out -> {
                Log.v(TAG, "onOptionsItemSelected IN")
                signOutButton()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun signOutButton() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun loadRecyclerView() {
        mRecyclerView = mBinding?.recycler
        mRecyclerView?.layoutManager = StaggeredGridLayoutManager(
                2,
                StaggeredGridLayoutManager.VERTICAL
        )
        try {
            mAdapter = context?.let { AdapterMenu(it) }!!
        } catch (ex: Exception) {
            println(ex.message)
            Log.v(TAG, "Context has produced NULL")
        }
        mRecyclerView?.adapter = mAdapter
    }

    private fun updateUI(list: List<MainMenuNote>) {
        mAdapter.notesList = list
    }

    private fun noteButton() {
        mBinding?.noteButton?.setOnClickListener {
            startActivity(
                    Intent(context, NoteActivity::class.java)
            )
        }
    }

    private fun groupNoteButton() {
        mBinding?.groupButton?.setOnClickListener {
            val intent = Intent(context, ContactsListActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
            startActivity(intent)
        }
    }
}