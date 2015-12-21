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
import com.example.huanyingxiangji1.processor.FileProcessor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class MainMenu extends Activity implements OnClickListener {

    private static final String TAG = MainMenu.class.getName();

    Button picButton, groupButton;

    private Button workset;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);
        picButton = (Button) findViewById(R.id.main_pic_button);
        groupButton = (Button) findViewById(R.id.main_group_button);
        workset = (Button) findViewById(R.id.workset);
        picButton.setOnClickListener(this);
        groupButton.setOnClickListener(this);
        workset.setOnClickListener(this);

        FileProcessor.checkDirs();
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
                break;
            case R.id.main_group_button:
                i.setClass(this, GroupList.class);
                break;
            case R.id.workset:
                i.setClass(this, WorkSetActivity.class);
            default:
                break;
        }
        startActivity(i);
    }
}
