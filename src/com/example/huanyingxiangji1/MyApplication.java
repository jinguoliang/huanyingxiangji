package com.example.huanyingxiangji1;

import java.util.HashMap;
import java.util.Map;

import android.app.Application;
import android.graphics.Bitmap;

public class MyApplication extends Application {
	final public static String APP_SD_DIR="/huanyingxiangji/"; //the root of application
	final public static String GROUP_DIR="group/";
	final public static String TMP_DIR="tmp/";	
	final public  static String OUT_DIR="out/"; 
	public static final String group_path=APP_SD_DIR+GROUP_DIR;
	public static final String tmp_path=APP_SD_DIR+TMP_DIR;
	public static final String out_path=APP_SD_DIR+OUT_DIR;
	
	public final static String mengPic="mengPic"; 
	public  static String newPicPath=""; 
	
	private static Map<String, Bitmap>picMap=new HashMap<String, Bitmap>();
	public static Bitmap getPic(String picName) {
		synchronized (picMap) {
			return picMap.get(picName);
		}
	}
	public static void putPic(String picName,Bitmap b) {
		synchronized (picMap) {
			MyApplication.picMap.put(picName, b);
		}
	}
	

}
