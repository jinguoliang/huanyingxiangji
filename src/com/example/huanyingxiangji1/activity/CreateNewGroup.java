package com.example.huanyingxiangji1.activity;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.huanyingxiangji1.MyApplication;
import com.example.huanyingxiangji1.R;
import com.example.huanyingxiangji1.processor.FileProcessor;
import com.example.huanyingxiangji1.processor.PicProcessor;
import com.example.huanyingxiangji1.utils.LogHelper;

public class CreateNewGroup extends Activity implements OnClickListener {
	private String TAG = CreateNewGroup.class.getSimpleName();

	private static final int REQUEST_SELECT_PIC1 = 1;
	private static final int REQUEST_SELECT_PIC2 = 2;

	private ImageButton picButton1, picButton2;
	private Button okButton, cancelButton;
	private EditText groupNameText;

	private Bitmap mengPic;
	private Bitmap newPic;
	private Uri mPic1Uri;
	private Uri mPic2Uri;

	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_new_group);
		initView();

		if (getIntent().getExtras() == null) {
			return;
		}

		String pic1 = getIntent().getExtras().getString("mengpic");
		String pic2 = getIntent().getExtras().getString("newpic");
		mPic1Uri = Uri.parse(pic1);
		mPic2Uri = Uri.parse(pic2);

		LogHelper.i(TAG, "pic1 path: " + pic1);
		LogHelper.i(TAG, "pic2 path: " + pic2);

		loadImage();
	}

	private void initView() {
		picButton1 = (ImageButton) findViewById(R.id.picButton1);
		picButton2 = (ImageButton) findViewById(R.id.picButton2);
		okButton = (Button) findViewById(R.id.OK);
		cancelButton = (Button) findViewById(R.id.cancel);
		groupNameText = (EditText) findViewById(R.id.groupNameInput);
		okButton.setOnClickListener(this);
		cancelButton.setOnClickListener(this);
		picButton1.setOnClickListener(this);
		picButton2.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.OK:
			finishCreateNewGroup();
			break;
		case R.id.cancel:
			setResult(RESULT_CANCELED);
			this.finish();
			break;
		case R.id.picButton1:
			selectPic(REQUEST_SELECT_PIC1);
			break;
		case R.id.picButton2:
			selectPic(REQUEST_SELECT_PIC2);
			break;
		default:
			break;
		}
	}

	private void selectPic(int requestWhich) {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.setType("image/*");
		startActivityForResult(intent, requestWhich);
	}


	private void finishCreateNewGroup() {
		String path = Environment.getExternalStorageDirectory()
				+ MyApplication.APP_SD_DIR;
		FileProcessor processor = new FileProcessor(path);
		String groupName = groupNameText.getText().toString();
		Uri uri;
		try {
			processor.createGroup(groupName, mPic1Uri,
					mPic2Uri,this);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.finish();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	private void loadImage() {
		try {
			mengPic = PicProcessor.getBitmapFromUri(this, mPic1Uri,
					PicProcessor.SCALE_MID);
			newPic = PicProcessor.getBitmapFromUri(this, mPic2Uri,
					PicProcessor.SCALE_MID);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		picButton1.setImageBitmap(mengPic);
		picButton2.setImageBitmap(newPic);

		picButton1.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			@Override
			public boolean onPreDraw() {
				adjustImageViewHeight(picButton1, mengPic);
				picButton1.getViewTreeObserver().removeOnPreDrawListener(this);
				return true;
			}
		});
		picButton2.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			@Override
			public boolean onPreDraw() {
				adjustImageViewHeight(picButton2, newPic);
				picButton1.getViewTreeObserver().removeOnPreDrawListener(this);
				return true;
			}
		});
	}

	private void adjustImageViewHeight(ImageButton view, Bitmap bitmap) {
		ViewGroup.LayoutParams params = view.getLayoutParams();
		int bitmapWidth = bitmap.getWidth();
		int bitmapHeight = bitmap.getHeight();
		LogHelper.i(TAG, "the bitmap size (" + bitmapWidth + ", " + bitmapHeight + ")");
		LogHelper.i(TAG, "the view size (" + params.width + ", " + params.height + ")");
		int viewWidth = view.getWidth();
		LogHelper.i(TAG, "the view size (" + view.getWidth() + ", " + view.getHeight() + ")");

		params.height = (int) (bitmapHeight * (float)viewWidth / bitmapWidth);
		view.setLayoutParams(params);
	}

	@Override
	protected void onDestroy() {
		mengPic.recycle();
		newPic.recycle();
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_CANCELED) {
			return;
		}

		if (data == null) {
			throw new RuntimeException("no data");
		} else {
			Uri uri = data.getData();
			switch (requestCode) {
			case REQUEST_SELECT_PIC1:
				mPic1Uri = uri;
				break;
			case REQUEST_SELECT_PIC2:
				mPic2Uri = uri;
				break;
			default:
				break;
			}
		}
		loadImage();
	}
}
