package com.example.huanyingxiangji1.handler;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.example.huanyingxiangji1.MyApplication;
import com.example.huanyingxiangji1.processor.FileProcessor;
import com.example.huanyingxiangji1.processor.PicProcessor;
import com.example.huanyingxiangji1.utils.LogHelper;

/**
 * Created by jinux on 15/10/21.
 */
public class PictureProcessHandler extends Handler {
    private static final String TAG = "PictureProcessHandler";

    private static final int MSG_PROCESS = 1;
    private static final int MSG_GEN_GIF = 2;
    private static final int MSG_COMB_HORIZONAL = 3;
    private static final int MSG_COMB_VERTICAL = 4;
    private static HandlerThread mThread;
    private final FileProcessor mFileProcessor;

    private PictureProcessHandler(Looper looper) {
        super(looper);
        mFileProcessor = new FileProcessor();
    }

    public static PictureProcessHandler getIntance() {
        mThread = new HandlerThread("picture process save");
        mThread.start();
        PictureProcessHandler handler = new PictureProcessHandler(mThread.getLooper());
        return handler;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        String groupName = null;
        switch (msg.what) {
            case MSG_COMB_HORIZONAL:
                groupName = msg.obj.toString();
                handleCombineHorizonal(groupName);
                break;
            case MSG_COMB_VERTICAL:
                groupName = msg.obj.toString();
                handleCombineVertical(groupName);
                break;
            case MSG_GEN_GIF:
                groupName = msg.obj.toString();
                handleGenGif(groupName);
                break;
            default:
                break;
        }
    }

    private void handleGenGif(String groupName) {
        String dest = MyApplication.out_path + groupName + ".gif";
        try {
            PicProcessor.generateGif(mFileProcessor.getGroup(groupName), dest, 2000);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    private void handleCombineHorizonal(String groupName) {
        String dest = MyApplication.out_path + groupName + "_h.jpg";

        try {
            new PicProcessor().combinate(mFileProcessor.getGroup(groupName),
                    dest, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleCombineVertical(String groupName) {
        String dest = MyApplication.out_path + groupName + "_v.jpg";

        try {
            new PicProcessor().combinate(mFileProcessor.getGroup(groupName),
                    dest, 1);
        } catch (Exception e) {
            e.printStackTrace();
            LogHelper.e(TAG, "failed to combine pictures");
        }
    }

    public void generateGif(String groupName) {
        packMsg(MSG_GEN_GIF, groupName);
    }

    public void combineHorizonal(String groupName) {
        packMsg(MSG_COMB_HORIZONAL, groupName);
    }

    public void combineVertical(String groupName) {
        packMsg(MSG_COMB_VERTICAL, groupName);
    }

    private void packMsg(int msg, String groupName) {
        Message message = obtainMessage(msg);
        message.obj = groupName;
        sendMessage(message);
    }
}
