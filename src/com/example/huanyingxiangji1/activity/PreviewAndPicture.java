package com.example.huanyingxiangji1.activity;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.huanyingxiangji1.MyApplication;
import com.example.huanyingxiangji1.R;
import com.example.huanyingxiangji1.R.id;
import com.example.huanyingxiangji1.processor.PicProcessor;
import com.example.huanyingxiangji1.processor.SomeTool;

public class PreviewAndPicture extends Activity {

	static String TAG = PreviewAndPicture.class.getName();

	private MengView mengImageView;
	private ImageButton pictureButton;
	private CameraPreview mPreview;
	private static Camera mCamera;
	private String savePath;
	private boolean hasMeng;
	private Uri mengUri;
	private int alpha = 5;
	private Bitmap bitmap;

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				mCamera.startPreview();
				break;
			case 1:
				if (PreviewAndPicture.this.hasMeng) {
					Log.e(TAG, "ok to create group");

					Intent i = new Intent(PreviewAndPicture.this,
							CreateNewGroup.class);
					i.putExtra("mengpic", mengUri.toString());
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
		setContentView(R.layout.pic_activity);

		// pictureButton = (ImageButton) findViewById(R.id.pic_button);

		readPreference();

		initialView();

		readPreference();
		Log.e(TAG, "mengUriString = " + mengUri);
		loadMeng(false);
	}

	private void initialView() {
		if (SomeTool.checkCameraHardware(this, SomeTool.CAMERA_FRONT)) {
			mCamera = SomeTool.getCameraInstance(SomeTool.CAMERA_FRONT);

			mPreview = new CameraPreview(this, mCamera);
			RelativeLayout preview = (RelativeLayout) findViewById(id.surfaceRelativeLayout);
			preview.addView(mPreview);

			mengImageView = new MengView(this);

			// mengImageView.setScaleType(ScaleType.FIT_END);
			// RelativeLayout.LayoutParams l=new
			// RelativeLayout.LayoutParams(mPreview.getWidth(),
			// mPreview.getWidth());
			//
			// preview.addView(mengImageView, l);
			preview.addView(mengImageView, new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

			addShotterButton(preview);

		} else {
			Toast.makeText(this, "no camera", Toast.LENGTH_LONG).show();
			this.finish();
		}
	}

	private void addShotterButton(RelativeLayout preview) {
		RelativeLayout.LayoutParams params = new android.widget.RelativeLayout.LayoutParams(
				200, 100);
		params.alignWithParent = true;
		params.addRule(RelativeLayout.ALIGN_BOTTOM);
		params.addRule(RelativeLayout.CENTER_HORIZONTAL);

		Button b = new Button(this);
		b.setText("picture");
		preview.addView(b, params);

		b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				pictureButtonClick(v);
			}
		});
	}

	private void readPreference() {
		SharedPreferences mengPicPreference = getSharedPreferences(
				"mengPicPreference", MODE_PRIVATE);
		hasMeng = mengPicPreference.getBoolean("hasMeng", false);
		mengUri = Uri.parse(mengPicPreference.getString("mengFullPath", ""));

		// File
		File storeDir = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		Log.e(TAG, storeDir.getAbsolutePath());
		savePath = mengPicPreference.getString("savePath",
				storeDir.getAbsolutePath());
		alpha = mengPicPreference.getInt("alpha", 75);
	}

	@Override
	protected void onStart() {
		super.onStart();

	}

	@Override
	protected void onRestart() {
		super.onRestart();
		Log.e(TAG, "onRestart");
		Log.e(TAG, "hasMeng ? " + hasMeng + " bitmap = " + bitmap);
		Log.e(TAG, "mengUriString = " + mengUri);

		loadMeng(false);

	};

	private void loadMeng(boolean isReload) {
		// ������
		if (hasMeng && (bitmap == null || isReload)) {
			try {
				Log.e(TAG, "loadMeng.....");
				bitmap = PicProcessor.getBitmapFromUri(this, mengUri,
						PicProcessor.SCALE_MID);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.e(TAG, "wrong");
				e.printStackTrace();
			}
			// MyApplication.putPic(MyApplication.mengPic, bitmap);
			if (bitmap == null) {
				Toast.makeText(this, "no bitmap", Toast.LENGTH_LONG).show();
			} else {
				// if (bitmap.getHeight() < bitmap.getWidth()) {
				// bitmap = PicProcessor.rotatePic(bitmap);
				// }
				mengImageView.setImageBitmap(bitmap);
				mengImageView.setAlpha(alpha);
				mengImageView.setVisibility(View.VISIBLE);
			}

		} else {
			mengImageView.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	protected void onStop() {
		Log.e(TAG, "onStop");

		if (bitmap != null) {
			bitmap.recycle();
			bitmap = null;
		}
		storePreference();

		super.onStop();
	}

	private void storeMengUrl() {
		SharedPreferences mengPicPreference = getSharedPreferences(
				"mengPicPreference", MODE_PRIVATE);
		SharedPreferences.Editor mengPicPrefrerenceEditor = mengPicPreference
				.edit();
		mengPicPrefrerenceEditor.remove("mengFullPath");
		mengPicPrefrerenceEditor.putString("mengFullPath", mengUri.toString());
		mengPicPrefrerenceEditor.commit();
	}

	private void storePreference() {
		SharedPreferences mengPicPreference = getSharedPreferences(
				"mengPicPreference", MODE_PRIVATE);
		SharedPreferences.Editor mengPicPrefrerenceEditor = mengPicPreference
				.edit();
		mengPicPrefrerenceEditor.remove("alpha");
		mengPicPrefrerenceEditor.putInt("alpha", alpha);
		mengPicPrefrerenceEditor.remove("mengFullPath");
		mengPicPrefrerenceEditor.putString("mengFullPath", mengUri.toString());
		mengPicPrefrerenceEditor.remove("savePath");
		mengPicPrefrerenceEditor.putString("savePath", savePath);
		mengPicPrefrerenceEditor.remove("hasMeng");
		mengPicPrefrerenceEditor.putBoolean("hasMeng", hasMeng);
		mengPicPrefrerenceEditor.commit();
	}

	@Override
	protected void onDestroy() {
		mCamera.release();
		super.onDestroy();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 1, "Meng");

        menu.add(0, 0, 0, "unmeng");

        if (hasMeng) {
		} else {
			menu.getItem(1).setVisible(false);
		}
		menu.add(0, 2, 2, "path");
		menu.add(0, 3, 3, "switch");

		mengMenu = menu.getItem(1);
		return true;
	}

	MenuItem mengMenu;

	private int mWhichCamera=SomeTool.CAMERA_FRONT;

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:	
			if (hasMeng) {
				mengImageView.setVisibility(View.INVISIBLE);
				item.setTitle("unmeng");
			} else {
				mengImageView.setVisibility(View.VISIBLE);
				item.setTitle("meng");
			}
			hasMeng = !hasMeng;
			mengMenu.setVisible(hasMeng);

			break;
		case 1:
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.addCategory(Intent.CATEGORY_DEFAULT);
			intent.setType("image/*");
			startActivityForResult(intent, 0);
			break;
		case 2:
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
								savePath = tmp;

							} else {
								Toast.makeText(PreviewAndPicture.this,
										"not a directory", Toast.LENGTH_LONG).show();
							}
						}
					});
			builder.setNegativeButton("cancel", null);
			builder.show();
			break;
		case 3:// picture
			if (mWhichCamera==SomeTool.CAMERA_BACK) {
				changeCamera(SomeTool.CAMERA_FRONT);
			}else{
				changeCamera(SomeTool.CAMERA_BACK);
			}
		}
		return true;
	}

	private void changeCamera(int which) {
		// TODO Auto-generated method stub
		if (mWhichCamera==which) return;
		
		
		if (SomeTool.checkCameraHardware(this, which)) {
			mCamera=SomeTool.getCameraInstance(which);
		}
		mPreview.setCamera(mCamera);
		mWhichCamera=which;
	}

	private static final int REQUEST_SELECT_PIC = 0;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.e(TAG, "onActivityResult");
		switch (requestCode) {
		case REQUEST_SELECT_PIC:
			if (resultCode == RESULT_OK && data != null) {
				// this.bitmap =
				// MyApplication.getPic(MyApplication.mengPic);
				// if (this.bitmap != null && !this.bitmap.isRecycled()) {
				// this.bitmap.recycle();
				// }

				mengUri = data.getData();

				Log.e(TAG, mengUri.toString());
				// bitmap = Media.getBitmap(getContentResolver(),
				// mengFileUri);

				bitmap = null;

				storePreference();
			} else {
				if (mengUri == null) {
					hasMeng = false;
				}
			}
			break;

		default:
			break;
		}
	}

	String fileName = null;

	/**
	 * when touch the picture button ,this function will be called
	 * 
	 * @param v
	 */
	public void pictureButtonClick(View v) {
		fileName = SomeTool.genPicPathName(savePath);
		JPEGCallBack jpegCallBack = new JPEGCallBack(mHandler);
		jpegCallBack.setPath(fileName);

		try {
			mCamera.takePicture(null, null, jpegCallBack);
		} catch (Exception e) {
			Log.e(TAG, "takepicture error");
			e.printStackTrace();
		}
		Toast.makeText(PreviewAndPicture.this, "�ѱ���Ϊ" + fileName,
				Toast.LENGTH_LONG).show();
		// Canvas canvas=surfaceView.getHolder().lockCanvas();
		// canvas.drawBitmap(bitmap, 0, 0,null);
		// surfaceView.getHolder().unlockCanvasAndPost(canvas);
	}

}
