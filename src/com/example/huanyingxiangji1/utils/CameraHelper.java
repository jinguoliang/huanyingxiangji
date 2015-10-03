package com.example.huanyingxiangji1.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.text.TextUtils;

/**
 * Created by baidu on 15/10/1.
 */
public class CameraHelper {
    private static final String TAG = CameraHelper.class.getSimpleName();

    public static final int CAMERA_FRONT = 1;
    public static final int CAMERA_BACK = 0;

    /**
     * 检查是否有前后摄像头之中的某一个，如果有则返回 true
     * @param context
     * @param which
     * @return
     */
    public static boolean checkCameraHardware(Context context, int which) {
        String camera_feature = "";
        if (which == CAMERA_FRONT) {
            camera_feature = PackageManager.FEATURE_CAMERA_FRONT;
        }else if(which == CAMERA_BACK) {
            camera_feature = PackageManager.FEATURE_CAMERA;
        }

        if (TextUtils.isEmpty(camera_feature)){
            return false;
        }else{
            return context.getPackageManager().hasSystemFeature(
                    PackageManager.FEATURE_CAMERA);
        }
    }

    public static Camera getCameraInstance(int which) {
        int facing = -1;

        switch (which) {
            case CAMERA_BACK:
                facing = Camera.CameraInfo.CAMERA_FACING_BACK;
                break;
            case CAMERA_FRONT:
                facing = Camera.CameraInfo.CAMERA_FACING_FRONT;
                break;
            default:
                return null;
        }

        int numberOfCameras = Camera.getNumberOfCameras();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == facing) {
                LogHelper.i(TAG, "current open camera " + i);
                return Camera.open(i);
            }
        }

        return null;
    }

    public static int getAnotherCamera(int which) {
        return which == CAMERA_BACK? CAMERA_FRONT : CAMERA_BACK;
    }
}
