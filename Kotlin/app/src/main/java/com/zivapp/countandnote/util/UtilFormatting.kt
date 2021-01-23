package com.zivapp.countandnote.util

class UtilFormatting {
    companion object {
        // If User didn't set title name the "New note" will be added by default
        fun getDefaultName(position: Int): String {
            return "New Note " + (position + 1)
        }
    }
}