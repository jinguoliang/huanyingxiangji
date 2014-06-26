package com.example.huanyingxiangji1.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.huanyingxiangji1.MyApplication;
import com.example.huanyingxiangji1.R;
import com.example.huanyingxiangji1.processor.FileProcessor;
import com.example.huanyingxiangji1.processor.PicProcessor;
import com.example.huanyingxiangji1.processor.SomeTool;

public class CreateNewGroup extends Activity implements OnClickListener {

	String pic1, pic2;
	ImageButton picButton1, picButton2;
	Button okButton, cancelButton;
	EditText groupNameText;
	private String TAG = "CreateNewGroup";
	private Bitmap mengPic;
	private Bitmap newPic;

	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_new_group);
		// 绑定控件
		picButton1 = (ImageButton) findViewById(R.id.picButton1);
		picButton2 = (ImageButton) findViewById(R.id.picButton2);
		okButton = (Button) findViewById(R.id.OK);
		cancelButton = (Button) findViewById(R.id.cancel);
		groupNameText = (EditText) findViewById(R.id.groupNameInput);

		// 读取两个图片
		pic1 = getIntent().getExtras().getString("mengpic");
		pic2 = getIntent().getExtras().getString("newpic");

		Log.e(TAG, "pic1 path: " + pic1);
		Log.e(TAG, "pic2 path: " + pic2);

		try {
			mengPic = PicProcessor.getBitmapFromUri(this, Uri.parse(pic1),
					PicProcessor.SCALE_SMALL);
			newPic = PicProcessor.getBitmapFromUri(this, Uri.parse(pic2),
					PicProcessor.SCALE_SMALL);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		picButton1.setImageBitmap(mengPic);
		picButton2.setImageBitmap(newPic);

		okButton.setOnClickListener(this);
		cancelButton.setOnClickListener(this);
		picButton1.setOnClickListener(this);
		picButton2.setOnClickListener(this);

	}

	@Override
	public void onClick(View arg0) {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.setType("image/*");
		switch (arg0.getId()) {
		case R.id.OK:
			String path = Environment.getExternalStorageDirectory()
					+ MyApplication.APP_SD_DIR;
			FileProcessor processor = new FileProcessor(path);
			String groupName = groupNameText.getText().toString();
			processor.createGroup(groupName, pic1.substring(7),
					pic2.substring(7));
			intent.putExtra("groupName", groupName);
			setResult(5, intent);
			this.finish();
			break;
		case R.id.cancel:
			setResult(4);
			this.finish();
			break;
		case R.id.picButton1:
			startActivityForResult(intent, 1);
			break;
		case R.id.picButton2:
			startActivityForResult(intent, 2);
			break;
		default:
			break;
		}
	};

	@Override
	protected void onStop() {

		super.onStop();
	}

	@Override
	protected void onDestroy() {
		mengPic.recycle();
		newPic.recycle();
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode==RESULT_CANCELED) {
			return ;
		}
		
		if (data == null) {
			throw new RuntimeException("no data");
		} else {
			Uri uri = data.getData();
			switch (requestCode) {
			case 1:

				try {
					picButton1.setImageBitmap(PicProcessor.getBitmapFromUri(
							this, uri, PicProcessor.SCALE_SMALL));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// getContentResolver().query(uri, projection, selection,
				// selectionArgs, sortOrder)
				pic1 = uri.toString();
				break;
			case 2:
				try {
					picButton2.setImageBitmap(PicProcessor.getBitmapFromUri(
							this, uri, PicProcessor.SCALE_SMALL));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				pic2 = uri.toString();
				break;
			default:
				break;
			}
		}
	}
}
