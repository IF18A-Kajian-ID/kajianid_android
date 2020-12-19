package com.kajianid.ustadz.utils;

public class StringHelper {
    public static boolean isNullOrEmpty(String what) {
        return (what == null) || (what.trim().equals("") || what.equals("null"));
    }
}
