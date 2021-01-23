package com.zivapp.countandnote.util

import java.text.SimpleDateFormat
import java.util.*

class UtilDate {
    companion object {
        fun getCurrentDate(): String {
            return SimpleDateFormat("dd MMM yyyy | HH:mm").format(Date().time)
        }

        fun getGroupDate(): String {
            return SimpleDateFormat("HH:mm | dd MMM").format(Date().time)
        }
    }
}