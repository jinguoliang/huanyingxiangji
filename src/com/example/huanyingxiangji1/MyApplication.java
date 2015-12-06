package com.example.huanyingxiangji1;

import android.app.Application;
import android.graphics.Bitmap;
import android.os.Environment;

import java.util.HashMap;
import java.util.Map;

public class MyApplication extends Application {
	private static MyApplication sMyApplication;

	final public static String APP_SD_DIR= Environment.getExternalStorageDirectory() + "/huanyingxiangji/"; //the root of application
	final public static String GROUP_DIR="group/";
	final public static String TMP_DIR="tmp/";	
	final public  static String OUT_DIR="out/"; 
	final public  static String PIC_DIR="picture/";

	public static final String group_path=APP_SD_DIR+GROUP_DIR;
	public static final String tmp_path=APP_SD_DIR+TMP_DIR;
	public static final String out_path=APP_SD_DIR+OUT_DIR;
	public static final String pic_path=APP_SD_DIR+PIC_DIR;

	@Override
	public void onCreate() {
		super.onCreate();
		sMyApplication = this;
	}

	public static MyApplication getInstance(){
		return sMyApplication;
	}
}
