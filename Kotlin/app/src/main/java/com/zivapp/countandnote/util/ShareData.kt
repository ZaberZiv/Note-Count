package com.zivapp.countandnote.util

import com.zivapp.countandnote.model.GroupNote
import com.zivapp.countandnote.model.MainMenuNote
import com.zivapp.countandnote.model.Note
import java.util.*

class ShareData {

    companion object {
        private const val TOP_LINE = "********************"
        private const val BOTTOM_LINE = "___________________________________"
        private const val APP_NAME = "NOTE & COUNT APP"
        private const val TOTAL_SUM = "Общая сумма"
        private const val CURRENCY_KZ = ": KZT "
    }

    fun formatStringData(list: List<Note>, totalData: MainMenuNote): String {
        var stringBuilder = StringBuilder()
        stringBuilder = getAppName(stringBuilder, totalData.title)
        for (note in list) {
            stringBuilder
                    .append(note.message)
                    .append(CURRENCY_KZ)
                    .append(UtilConverter.customStringFormat(note.sum))
                    .append("\n")
        }
        stringBuilder = getTotalSum(stringBuilder, totalData.total_sum)
        return stringBuilder.toString()
    }

    fun formatStringDataGroup(list: List<GroupNote>, totalData: MainMenuNote): String {
        var stringBuilder = StringBuilder()
        stringBuilder = getAppName(stringBuilder, totalData.title)
        for (note in list) {
            stringBuilder
                    .append(note.member)
                    .append(" приобрел(а) ")
                    .append(note.message)
                    .append(CURRENCY_KZ)
                    .append(UtilConverter.customStringFormat(note.sum))
                    .append("\n")
        }
        stringBuilder = getTotalSum(stringBuilder, totalData.total_sum)
        return stringBuilder.toString()
    }

    private fun getAppName(builder: StringBuilder, title: String): StringBuilder {
        return builder
                .append(TOP_LINE)
                .append("\n")
                .append(APP_NAME)
                .append("\n\n")
                .append("Note: ")
                .append(title)
                .append("\n\n")
    }

    private fun getTotalSum(builder: StringBuilder, total_sum: Int): StringBuilder {
        return builder
                .append(BOTTOM_LINE)
                .append("\n\n")
                .append(TOTAL_SUM + CURRENCY_KZ)
                .append(UtilConverter.customStringFormat(total_sum))
                .append("\n")
                .append(BOTTOM_LINE)
    }
}