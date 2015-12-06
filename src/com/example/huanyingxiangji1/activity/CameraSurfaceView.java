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
import com.example.huanyingxiangji1.utils.PhoneUtils;

/**
 * A basic Camera preview class
 */
public class CameraSurfaceView extends SurfaceView implements
        SurfaceHolder.Callback {
    private static final String TAG = "CameraSurfaceView";

    private Camera mCamera;
    private SurfaceHolder mHolder;

    private Size mPreviewSize;
    private Size mPictureSize;

    public CameraSurfaceView(Context context) {
        super(context);
        initView();
    }

    public CameraSurfaceView(Context context, AttributeSet set) {
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
        LogHelper.i(TAG, "surface width=" + width + ",height=" + height);
        mHolder = holder;
        mCamera.stopPreview();

        try {
            mCamera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
            LogHelper.e(TAG, "surface has created?");
        }

        mCamera.startPreview();
    }

    /**
     * 设置相机预览大小和照片大小
     */
    private void configCamera() {
        int width = PhoneUtils.getScreenWidth();
        int height = PhoneUtils.getScreenHeight();
        // 计算最优尺寸
        // TODO the implement need to change a little, now I just exchange the two param
        mPreviewSize = getOptimalPreviewSize(mCamera.getParameters()
                        .getSupportedPreviewSizes(), height,
                width);
        mPictureSize = getOptimalPreviewSize(mCamera.getParameters()
                        .getSupportedPictureSizes(), height,
                width);
        LogHelper.i(TAG, "the select preview size (" + mPreviewSize.width + "," + mPreviewSize.height + ")");
        LogHelper.i(TAG, "the select picture size (" + mPictureSize.width + "," + mPictureSize.height + ")");

        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
        parameters.setPictureSize(mPictureSize.width, mPictureSize.height);
        mCamera.setParameters(parameters);

        // 竖屏下需要旋转90
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

    public void setCamera(Camera c) {
        mCamera = c;

        configCamera();

        try {
            mCamera.setPreviewDisplay(mHolder);
        } catch (IOException e) {
            e.printStackTrace();
            LogHelper.e(TAG, "the surface has created?");
        }

        mCamera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //TODO
    }
}
