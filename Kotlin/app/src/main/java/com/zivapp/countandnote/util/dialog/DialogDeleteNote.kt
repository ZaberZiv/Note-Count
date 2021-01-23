package com.zivapp.countandnote.util.dialog

import android.app.Activity
import android.app.AlertDialog
import android.view.View
import androidx.appcompat.app.AppCompatDialogFragment
import com.zivapp.countandnote.R

class DialogDeleteNote : AppCompatDialogFragment() {
    companion object {
        private const val TAG = "DialogDeleteNote"

        fun onCreateDialogBuilder(activity: Activity): AlertDialog.Builder? {
            val builder = AlertDialog.Builder(activity)
            val inflater = activity.layoutInflater
            val view: View = inflater.inflate(R.layout.dialog_edit_note, null)
            builder.setView(view)
            return builder
        }
    }
}