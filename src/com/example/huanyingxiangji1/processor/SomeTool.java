package com.example.huanyingxiangji1.processor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

//import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;

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



//	static public InputStream getInputStreamFrom(Uri uri, Context context) {
//		String path = "";
//		InputStream in = null;
//		if (uri.getScheme().equals("content")) {
//			Cursor c = context.getContentResolver().query(uri, null, null,
//					null, null);
//			c.moveToFirst();
//			byte buf[] = c.getBlob(c.getColumnIndex(Media.DATA));
//			in = new ByteInputStream(buf, buf.length);
//		} else {
//			try {
//				in = new FileInputStream(new File(uri.getPath()));
//			} catch (FileNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//
//		return in;
//	}

	public static Uri getUriFromPath(String path) {
		return Uri.parse("file://" + path);
	}

	public static enum FileType {
		JPG, GIF, NONE
	}

	public static FileType getFileType(String f) {
		if (f.endsWith("jpg"))
			return FileType.JPG;
		if (f.endsWith("gif"))
			return FileType.GIF;
		return FileType.NONE;

	}
}
