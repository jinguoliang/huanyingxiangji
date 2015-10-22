package com.example.huanyingxiangji1.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Handler;

import com.example.huanyingxiangji1.MyApplication;
import com.example.huanyingxiangji1.handler.PictureProcessSaveHandler;
import com.example.huanyingxiangji1.processor.PicProcessor;
import com.example.huanyingxiangji1.utils.CameraHelper;
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
        PictureProcessSaveHandler.getIntance(mHandler).process(data);
	}
}
