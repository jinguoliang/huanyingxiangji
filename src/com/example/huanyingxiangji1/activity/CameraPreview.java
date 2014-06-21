package com.example.huanyingxiangji1.activity;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/** A basic Camera preview class */
public class CameraPreview extends SurfaceView implements
		SurfaceHolder.Callback {
	private Camera mCamera;
	String TAG = "CameraPreview";

	public CameraPreview(Context context, Camera camera) {
		super(context);

		mCamera = camera;
		configCamera();
		
		SurfaceHolder mHolder = getHolder();
		mHolder.addCallback(this);

		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Log.e(TAG, "surfaceChanged");
		
		Log.e(TAG,"surface width="+width+",height="+height);
		mCamera.stopPreview();

		configCamera();

		try {
			mCamera.setPreviewDisplay(holder);
		} catch (IOException e) {
			e.printStackTrace();
		}
		mCamera.setDisplayOrientation(90);
		mCamera.startPreview();

	}

	private void configCamera() {
		Camera.Parameters param = mCamera.getParameters();
		param.setPictureFormat(PixelFormat.JPEG);
		 List<Size>list= param.getSupportedPreviewSizes();

//		 for (int i = 0; i <list.size(); i++) {
//		
//		 Log.e(TAG, "height:"+param.getSupportedPreviewSizes().get(i).height);
//		 Log.e(TAG,"width:"+ param.getSupportedPreviewSizes().get(i).width);
//		 }

		param.setPreviewSize(1440,816);
		param.setPictureSize(1440, 816);
//		Size previewSize=param.getPreviewSize();
//		Size pictureSize=param.getPictureSize();
//		Log.e(TAG,"previewSize="+previewSize.width+","+previewSize.height);
//		Log.e(TAG,"previewSize="+pictureSize.width+","+pictureSize.height);
		mCamera.setParameters(param);
	}

	public void surfaceCreated(SurfaceHolder holder) {
		Log.e(TAG, "surfaceCreated");

	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.e(TAG, "surfaceDestroyed");
	}
	
}