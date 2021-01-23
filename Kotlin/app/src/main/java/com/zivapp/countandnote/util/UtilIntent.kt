package com.zivapp.countandnote.util

import android.content.Context
import android.content.Intent

class UtilIntent {
    companion object {
        fun shareDataByIntent(context: Context, text: String) {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT, text)
            sendIntent.type = "text/plain"
            val shareIntent = Intent.createChooser(sendIntent, null)
            context.startActivity(shareIntent)
        }
    }
}