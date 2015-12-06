package com.example.huanyingxiangji1.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by baidu on 15/12/6.
 */
public class ViewUtils {
    public static void showToast(Context c, String msg) {
        Toast.makeText(c, msg, Toast.LENGTH_LONG).show();
    }
}
