package com.example.huanyingxiangji1.activity;

import java.io.IOException;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

/** A basic Camera preview class */
public class CameraPreview extends SurfaceView implements
		SurfaceHolder.Callback {
	private Camera mCamera;
	String TAG = "CameraPreview";
	private SurfaceHolder mHolder;
	private List<Size> mSupportedPreviewSizes;
	private Size mPreviewSize;
	private Context mContext;
	private List<Size> mSupportedPictureSizes;
	private Size mPictureSize;

	public CameraPreview(Context context, Camera camera) {
		super(context);

		this.mContext = context;
		mCamera = camera;

		configCamera();

		mHolder = getHolder();
		mHolder.addCallback(this);

		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Log.e(TAG, "surfaceChanged");
		mHolder = holder;
		Log.e(TAG, "surface width=" + width + ",height=" + height);
		mCamera.stopPreview();

		configCamera();

		try {
			mCamera.setPreviewDisplay(holder);
		} catch (IOException e) {
			e.printStackTrace();
		}
		mCamera.startPreview();
	}

	private void configCamera() {
		WindowManager wm = (WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE);
		int width = wm.getDefaultDisplay().getWidth();
		int height = wm.getDefaultDisplay().getHeight();
		if (mCamera != null) {
			mSupportedPreviewSizes = mCamera.getParameters()
					.getSupportedPreviewSizes();
			mSupportedPictureSizes = mCamera.getParameters()
					.getSupportedPictureSizes();
		}
		//TODO the implement need to change a little, now I just exchange the two param
		mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, height,
				width);
		mPictureSize = getOptimalPreviewSize(mSupportedPictureSizes, height,
				width);
		Camera.Parameters parameters = mCamera.getParameters();
		parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
		parameters.setPictureSize(mPictureSize.width, mPictureSize.height);

		
		mCamera.setParameters(parameters);
		mCamera.setDisplayOrientation(90);

	}

	private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
		final double ASPECT_TOLERANCE = 0.1;
		double targetRatio = (double) w / h;
		if (sizes == null)
			return null;

		Size optimalSize = null;
		double minDiff = Double.MAX_VALUE;

		int targetHeight = h;

		// Try to find an size match aspect ratio and size
		for (Size size : sizes) {
			double ratio = (double) size.width / size.height;
			if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
				continue;
			if (Math.abs(size.height - targetHeight) < minDiff) {
				optimalSize = size;
				minDiff = Math.abs(size.height - targetHeight);
			}
		}

		// Cannot find the one match the aspect ratio, ignore the requirement
		if (optimalSize == null) {
			minDiff = Double.MAX_VALUE;
			for (Size size : sizes) {
				if (Math.abs(size.height - targetHeight) < minDiff) {
					optimalSize = size;
					minDiff = Math.abs(size.height - targetHeight);
				}
			}
		}
		return optimalSize;
	}

	public void surfaceCreated(SurfaceHolder holder) {
		Log.e(TAG, "surfaceCreated");
		mHolder = holder;

	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.e(TAG, "surfaceDestroyed");
	}

	@SuppressLint("NewApi")
	public void setCamera(Camera c) {
		Log.e(TAG, "setCamera");

		mCamera.stopPreview();
		mCamera.release();
		
		mCamera = c;

		configCamera();
		try {
			mCamera.setPreviewDisplay(mHolder);
		} catch (IOException e) {
			e.printStackTrace();
		}

		mCamera.startPreview();
	}

}
