package com.example.huanyingxiangji1.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Handler;

import com.example.huanyingxiangji1.MyApplication;
import com.example.huanyingxiangji1.handler.PictureProcessSaveHandler;
import com.example.huanyingxiangji1.processor.PicProcessor;
import com.example.huanyingxiangji1.processor.SomeTool;
import com.example.huanyingxiangji1.utils.CameraHelper;
import com.example.huanyingxiangji1.utils.LogHelper;


public class JPEGCallBack implements PictureCallback {
	final private String TAG="JPEGCallBack";

	String path;
	private Handler mHandler;

    public String mPicPath;

    public JPEGCallBack(Handler handler) {
		mHandler=handler;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	
	public void onPictureTaken(final byte[] data, Camera camera) {
        new Thread() {
            @Override
            public void run() {
                handleProcessPicData(data);
            }
        }.start();
	}

	private synchronized void handleProcessPicData(byte[] data) {
		Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
		LogHelper.i(TAG, "the size of the picture just taken is (" + bitmap.getWidth() + ", " + bitmap.getHeight() + ")");
        try {
			if (PreviewAndPicture.mWhichCamera == CameraHelper.CAMERA_FRONT) {
				bitmap = PicProcessor.rotatePic(bitmap, -90);
				bitmap = PicProcessor.turnPicture(bitmap);
			} else {
				bitmap = PicProcessor.rotatePic(bitmap, 90);
			}
			mPicPath = SomeTool.genPicPathName(MyApplication.pic_path);
			PicProcessor.storePic(bitmap, mPicPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
        mHandler.sendMessage(mHandler.obtainMessage(PreviewAndPicture.MSG_PICTURE));
    }
}
