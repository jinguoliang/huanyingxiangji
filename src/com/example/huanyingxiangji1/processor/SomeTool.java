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

	public static final int CAMERA_FRONT = 1;
	public static final int CAMERA_BACK = 0;

	@SuppressLint("NewApi")
	public static Camera getCameraInstance(int which) {
		Camera c = null;
		switch (which) {
		case CAMERA_BACK:
			c = Camera.open(); // attempt to get a Camera instance
			if (c == null)
				c = Camera.open(CAMERA_BACK);
			break;
		case CAMERA_FRONT:
			c = Camera.open(1);
			break;

		default:
			break;
		}

		return c; // returns null if camera is unavailable
	}

	/** Check if this device has a camera */
	public static boolean checkCameraHardware(Context context, int which) {
		switch (which) {
		case CAMERA_FRONT:

			if (context.getPackageManager().hasSystemFeature(
					PackageManager.FEATURE_CAMERA_FRONT)) {
				// this device has a camera
				return true;
			} else {
				// no camera on this device
				return false;
			}
		case CAMERA_BACK:
			if (context.getPackageManager().hasSystemFeature(
					PackageManager.FEATURE_CAMERA)) {
				// this device has a camera
				return true;
			} else {
				// no camera on this device
				return false;
			}
		default:
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
