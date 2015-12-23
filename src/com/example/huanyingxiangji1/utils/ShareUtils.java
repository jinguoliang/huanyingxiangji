package com.example.huanyingxiangji1.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.example.huanyingxiangji1.R;

/**
 * Created by baidu on 15/12/23.
 *
 * 分享相关的工具
 */
public class ShareUtils {
    public static void share(Context c, String msg, Uri uri) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("image/*");
        i.putExtra(Intent.EXTRA_TEXT, msg + "@" + c.getString(R.string.app_name));
        i.putExtra(Intent.EXTRA_STREAM, uri);
        c.startActivity(i);
    }
}
