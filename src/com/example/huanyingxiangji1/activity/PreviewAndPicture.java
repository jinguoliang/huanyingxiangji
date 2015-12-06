package com.example.huanyingxiangji1.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.huanyingxiangji1.MyApplication;
import com.example.huanyingxiangji1.R;
import com.example.huanyingxiangji1.processor.PicProcessor;
import com.example.huanyingxiangji1.utils.CameraHelper;
import com.example.huanyingxiangji1.utils.LogHelper;
import com.example.huanyingxiangji1.utils.SharedPrefUtils;
import com.example.huanyingxiangji1.utils.ViewUtils;
import com.example.huanyingxiangji1.view.MengView;

import java.io.File;
import java.io.IOException;

public class PreviewAndPicture extends Activity {
    private static final String TAG = "PreviewAndPicture";

    // intent bundle key
    public static final String KEY_FROM = "from";
    public static final String KEY_MENG_PATH = "kmp";

    private boolean isFromPictureGroup;

    public static final int MSG_PICTURE = 1;

    // 请求外部程序选择蒙图片
    private static final int REQUEST_SELECT_PIC = 0;

    // 视图控件
    private MengView mMengImageView;
    private CameraSurfaceView mPreview;
    private Button mSelectMengBtn;
    private CheckBox mMengCb;
    private SeekBar mMengAlphaSb;

    private static Camera mCamera;
    private JPEGCallBack mCallBack;

    // 一些状态
    public static int mWhichCamera = CameraHelper.CAMERA_BACK;
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
                        if (isFromPictureGroup) {
                            Intent i = new Intent();
                            i.setData(Uri.parse("file:///" + mCallBack.mPicPath));
                            setResult(RESULT_OK, i);
                            finish();
                        } else {
                            Intent i = new Intent(PreviewAndPicture.this,
                                    CreateNewGroup.class);
                            i.putExtra("mengpic", mMengUri.toString());
                            i.putExtra("newpic", "file:///" + mCallBack.mPicPath);
                            PreviewAndPicture.this.startActivity(i);
                        }
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

        loadConfig();

        // 处理 intent
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.getString(KEY_FROM).equals(GroupList.class.getSimpleName())) {
            isFromPictureGroup = true;
            mMengUri = Uri.parse("file://" + bundle.getString(KEY_MENG_PATH));
            mHasMeng = true;
        }

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
            configureView();
        } else {
            Toast.makeText(this, getString(R.string.toast_no_camera), Toast.LENGTH_LONG).show();
            this.finish();
        }
    }

    private void initialNormalView() {
        mPreview = (CameraSurfaceView) findViewById(R.id.cameraSurfaceView);
        mPreview.setCamera(mCamera);

        mMengImageView = (MengView) findViewById(R.id.mengView);
        mMengImageView.setAlpha(mMengAlpha);
        mSelectMengBtn = (Button) findViewById(R.id.mengPathBtn);
        mMengCb = (CheckBox) findViewById(R.id.mengCb);
        mMengAlphaSb = (SeekBar) findViewById(R.id.mengAlphaSb);
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
    }

    private void configureView() {
        if (mHasMeng) {
            mMengImageView.setVisibility(View.VISIBLE);
            mMengAlphaSb.setVisibility(View.VISIBLE);
            mSelectMengBtn.setEnabled(true);
        } else {
            mMengImageView.setVisibility(View.INVISIBLE);
            mMengAlphaSb.setVisibility(View.INVISIBLE);
            mSelectMengBtn.setEnabled(false);
        }
        checkMengCb(mHasMeng);
    }


    private void loadConfig() {
        mHasMeng = SharedPrefUtils.hasMeng();
        mMengUri = Uri.parse(SharedPrefUtils.getMengPath());
        mMengAlpha = SharedPrefUtils.getMengAlpha();
        mWhichCamera = SharedPrefUtils.getWitchCamera();

        LogHelper.i(TAG, "mengUrl = " + mMengUri + ",mMengAlpha = " + mMengAlpha + ",mWitchCamera = " + mWhichCamera);
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            mCamera.reconnect();
        } catch (IOException e) {
            e.printStackTrace();
            ViewUtils.showToast(this, getString(R.string.reconnect_error));
        }
        if (mHasMeng) {
            loadMeng();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mCamera.unlock();
        if (mMengBitmap != null) {
            mMengBitmap.recycle();
            mMengBitmap = null;
        }
        savePreference();
    }

    private void loadMeng() {
        //TODO 应该放到单线程里
        LogHelper.i(TAG, "the current meng url = " + mMengUri);
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
        SharedPrefUtils.putMengAlpha(mMengAlpha);
        SharedPrefUtils.putMengPath(mMengUri.toString());
        SharedPrefUtils.putHasMeng(mHasMeng);
        SharedPrefUtils.putWhichCamera(mWhichCamera);
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
        mPreview.setCamera(mCamera);
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
        mCallBack = new JPEGCallBack(mHandler);
        mCamera.takePicture(null, null, mCallBack);
    }

    public void switchCameraButtonClick(View view) {
        switchCamera();
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
        if (checked != mMengCb.isChecked()) {
            mMengCb.toggle();
        }
    }
}
