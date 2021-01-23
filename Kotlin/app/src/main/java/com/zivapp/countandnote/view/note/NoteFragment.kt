package com.zivapp.countandnote.view.note

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zivapp.countandnote.R
import com.zivapp.countandnote.adapters.AdapterNote
import com.zivapp.countandnote.databinding.FragmentNoteBinding
import com.zivapp.countandnote.model.FormatSum
import com.zivapp.countandnote.model.MainMenuNote
import com.zivapp.countandnote.model.Note
import com.zivapp.countandnote.util.UtilConverter
import com.zivapp.countandnote.util.UtilDate
import java.util.*

class NoteFragment : Fragment() {

    private val TAG = "NoteFragment"
    private var ID: String = "null"
    private var mTotalSum = 0
    private var mFlag = false
    private var mNoteList = listOf<Note>()
    private lateinit var mMainMenuNote: MainMenuNote

    private lateinit var mBinding: FragmentNoteBinding
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: AdapterNote

    private val viewModel: NoteViewModel by activityViewModels()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentNoteBinding.inflate(inflater, container, false)
        val view = mBinding.root

        menu()
        loadRecyclerView()
        ID = checkID()
        addNewNoteItemButton()

        return view
    }

    private fun menu() {
        setHasOptionsMenu(true)
        val toolbar = mBinding.toolbar.toolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_share -> {
                context?.let { viewModel.shareData(it) }
                true
            }
            else -> false
        }
    }

    /**
     * Getting intent Extra ID from MenuNotesActivity
     * and checking if there is note in database or not
     * if Yes - loading data from Firebase
     * if No - generate new ID
     */
    private fun checkID(): String {
        val arguments = activity?.intent?.extras
        val uID: String

        // if already exist in database
        if (arguments != null) {
            uID = arguments.getString("id_note").toString()
            Log.v(TAG, "This note is already exist! ID: $uID")

            getDataFromFirebase(uID)
            getTotalDataFromFirebase(uID)
            mFlag = false

        } else { // if not exist in database
            Log.v(TAG, "New Note!")

            // generate new ID
            uID = UUID.randomUUID().toString()
            getDataFromFirebase(uID)
            mFlag = true
        }
        return uID
    }

    /**
     * Get all messages of this Note from firebase
     */
    private fun getDataFromFirebase(id: String) {
        viewModel.getDataFromFirebase(id).observe(viewLifecycleOwner, {
            mNoteList = it
            updateNoteUI(it)
        })
    }

    private fun updateNoteUI(noteList: List<Note>) {
        setTotalSum(noteList, 0)
        updateUI(noteList)
    }

    /**
     * Count getSum() from every Note.class object
     * and bind total sum to XML
     */
    private fun setTotalSum(list: List<Note>, price: Int) {
        mTotalSum = list.sumBy { it.sum } + price
        setFormatTotalSum(mTotalSum)
    }

    /**
     * set formatted total sum
     */
    private fun setFormatTotalSum(totalSum: Int) {
        mBinding.toolbar.format = FormatSum(UtilConverter.customStringFormat(totalSum))
    }

    /**
     * Get title of this Note from firebase
     */
    private fun getTotalDataFromFirebase(id: String) {
        viewModel.getTotalDataFromFirebase(id).observe(viewLifecycleOwner, {
            mMainMenuNote = it
            workWithTotalData(it)
        })
    }

    private fun workWithTotalData(mainMenuNote: MainMenuNote) {
        setMainMenuNoteDataInLayout(mainMenuNote)
        changeFocusIfTitleExist(mainMenuNote)
    }

    /**
     * update XML file
     */
    private fun setMainMenuNoteDataInLayout(mainMenuNote: MainMenuNote) {
        mBinding.toolbar.mainNote = mainMenuNote
        mBinding.includeHintMessage.mainNote = mainMenuNote
        Log.v(TAG, "Hint message: ${mainMenuNote.message}")
    }

    /**
     * changing focus on EditText "Add name" if Title exist.
     */
    private fun changeFocusIfTitleExist(mainMenuNote: MainMenuNote) {
        if (mainMenuNote.title != "") mBinding.includeInterface.etNameOperation.requestFocus()
    }

    /**
     * "Add" button - get data from all EditTexts
     * and add data to the recyclerView and bind to XML file,
     * notify adapter that data has changed.
     * EditText initialized here and made some work with it.
     */
    private fun addNewNoteItemButton() {
        val editTextTitleName = mBinding.toolbar.etNoteTitleName
        val editTextName = mBinding.includeInterface.etNameOperation
        val editTextPrice = mBinding.includeInterface.etPriceOperation
        editTextTitleName.requestFocus()

        // Add button
        mBinding.includeInterface.buttonAddNewItem.setOnClickListener { // checking if edit text not empty
            if (editTextName.text.toString().trim { it <= ' ' } == ""
                    || editTextPrice.text.toString().trim { it <= ' ' } == "") {
                Toast.makeText(context,
                        R.string.no_data,
                        Toast.LENGTH_SHORT).show()
            }

            val message = editTextName.text.toString().trim { it <= ' ' }
            val sum = editTextPrice.text.toString().trim { it <= ' ' }.toInt()
            setTotalSum(mNoteList, sum)

            // saving message
            saveNote(message, sum, ID)
            // saving total data
            saveTotalData()

            editTextName.text.clear()
            editTextPrice.text.clear()
            editTextName.requestFocus()
        }
    }

    private fun loadRecyclerView() {
        mRecyclerView = mBinding.recyclerCreation
        mRecyclerView.layoutManager = LinearLayoutManager(context,
                LinearLayoutManager.VERTICAL, false)
        mAdapter = AdapterNote()
        mRecyclerView.adapter = mAdapter
    }

    private fun updateUI(list: List<Note>) {
        mAdapter.list = list
    }

    /**
     * Saving data, MainMenuNote.class
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
     * Updating data, MainMenuNote.class
     */
    private fun updateMainMenuNoteData() {
        Log.v(TAG, "updateMainMenuNoteData()")

        // If title or total sum has changed than update data
        if (getMessage() != mMainMenuNote.message) {
            Log.v(TAG, "mainMenuNote is NULL")
            saveTotalData()
        } else if (mMainMenuNote.title != getTitleName() || mMainMenuNote.total_sum != mTotalSum) {
            Log.v(TAG, "DATA UPDATED")
            saveTotalData()
        } else {
            Log.v(TAG, "Note haven't changed!")
        }
    }

    private fun saveNote(message: String, sum: Int, id_note: String) {
        val note = Note()
        note.message = message
        note.sum = sum
        note.id_note = id_note
        saveNoteInFirebase(id_note, note)
    }

    private fun saveNoteInFirebase(id_note: String, note: Note) {
        viewModel.saveNoteInFirebase(id_note, note)
    }

    private fun saveTotalData() {
        val mainMenuNote = MainMenuNote()
        mainMenuNote.date = getCurrentDate()
        mainMenuNote.title = getTitleName()
        mainMenuNote.total_sum = mTotalSum
        mainMenuNote.id = ID
        mainMenuNote.group = false
        mainMenuNote.message = getMessage()
        saveTotalDataInFirebase(ID, mainMenuNote)
    }

    private fun saveTotalDataInFirebase(id: String, mainMenuNote: MainMenuNote) {
        viewModel.saveTotalDataInFirebase(id, mainMenuNote)
    }

    private fun getTitleName(): String {
        return mBinding.toolbar.etNoteTitleName.text.toString().trim()
    }

    private fun getMessage(): String {
        return mBinding.includeHintMessage.editTextMessage.text.toString().trim()
    }

    private fun ismFlag(): Boolean {
        return mFlag
    }

    private fun getCurrentDate(): String {
        return UtilDate.getCurrentDate()
    }

    override fun onDestroyView() {
        Log.v(TAG, "onDestroyView()")
        if (ismFlag()) {
            saveMainMenuNoteData()
        } else {
            updateMainMenuNoteData()
        }
        super.onDestroyView()
    }
}