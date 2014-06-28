package com.example.huanyingxiangji1.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

import com.example.huanyingxiangji1.R;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.UMImage;

public class MainMenu extends Activity {

	private static final String TAG = MainMenu.class.getName();

	Button picButton, groupButton;

	private UMSocialService mController;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		picButton = (Button) findViewById(R.id.main_pic_button);
		groupButton = (Button) findViewById(R.id.main_group_button);

		// ����������Activity��������³�Ա����
		mController = UMServiceFactory.getUMSocialService("com.umeng.share",
				RequestType.SOCIAL);
		// ���÷�������
//		mController
//				.setShareContent("������ữ�����SDK�����ƶ�Ӧ�ÿ��������罻�����ܣ�http://www.umeng.com/social");
		// ���÷���ͼƬ, ����2ΪͼƬ��url��ַ
//		mController.setShareMedia(new UMImage(this,
//				"http://www.umeng.com/images/pic/banner_module_social.png"));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public void onClick(View v) {
		Intent i = new Intent();
		switch (v.getId()) {
		case R.id.main_pic_button:
			i.setClass(this, PreviewAndPicture.class);
			startActivity(i);
			break;
		case R.id.main_group_button:
			i.setClass(this, GroupList.class);
			startActivity(i);
			break;
		case R.id.shared:
			// �Ƿ�ֻ���ѵ�¼�û����ܴ򿪷���ѡ��ҳ
			Log.e(TAG,"shared");
			mController.openShare(this, false);
			break;
		default:
			break;
		}
		
	}

}
