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

/*
 * ±£´æÎªjpegÍ¼Æ¬
 */
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
	
		Log.e(TAG,"wawa");
		Message m=Message.obtain();
		m.what=1;

		Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
		 mHandler.sendMessage(m);

		
		Log.e(TAG,"haha");
		
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
