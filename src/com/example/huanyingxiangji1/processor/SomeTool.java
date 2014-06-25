package com.example.huanyingxiangji1.processor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Camera;
import android.net.Uri;
import android.provider.MediaStore.Images.Media;
import android.widget.Toast;

public class SomeTool {
	public static String genPicPathName(String path) {
		String fileName = System.currentTimeMillis() + ".jpg";
		if (!path.endsWith("/")) {
			path = path + "/";
		}
		return path + fileName;
	}

	public static void makeToast(String text, Context c) {
		Toast.makeText(c, text, Toast.LENGTH_LONG).show();
	}

	@SuppressLint("NewApi")
	public static Camera getCameraInstance() {
		Camera c = null;
		// c = Camera.open(); // attempt to get a Camera instance
		if (c == null)
			c = Camera.open(1);
		if (c == null)
			c = Camera.open(1);
		return c; // returns null if camera is unavailable
	}

	/** Check if this device has a camera */
	public static boolean checkCameraHardware(Context context) {
		if (context.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA)) {
			// this device has a camera
			return true;
		} else {
			// no camera on this device
			return false;
		}
	}

	static public String getFileUriFrom(Uri uri, Context context) {
		String path = "";
		if (uri.getScheme().equals("content")) {
			Cursor c = context.getContentResolver().query(uri, null, null,
					null, null);
			c.moveToFirst();
			byte buf[] = c.getBlob(c.getColumnIndex(Media.DATA));
			return "file://" + buf.toString();
		} else {
			return uri.toString();
		}
	}

	public static Uri getUriFromPath(String path) {
		return Uri.parse("file://" + path);
	}

}
