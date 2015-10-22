package com.example.huanyingxiangji1.handler;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.example.huanyingxiangji1.MyApplication;
import com.example.huanyingxiangji1.activity.PreviewAndPicture;
import com.example.huanyingxiangji1.processor.PicProcessor;
import com.example.huanyingxiangji1.processor.SomeTool;
import com.example.huanyingxiangji1.utils.CameraHelper;
import com.example.huanyingxiangji1.utils.LogHelper;

import java.security.MessageDigest;

/**
 * Created by jinux on 15/10/21.
 */
public class PictureProcessSaveHandler extends Handler {

    private static final int MSG_PROCESS = 1;
    private static final String TAG = PictureProcessSaveHandler.class.getSimpleName();
    private static HandlerThread mThread;
    private final Handler mainHandler;

    private PictureProcessSaveHandler(Looper looper, Handler mainHandler) {
        super(looper);
        this.mainHandler = mainHandler;
    }

    public static PictureProcessSaveHandler getIntance(Handler mainHandler) {
        mThread = new HandlerThread("picture process save");
        mThread.start();
        PictureProcessSaveHandler handler = new PictureProcessSaveHandler(mThread.getLooper(), mainHandler);
        return handler;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case MSG_PROCESS:
                handleProcessPicData(msg);
                break;
        }
    }

    private void handleProcessPicData(Message msg) {
        byte[] data = (byte[]) msg.obj;
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        LogHelper.i(TAG, "the size of the picture just taken is (" + bitmap.getWidth() + ", " + bitmap.getHeight() + ")");
        mainHandler.sendMessage(mainHandler.obtainMessage(PreviewAndPicture.MSG_PICTURE));

        MyApplication.putPic("newPic", bitmap);
        try {
            if (PreviewAndPicture.mWhichCamera == CameraHelper.CAMERA_FRONT) {
                bitmap = PicProcessor.rotatePic(bitmap, -90);
                bitmap = PicProcessor.turnPicture(bitmap);
            } else {
                bitmap = PicProcessor.rotatePic(bitmap, 90);
            }
            MyApplication.newPicPath = SomeTool.genPicPathName(MyApplication.pic_path);
            PicProcessor.storePic(bitmap, MyApplication.newPicPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void process(byte[] data) {
        Message message = obtainMessage(MSG_PROCESS);
        message.obj = data;
        sendMessage(message);
    }


}
