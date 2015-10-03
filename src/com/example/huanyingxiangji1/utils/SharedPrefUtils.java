package com.example.huanyingxiangji1.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.huanyingxiangji1.MyApplication;
import com.example.huanyingxiangji1.R;

/**
 * Created by baidu on 15/10/1.
 */
public class SharedPrefUtils {
    private static final String TAG = SharedPrefUtils.class.getSimpleName();
    private static String PREF_NAME = MyApplication.APP.getString(R.string.shared_pref_name);
    private static SharedPreferences PREF = MyApplication.APP.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

    public static void put(String key, String value) {
        SharedPreferences.Editor editor = PREF.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getString(String key) {
        return PREF.getString(key, "");
    }

    public static String getString(String key, String defaultValue) {
        return PREF.getString(key, defaultValue);
    }

    public static void put(String key, boolean value) {
        SharedPreferences.Editor editor = PREF.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static boolean getBoolean(String key) {
        return PREF.getBoolean(key, false);
    }

    public static int getInt(String key, int defaultValue) {
        return PREF.getInt(key, defaultValue);
    }

    public static void put(String key, int value) {
        SharedPreferences.Editor editor = PREF.edit();
        editor.putInt(key, value);
        editor.commit();
    }
}
