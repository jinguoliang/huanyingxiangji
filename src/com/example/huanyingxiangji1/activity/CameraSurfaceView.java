package com.example.huanyingxiangji1.activity;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.example.huanyingxiangji1.utils.LogHelper;

/** A basic Camera preview class */
public class CameraSurfaceView extends SurfaceView implements
		SurfaceHolder.Callback {
	String TAG = CameraSurfaceView.class.getSimpleName();

	private Camera mCamera;
	private SurfaceHolder mHolder;
	private List<Size> mSupportedPreviewSizes;
	private Size mPreviewSize;
	private List<Size> mSupportedPictureSizes;
	private Size mPictureSize;

	public CameraSurfaceView(Context context) {
		super(context);
		initView();
	}

    public CameraSurfaceView(Context context,  AttributeSet set) {
        super(context, set);
        initView();
    }

    private void initView() {
        mHolder = getHolder();
        mHolder.addCallback(this);
    }


	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		mHolder = holder;
		LogHelper.i(TAG, "surface width=" + width + ",height=" + height);
		mCamera.stopPreview();

		try {
			mCamera.setPreviewDisplay(holder);
		} catch (IOException e) {
			e.printStackTrace();
		}

		mCamera.startPreview();
	}

	private void configCamera() {
		WindowManager wm = (WindowManager) getContext()
				.getSystemService(Context.WINDOW_SERVICE);
		int width = wm.getDefaultDisplay().getWidth();
		int height = wm.getDefaultDisplay().getHeight();
		if (mCamera != null) {
			mSupportedPreviewSizes =
                    mCamera.getParameters()
					.getSupportedPreviewSizes();
			mSupportedPictureSizes =
                    mCamera.getParameters()
					.getSupportedPictureSizes();
		}
		//TODO the implement need to change a little, now I just exchange the two param
		mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, height,
				width);
		mPictureSize = getOptimalPreviewSize(mSupportedPictureSizes, height,
				width);
        LogHelper.i(TAG, "the select preview size (" + mPreviewSize.width + "," + mPreviewSize.height +")");
        LogHelper.i(TAG, "the select picture size (" + mPictureSize.width + "," + mPictureSize.height +")");
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

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mHolder = holder;
	}

	public void changeCamera(Camera c) {
		mCamera = c;

		configCamera();
		try {
			mCamera.setPreviewDisplay(mHolder);
		} catch (IOException e) {
			e.printStackTrace();
		}

		mCamera.startPreview();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		//TODO
	}
}
