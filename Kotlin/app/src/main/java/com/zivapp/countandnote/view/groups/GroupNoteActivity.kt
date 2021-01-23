package com.zivapp.countandnote.view.groups

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.zivapp.countandnote.R

class GroupNoteActivity : AppCompatActivity() {

    private val groupNoteFragment = GroupNoteFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_note)

        loadFragment(groupNoteFragment)
        setBackArrow()
    }

    private fun loadFragment(fragment: Fragment) =
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragment_group_note_container, fragment)
                commit()
            }

    private fun setBackArrow() {
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}