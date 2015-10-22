package com.example.huanyingxiangji1.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.huanyingxiangji1.MyApplication;
import com.example.huanyingxiangji1.R;
import com.example.huanyingxiangji1.processor.PicProcessor;
import com.example.huanyingxiangji1.processor.SomeTool;
import com.example.huanyingxiangji1.utils.CameraHelper;
import com.example.huanyingxiangji1.utils.LogHelper;
import com.example.huanyingxiangji1.utils.SharedPrefUtils;

import java.io.File;
import java.io.IOException;

public class  PreviewAndPicture extends Activity {
    static String TAG = PreviewAndPicture.class.getName();


    private static final String KEY_HAS_MENG = "khm";
    private static final String KEY_MENG_PATH = "kmp";
    private static final java.lang.String KEY_SAVE_PATH = "ksp";
    private static final String KEY_WHICH_CAMERA = "kwc";
    private static final String KEY_ALPHA = "ka";

    public static final int DEFAULT_ALPHA = 75;

    public static final int MSG_PICTURE = 1;

    private static final int REQUEST_SELECT_PIC = 0;

    private MengView mMengImageView;
    private CameraSurfaceView mPreview;
    private Button mSelectMengBtn;
    private CheckBox mMengCb;
    private SeekBar mMengAlphaSb;

    private static Camera mCamera;
    public static int mWhichCamera = CameraHelper.CAMERA_BACK;
    private String mSavePath;
    private boolean mHasMeng;
    private Uri mMengUri;
    private int mMengAlpha = 5;
    private Bitmap mMengBitmap;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_PICTURE:
                    if (PreviewAndPicture.this.mHasMeng) {
                        Log.e(TAG, "ok to create group");

                        Intent i = new Intent(PreviewAndPicture.this,
                                CreateNewGroup.class);
                        i.putExtra("mengpic", mMengUri.toString());
                        i.putExtra("newpic", "file:///" + MyApplication.newPicPath);
                        PreviewAndPicture.this.startActivity(i);
                    } else {
                        mCamera.startPreview();
                    }
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        readPreference();

        initialView();
    }

    private void initialView() {
        mCamera = null;
        if (CameraHelper.checkCameraHardware(this, mWhichCamera)) {
            mCamera = CameraHelper.getCameraInstance(mWhichCamera);
        } else {
            LogHelper.i(TAG, "the phone has no "
                    + (mWhichCamera == CameraHelper.CAMERA_BACK ? "back " : "front ")
                    + "camera!!");
            mWhichCamera = CameraHelper.getAnotherCamera(mWhichCamera);
            if (CameraHelper.checkCameraHardware(this, mWhichCamera)) {
                mCamera = CameraHelper.getCameraInstance(mWhichCamera);
            }
        }

        if (mCamera != null) {
            initialNormalView();
        } else {
            Toast.makeText(this, getString(R.string.toast_no_camera), Toast.LENGTH_LONG).show();
            this.finish();
        }
    }

    private void initialNormalView() {
        mPreview = (CameraSurfaceView) findViewById(R.id.cameraSurfaceView);
        mPreview.changeCamera(mCamera);

        mMengImageView = (MengView) findViewById(R.id.mengView);
        mMengImageView.setAlpha(mMengAlpha);
        mSelectMengBtn = (Button)findViewById(R.id.mengPathBtn);
        mMengCb = (CheckBox)findViewById(R.id.mengCb);
        mMengAlphaSb = (SeekBar)findViewById(R.id.mengAlphaSb);
        mMengAlphaSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mMengAlpha = progress;
                mMengImageView.setAlpha(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mMengAlphaSb.setProgress(mMengAlpha);

        configureView();
    }

    private void configureView() {
        if (mHasMeng) {
            mMengImageView.setVisibility(View.VISIBLE);
            mMengAlphaSb.setVisibility(View.VISIBLE);
            mSelectMengBtn.setEnabled(true);
        }else {
            mMengImageView.setVisibility(View.INVISIBLE);
            mMengAlphaSb.setVisibility(View.INVISIBLE);
            mSelectMengBtn.setEnabled(false);
        }
        checkMengCb(mHasMeng);
    }


    private void readPreference() {
        mHasMeng = SharedPrefUtils.getBoolean(KEY_HAS_MENG);
        mMengUri = Uri.parse(SharedPrefUtils.getString(KEY_MENG_PATH));

        // File
        File storeDir = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        mSavePath = SharedPrefUtils.getString(KEY_SAVE_PATH,
                storeDir.getAbsolutePath());
        LogHelper.i(TAG, "the save path of picture is " + mSavePath);

        mMengAlpha = SharedPrefUtils.getInt(KEY_ALPHA, DEFAULT_ALPHA);

        mWhichCamera = SharedPrefUtils.getInt(KEY_WHICH_CAMERA, CameraHelper.CAMERA_BACK);

        LogHelper.i(TAG, "mengUrl = " + mMengUri + ",savePath = " + mSavePath + ",mMengAlpha = " + mMengAlpha + ",mWitchCamera = " + mWhichCamera);
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            mCamera.reconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(mHasMeng) {
            loadMeng();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mMengBitmap != null) {
            mMengBitmap.recycle();
            mMengBitmap = null;
        }
        savePreference();
    }

    private void loadMeng() {
        //TODO 应该放到单线程里
        try {
            mMengBitmap = PicProcessor.getBitmapFromUri(this, mMengUri,
                    PicProcessor.SCALE_MID);
        } catch (Exception e) {
            Log.e(TAG, "PicProcessor.getBitmapFromUri faild!!");
            e.printStackTrace();
        }
        // MyApplication.putPic(MyApplication.mengPic, mMengBitmap);
        if (mMengBitmap == null) {
            Toast.makeText(this, getString(R.string.toast_load_meng_failed), Toast.LENGTH_LONG).show();
            mHasMeng = false;
            configureView();
        } else {
            // if (mMengBitmap.getHeight() < mMengBitmap.getWidth()) {
            // mMengBitmap = PicProcessor.rotatePic(mMengBitmap);
            // }
            mMengImageView.setImageBitmap(mMengBitmap);
        }
    }

    private void savePreference() {
        SharedPrefUtils.put(KEY_ALPHA, mMengAlpha);
        SharedPrefUtils.put(KEY_MENG_PATH, mMengUri.toString());
        SharedPrefUtils.put(KEY_SAVE_PATH, mSavePath);
        SharedPrefUtils.put(KEY_HAS_MENG, mHasMeng);
        SharedPrefUtils.put(KEY_WHICH_CAMERA, mWhichCamera);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCamera.release();
    }

    private void switchCamera() {
        mCamera.stopPreview();
        mCamera.release();

        mWhichCamera = CameraHelper.getAnotherCamera(mWhichCamera);
        mCamera = CameraHelper.getCameraInstance(mWhichCamera);
        mPreview.changeCamera(mCamera);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED) {
            return;
        }

        switch (requestCode) {
            case REQUEST_SELECT_PIC:
                if (resultCode == RESULT_OK && data != null) {
                    // this.mMengBitmap =
                    // MyApplication.getPic(MyApplication.mengPic);
                    // if (this.mMengBitmap != null && !this.mMengBitmap.isRecycled()) {
                    // this.mMengBitmap.recycle();
                    // }

                    mMengUri = data.getData();

                    LogHelper.i(TAG, "onActivityResult: " + mMengUri.toString());
                    mMengBitmap = null;
                    savePreference();
                    loadMeng();
                } else {
                   showToast(R.string.faild_select_meng);
                }
                break;
            default:
                break;
        }
    }

    private void showToast(int msgResId) {
        Toast.makeText(this, getString(msgResId), Toast.LENGTH_LONG).show();
    }

    /**
     * when touch the picture button ,this function will be called
     *
     * @param v
     */
    public void pictureButtonClick(View v) {
        JPEGCallBack jpegCallBack = new JPEGCallBack(mHandler);
        try {
            mCamera.takePicture(null, null, jpegCallBack);
        } catch (Exception e) {
            Log.e(TAG, "takepicture error");
            e.printStackTrace();
        }
    }

    public void switchCameraButtonClick(View view) {
        switchCamera();
    }

    public void savePathBtnClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("path");
        final EditText pathText = new EditText(this);
        builder.setView(pathText);
        builder.setPositiveButton("ok",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        String tmp = pathText.getText().toString();
                        File testFile = new File(tmp);
                        if (testFile.isDirectory()) {
                            mSavePath = tmp;

                        } else {
                            Toast.makeText(PreviewAndPicture.this,
                                    "not a directory", Toast.LENGTH_LONG).show();
                        }
                    }
                });
        builder.setNegativeButton("cancel", null);
        builder.show();
    }

    public void onSelectMengBtnClick(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setDataAndType(Uri.fromFile(new File(MyApplication.pic_path)), "image/*");
        startActivityForResult(intent, REQUEST_SELECT_PIC);
    }

    public void onMengCbClick(View view) {
        mHasMeng = mMengCb.isChecked();
        configureView();
    }

    public void onBackClick(View view) {
        finish();
    }

    private void checkMengCb(boolean checked) {
        if(checked != mMengCb.isChecked()) {
            mMengCb.toggle();
        }
    }
}
