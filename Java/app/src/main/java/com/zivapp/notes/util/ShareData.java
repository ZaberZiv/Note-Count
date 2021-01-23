package com.zivapp.notes.util;

import com.zivapp.notes.model.GroupNote;
import com.zivapp.notes.model.MainMenuNote;
import com.zivapp.notes.model.Note;

import java.util.ArrayList;

public class ShareData {

    private static final String TOP_LINE = "********************";
    private static final String BOTTOM_LINE = "___________________________________";
    private static final String APP_NAME = "NOTE & COUNT APP";
    private static final String TOTAL_SUM = "Общая сумма";
    private static final String CURRENCY_KZ = ": KZT ";

    public static String formatStringData(ArrayList<Note> list, MainMenuNote totalData) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder = getAppName(stringBuilder, totalData.getTitle());

        for (Note note : list) {
            stringBuilder
                    .append(note.getMessage())
                    .append(CURRENCY_KZ)
                    .append(UtilConverter.customStringFormat(note.getSum()))
                    .append("\n");
        }

        stringBuilder = getTotalSum(stringBuilder, totalData.getTotal_sum());

        return stringBuilder.toString();
    }

    public static String formatStringDataGroup(ArrayList<GroupNote> list, MainMenuNote totalData) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder = getAppName(stringBuilder, totalData.getTitle());

        for (GroupNote note : list) {
            stringBuilder
                    .append(note.getMember())
                    .append(" приобрел(а) ")
                    .append(note.getMessage())
                    .append(CURRENCY_KZ)
                    .append(UtilConverter.customStringFormat(note.getSum()))
                    .append("\n");
        }

        stringBuilder = getTotalSum(stringBuilder, totalData.getTotal_sum());

        return stringBuilder.toString();
    }

    private static StringBuilder getAppName(StringBuilder builder, String title) {
        return builder
                .append(TOP_LINE)
                .append("\n")
                .append(APP_NAME)
                .append("\n\n")
                .append("Note: ")
                .append(title)
                .append("\n\n");
    }

    private static StringBuilder getTotalSum(StringBuilder builder, int total_sum) {
        return builder
                .append(BOTTOM_LINE)
                .append("\n\n")
                .append(TOTAL_SUM + CURRENCY_KZ)
                .append(UtilConverter.customStringFormat(total_sum))
                .append("\n")
                .append(BOTTOM_LINE);
    }
}
