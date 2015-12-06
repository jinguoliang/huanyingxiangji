package com.example.huanyingxiangji1.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.example.huanyingxiangji1.MyApplication;

/**
 * Created by baidu on 15/12/6.
 */
public class PhoneUtils {
    public static int getScreenWidth() {
        DisplayMetrics metrics = getDisplayMetrix();
        return metrics.widthPixels;
    }

    public static int getScreenHeight() {
        DisplayMetrics metrics = getDisplayMetrix();
        return metrics.heightPixels;
    }

    public static DisplayMetrics getDisplayMetrix() {
        WindowManager wm = (WindowManager) MyApplication.getInstance()
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        return metrics;
    }
}
