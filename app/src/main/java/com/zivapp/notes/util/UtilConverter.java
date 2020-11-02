package com.zivapp.notes.util;

import java.text.DecimalFormat;

public class UtilConverter {

    public static String customStringFormat(double value) {
        DecimalFormat myFormatter = new DecimalFormat("###,###.##");
        return myFormatter.format(value);
    }

    public static String customStringFormat(int value) {
        DecimalFormat myFormatter = new DecimalFormat("###,###.##");
        return myFormatter.format(value);
    }
}
