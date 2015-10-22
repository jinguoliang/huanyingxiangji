package com.example.huanyingxiangji1.handler;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import java.security.MessageDigest;

/**
 * Created by jinux on 15/10/21.
 */
public class PictureProcessSaveHandler extends Handler{

    private static final int MSG_PROCESS = 1;
    private static HandlerThread mThread;

    private PictureProcessSaveHandler(Looper looper) {
       super(looper);
    }

    public static PictureProcessSaveHandler getIntance() {
        mThread = new HandlerThread("picture process save");
        mThread.start();
        PictureProcessSaveHandler handler = new PictureProcessSaveHandler(mThread.getLooper());
        return handler;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case MSG_PROCESS:

                break;
        }
    }

    public void process(byte[] data) {
        Message message = obtainMessage(MSG_PROCESS);
        message.obj = data;
        sendMessage(message);
    }


}
