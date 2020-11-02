package com.zivapp.notes.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UtilDate {

    public static String getCurrentDate() {
        return new SimpleDateFormat("dd MMM yyyy | HH:mm").format(new Date().getTime());
    }

    public static String getGroupDate() {
        return new SimpleDateFormat("HH:mm | dd MMM yyyy").format(new Date().getTime());
    }
}
