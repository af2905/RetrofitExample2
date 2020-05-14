package ru.job4j.retrofitexample2;

import java.text.SimpleDateFormat;
import java.util.Locale;

class Utils {
    static String getDate(long date) {
        SimpleDateFormat format = new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault());
        return format.format(date);
    }
}
