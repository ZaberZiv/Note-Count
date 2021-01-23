package com.zivapp.countandnote.view.groups

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.zivapp.countandnote.R
import com.zivapp.countandnote.adapters.AdapterGroupNote
import com.zivapp.countandnote.databinding.FragmentGroupNoteBinding
import com.zivapp.countandnote.model.*
import com.zivapp.countandnote.util.UtilConverter
import com.zivapp.countandnote.util.UtilDate

class GroupNoteFragment : Fragment(), GroupIDProvider {

    private val TAG = "GroupNoteFragment"
    private lateinit var mBinding: FragmentGroupNoteBinding

    private var mNoteList = listOf<GroupNote>()
    private var mMembersList = listOf<User>()

    private lateinit var mMainMenuNote: MainMenuNote
    private lateinit var mUser: User

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: AdapterGroupNote

    private var ID: String = ""
    private var mTotalSum = 0
    private var mFlag = false

    private val viewModel: GroupNoteViewModel by viewModels()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentGroupNoteBinding.inflate(inflater, container, false)
        val view = mBinding.root

        mUser = viewModel.getUserFromFirebase()
        loadRecyclerView()
        ID = checkID()
        addNewNoteItem()

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.note_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_share -> {
                context?.let { viewModel.shareData(it) }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Getting intent Extra ID from MainMenuFragment
     * and checking if there is note in database or not
     * if Yes - loading data from DB
     * if No - generate new ID
     */
    private fun checkID(): String {
        val arguments = activity?.intent?.extras
        val uID: String
        if (arguments?.getString("id_note") != null) { // if exist in database
            uID = arguments.getString("id_note").toString()
            Log.v(TAG, "This note is already exist! ID: $uID")

            viewModel.setReferences(uID)
            getDataFromFirebase()
            getTotalDataFromFirebase()
            getMembersFromFirebase()
            mFlag = false

        } else { // if created in contacts
            Log.v(TAG, "New Note!")
            uID = getExtraData()

            viewModel.setReferences(uID)
            getDataFromFirebase()
            getMembersFromFirebase()
            mFlag = true
        }
        return uID
    }

    private fun getExtraData(): String {
        val arguments = activity?.intent?.extras
        var key = ""
        if (arguments != null) {
            key = arguments.getString("key").toString()
        }
        return key
    }

    private fun getDataFromFirebase() {
        viewModel.getDataFromFirebase().observe(viewLifecycleOwner, {
            mNoteList = it
            updateGroupNoteUI(it)
        })
    }

    private fun getTotalDataFromFirebase() {
        viewModel.getTotalDataFromFirebase().observe(viewLifecycleOwner, {
            mMainMenuNote = it
            updateTotalDataUI(it)
        })
    }

    private fun getMembersFromFirebase() {
        viewModel.getMembersFromFirebase().observe(viewLifecycleOwner, {
            mMembersList = it
        })
    }

    private fun loadRecyclerView() {
        mRecyclerView = mBinding.recyclerGroups
        mRecyclerView.layoutManager = StaggeredGridLayoutManager(
                1,
                StaggeredGridLayoutManager.VERTICAL)
        mAdapter = AdapterGroupNote(this)
        mRecyclerView.adapter = mAdapter
    }

    private fun updateUI(list: MutableList<GroupNote>) {
        mAdapter.list = list
    }

    /**
     * "Add" button - get data from all EditTexts
     * and add data to the recyclerView and bind to XML file,
     * notify adapter that data has changed.
     * EditText initialized here and made some work with it.
     */
    private fun addNewNoteItem() {
        val editTextTitle = mBinding.toolbar.etNoteTitleName
        val editTextName = mBinding.includeInterface.etNameOperation
        val editTextPrice = mBinding.includeInterface.etPriceOperation
        editTextTitle.requestFocus()

        // Add button
        mBinding.includeInterface.buttonAddNewItem.setOnClickListener { // checking if edit text not empty
            if (editTextName.text.toString().trim { it <= ' ' } == ""
                    || editTextPrice.text.toString().trim { it <= ' ' } == "") {
                Toast.makeText(activity,
                        R.string.no_data,
                        Toast.LENGTH_SHORT).show()
            }

            val message = editTextName.text.toString().trim { it <= ' ' }
            val sum = editTextPrice.text.toString().trim { it <= ' ' }.toInt()
            setTotalSum(mNoteList, sum)

            // Saving message
            saveGroupNote(message, sum)
            //Saving total sum
            saveTotalData()

            editTextName.text.clear()
            editTextPrice.text.clear()
            editTextName.requestFocus()
        }
    }

    /**
     * Count getSum() from every Note.class object
     * and bind total sum to XML
     */
    private fun setTotalSum(list: List<GroupNote>, price: Int) {
        mTotalSum = list.sumBy { it.sum } + price
        setFormatTotalSum(mTotalSum)
    }

    private fun setFormatTotalSum(totalSum: Int) {
        mBinding.toolbar.format = FormatSum(UtilConverter.customStringFormat(totalSum))
    }

    private fun saveGroupNote(message: String, price: Int) {
        val gNote = GroupNote()
        gNote.message = message
        gNote.sum = price
        gNote.member = mUser.name
        gNote.date = UtilDate.getGroupDate()
        gNote.Uid = viewModel.getFirebaseUserID()
        gNote.group_id = ID
        saveGroupNoteInFirebase(gNote)
    }

    private fun saveGroupNoteInFirebase(gNote: GroupNote) {
        viewModel.saveGroupNoteInFirebase(gNote)
    }

    private fun saveTotalData() {
        val mainMenuNote = MainMenuNote()
        mainMenuNote.date = getCurrentDate()
        mainMenuNote.title = getTitleName()
        mainMenuNote.total_sum = mTotalSum
        mainMenuNote.id = ID
        mainMenuNote.group = true
        mainMenuNote.message = getMessage()
        saveTotalDataInFirebase(mainMenuNote)
    }

    private fun saveTotalDataInFirebase(mainMenuNote: MainMenuNote) {
        // Saving data of members to the branch "Total Data" -> member id -> note id -> data
        for (user in mMembersList) {
            viewModel.saveTotalDataInFirebase(user.id, ID, mainMenuNote)
            Log.v(TAG, "user.getId(): ${user.id}, ID: $ID")
        }
        // Saving data of current user to the branch "Total Data" -> note id -> data
        viewModel.saveTotalDataCurrentUser(ID, mainMenuNote)

        // Saving data to the current "Groups" -> "Total Data"
        viewModel.saveGroupsInTotalData(mainMenuNote)
    }

    private fun getCurrentDate(): String {
        return UtilDate.getCurrentDate()
    }

    private fun getTitleName(): String {
        return mBinding.toolbar.etNoteTitleName.text.toString().trim()
    }

    private fun isFlag(): Boolean {
        return mFlag
    }

    private fun getMessage(): String {
        return mBinding.includeHintMessage.editTextMessage.text.toString().trim()
    }

    private fun updateGroupNoteUI(noteList: MutableList<GroupNote>) {
        setTotalSum(noteList, 0)
        updateUI(noteList)
    }

    private fun updateTotalDataUI(mainMenuNote: MainMenuNote) {
        setMainMenuNoteDataInLayout(mainMenuNote)
        changeFocusIfTitleExist(mainMenuNote)
    }

    /**
     * set data in xml file
     */
    private fun setMainMenuNoteDataInLayout(mainMenuNote: MainMenuNote) {
        mBinding.toolbar.mainNote = mainMenuNote
        mBinding.includeHintMessage.mainNote = mainMenuNote
    }

    /**
     * changing focus on EditText "Add name" if Title exist.
     */
    private fun changeFocusIfTitleExist(mainMenuNote: MainMenuNote?) {
        if (mainMenuNote == null) return
        if (mainMenuNote.title != "") mBinding.includeInterface.etNameOperation.requestFocus()
    }

    override fun getCurrentNoteID(): String {
        return ID
    }

    override fun onDestroyView() {
        Log.v(TAG, "onDestroyView()")
        if (isFlag()) {
            saveMainMenuNoteData()
        } else {
            updateMainMenuNoteData()
        }
        super.onDestroyView()
    }

    /**
     * Saving data, in GroupNoteActivity;
     */
    private fun saveMainMenuNoteData() {
        Log.v(TAG, "saveMainMenuNoteData()")
        if (mTotalSum != 0) {
            Log.v(TAG, "DATA SAVED for the first time!")
            saveTotalData()
        } else {
            Log.v(TAG, "Empty note haven't saved!")
        }
    }

    /**
     * Updating data, in GroupNoteActivity;
     */
    private fun updateMainMenuNoteData() {
        Log.v(TAG, "updateMainMenuNoteData()")

        // If title or total sum has changed than update data
        if (getMessage() != mMainMenuNote.message) {
            Log.v(TAG, "mMainMenuNote is NULL")
            saveTotalData()
        } else if (mMainMenuNote.title != getTitleName()
                || mMainMenuNote.total_sum != mTotalSum) {
            Log.v(TAG, "DATA UPDATED")
            saveTotalData()
        } else {
            Log.v(TAG, "Note haven't changed!")
        }
    }
}