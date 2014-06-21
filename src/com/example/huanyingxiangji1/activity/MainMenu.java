package com.example.huanyingxiangji1.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.example.huanyingxiangji1.R;

public class MainMenu extends Activity implements OnClickListener{

	private static final String TAG = MainMenu.class.getName();
	
	Button picButton,groupButton;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        picButton=(Button) findViewById(R.id.main_pic_button);
        groupButton=(Button) findViewById(R.id.main_group_button);
        picButton.setOnClickListener(this);
        groupButton.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

	public void onClick(View v) {
		Intent i=new Intent();
		switch (v.getId()) {
		case R.id.main_pic_button:
			i.setClass(this,PreviewAndPicture.class);
			break;
		case R.id.main_group_button:
			i.setClass(this,GroupList.class);
			break;
		default:
			break;
		}
		startActivity(i);
	}
    
}
