package ru.job4j.retrofitexample2;

import java.text.SimpleDateFormat;
import java.util.Locale;

class Utils {
    static String getDate(long date) {
        SimpleDateFormat format = new SimpleDateFormat("MMM dd", Locale.getDefault());
        return format.format(date);
    }

    static String getTime(long date) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return format.format(date);
    }

    static String appendSpaces(String text) {
        return text.concat("     ");
    }
}
