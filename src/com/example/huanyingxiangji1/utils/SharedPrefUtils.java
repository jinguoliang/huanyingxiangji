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
    private static final String TAG = "SharedPrefUtils";

    public static final int DEFAULT_ALPHA = 75;

    // keys
    private static final String KEY_HAS_MENG = "khm";
    private static final String KEY_MENG_PATH = "kmp";
    private static final String KEY_WHICH_CAMERA = "kwc";
    private static final String KEY_ALPHA = "ka";

    public static boolean hasMeng() {
        return getBoolean(KEY_HAS_MENG);
    }

    public static void putHasMeng(boolean hasMeng) {
        put(KEY_HAS_MENG, hasMeng);
    }

    public static String getMengPath() {
        return getString(KEY_MENG_PATH);
    }

    public static void putMengPath(String path) {
        put(KEY_MENG_PATH, path);
    }

    public static int getWitchCamera() {
        return getInt(KEY_WHICH_CAMERA, CameraHelper.CAMERA_BACK);
    }

    public static void putWhichCamera(int which) {
        put(KEY_WHICH_CAMERA, which);
    }

    public static int getMengAlpha() {
        return getInt(KEY_ALPHA, DEFAULT_ALPHA);
    }

    public static void putMengAlpha(int alpha) {
        put(KEY_ALPHA, alpha);
    }



    // ====== 以下是基础方法
    private static SharedPreferences PREF;
    static {
        String prefname = MyApplication.getInstance().getString(R.string.shared_pref_name);
        PREF = MyApplication.getInstance().getSharedPreferences(prefname, Context.MODE_PRIVATE);
    }
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
