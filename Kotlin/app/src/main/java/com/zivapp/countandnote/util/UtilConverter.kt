package com.zivapp.countandnote.util

import java.text.DecimalFormat

class UtilConverter {
    companion object {
        fun customStringFormat(value: Double): String {
            val myFormatter = DecimalFormat("###,###.##")
            return myFormatter.format(value)
        }

        fun customStringFormat(value: Int): String {
            val myFormatter = DecimalFormat("###,###.##")
            return myFormatter.format(value)
        }
    }
}