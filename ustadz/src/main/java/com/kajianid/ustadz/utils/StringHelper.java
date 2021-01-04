package com.kajianid.ustadz.utils;

import android.util.Patterns;

public class StringHelper {
    public static boolean isNullOrEmpty(String what) {
        return (what == null) || (what.trim().equals("") || what.equals("null"));
    }

    public static boolean isValidEmail(String email) {
        if (email.equals(null)) return false;
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean convertToBoolean(String what) {
        return what.equals("1") || what.equalsIgnoreCase("TRUE");
    }
}
