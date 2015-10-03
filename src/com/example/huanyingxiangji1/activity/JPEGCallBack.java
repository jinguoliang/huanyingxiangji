package com.example.huanyingxiangji1.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.huanyingxiangji1.MyApplication;
import com.example.huanyingxiangji1.processor.PicProcessor;
import com.example.huanyingxiangji1.utils.LogHelper;


public class JPEGCallBack implements PictureCallback {

	String path;
	final private String TAG="JPEGCallBack";
	private Handler mHandler;
	public JPEGCallBack(Handler handler) {
		mHandler=handler;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	
	public void onPictureTaken(byte[] data, Camera camera) {
	
		Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
		LogHelper.i(TAG, "the size of the picture just taken is (" + bitmap.getWidth() + ", " + bitmap.getHeight() + ")");
		mHandler.sendMessage(mHandler.obtainMessage(PreviewAndPicture.MSG_PICTURE));

		MyApplication.putPic("newPic", bitmap);
		try {
			bitmap=PicProcessor.rotatePic(bitmap);
			bitmap=PicProcessor.turnPicture(bitmap);
			MyApplication.newPicPath=path;
			PicProcessor.storePic(bitmap, path);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
