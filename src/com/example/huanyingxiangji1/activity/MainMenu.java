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

		// 首先在您的Activity中添加如下成员变量
		mController = UMServiceFactory.getUMSocialService("com.umeng.share",
				RequestType.SOCIAL);
		// 设置分享内容
//		mController
//				.setShareContent("友盟社会化组件（SDK）让移动应用快速整合社交分享功能，http://www.umeng.com/social");
		// 设置分享图片, 参数2为图片的url地址
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
			// 是否只有已登录用户才能打开分享选择页
			Log.e(TAG,"shared");
			mController.openShare(this, false);
			break;
		default:
			break;
		}
		
	}

}
