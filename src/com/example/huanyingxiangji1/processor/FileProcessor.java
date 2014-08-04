package com.example.huanyingxiangji1.processor;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore.Images.Media;
import android.util.Log;

import com.example.huanyingxiangji1.MyApplication;

public class FileProcessor {
	private static final String TAG = FileProcessor.class.getName();

	final String tag = "FileProcessor";

	String groupDirName = MyApplication.GROUP_DIR;
	String tmpDirName = MyApplication.TMP_DIR;

	String groupDirFullPath;
	String tmpDirFullPath;

	public FileProcessor(String storeDirName) {
		groupDirFullPath = storeDirName + groupDirName;
		tmpDirFullPath = storeDirName + tmpDirName;
	}

	// 有组名返回该组的文件名列表
	public ArrayList<String> getGroup(String parent, String groupName) {
		ArrayList<String> list = new ArrayList<String>();
		String[] tmp = new File(groupDirFullPath).list();
		for (int i = 0; i < tmp.length; i++) {
			String aGroupNamne = this.getGroupName(tmp[i]);
			if (aGroupNamne != null && aGroupNamne.equals(groupName)) {
				list.add(parent + tmp[i]);
				// Log.e("jin", tmp[i]);
			}
		}
		return list;
	}

	public ArrayList<String> getGroup(String groupName) {
		return getGroup(groupDirFullPath, groupName);
	}

	// 得到存在的组名
	public List<String> getAllGroupName() {
		ArrayList<String> list = new ArrayList<String>() {
			// @Override
			// public boolean contains(Object o) {
			//
			// for (int i = 0; i < size(); i++) {
			// if (((String)o).equals(get(i))) {
			// return true;
			// }
			// }
			// return false;
			// }
		};
		Log.d(TAG,"groupDirFullPath = "+groupDirFullPath);
		String[] tmp = new File(groupDirFullPath).list();
		for (int i = 0; i < tmp.length; i++) {
			String aGroupNamne = this.getGroupName(tmp[i]);
			if (aGroupNamne != null && !list.contains(aGroupNamne)) {
				list.add(aGroupNamne);
				Log.e(tag, aGroupNamne);
			}
		}
		return list;
	}

	// 由文件名得到组名，如果不能得到组名返回null
	public String getGroupName(String fileName) {
		int indexofq = fileName.lastIndexOf("&");
		String tmp = null;
		if (indexofq != -1) {
			tmp = fileName.substring(0, indexofq);
		}
		return tmp;
	}

	// 移除一个组，并可根据后以参数决定是否彻底删除
	public void removeGroup(String groupName, boolean isRealDel) {

		for (Iterator<String> iterator = getGroup(groupDirFullPath, groupName)
				.iterator(); iterator.hasNext();) {
			String filename = iterator.next();
			if (!isRealDel) {// 如果不是彻底删除就先复制到tmp文件夹中
				String destFilePath = tmpDirFullPath + filename;
				copyFile(filename, destFilePath);
			}
			Log.e(TAG, "remove the file: " + filename);
			// 然后彻底删除
			removeFile(filename);
		}

	}

	// 删除一个文件
	public boolean removeFile(String fileName) {
		File file = new File(fileName);
		return file.delete();
	}

	// 创建组，参数为组名，两个文件，以后的文件可以调用addToGroup添加
	public void createGroup(String groupName, String file1, String file2) {


	}
	
	public void createGroup(String groupName, Uri pic1Uri, Uri pic2Uri,
			Context c) throws IOException {
		// 得到俩个源文件和目的文件
		String destFilePath1 = groupDirFullPath + groupName + "&1.jpg";
		String destFilePath2 = groupDirFullPath + groupName + "&2.jpg";

		checkDirs();
		
		Log.e(TAG,"copying....");
		// 复制文件
		InputStream in=c.getContentResolver().openInputStream(pic1Uri);
		OutputStream out=new FileOutputStream(new File(destFilePath1));
		copyFile(in, out);
		in.close();
		out.close();
		in=c.getContentResolver().openInputStream(pic2Uri);
		out=new FileOutputStream(new File(destFilePath2));
		copyFile(in, out);
		in.close();
		out.close();
		Log.e(TAG,"copy ending");
	}

	public boolean copyFile(InputStream in, OutputStream out) {
		int byteread = 0; // 读取的字节数

		try {
			byte[] buffer = new byte[1024];

			while ((byteread = in.read(buffer)) != -1) {
				Log.d(TAG,"pppp");
				out.write(buffer, 0, byteread);
			}
			return true;
		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e) {
			return false;
		} 
	}

	public boolean copyFile(String srcFilePath, String destFilePath) {
		File srcFile = new File(srcFilePath);
		File destFile = new File(destFilePath);
		int byteread = 0; // 读取的字节数
		InputStream in = null;
		OutputStream out = null;

		try {
			in = new FileInputStream(srcFile);
			out = new FileOutputStream(destFile);
			
			return copyFile(in, out);
		} catch (FileNotFoundException e) {
			return false;
		} finally {
			try {
				if (out != null)
					out.close();
				if (in != null)
					in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	


	static public InputStream getInputStreamFrom(Uri uri, Context context) {
		String path = "";
		InputStream in = null;
		if (uri.getScheme().equals("content")) {
			Cursor c = context.getContentResolver().query(uri, null, null,
					null, null);
			c.moveToFirst();
			byte buf[] = c.getBlob(c.getColumnIndex(Media.DATA));
			
			try {
				in = context.getContentResolver().openInputStream(uri);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			try {
				in = new FileInputStream(uri.getPath());
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return in;
	}
	
	public void addToGroup(String groupName, InputStream in) {
		int currentIndex = getGroup(groupDirFullPath, groupName).size() + 1;
		String destFileName = groupDirFullPath + groupName + "&" + currentIndex
				+ ".jpg";
		System.out.println(destFileName);
		OutputStream out = null;
		try {
			out = new FileOutputStream(destFileName);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		copyFile(in, out);
	}
	
	public static String getSDPath(String path){
		return "/sdcard/"+path;
	}

	public static ArrayList<String> getWorksPaths() {
		ArrayList<String>list=new ArrayList<String>();
		File dir = new File(getSDPath(MyApplication.out_path));
		Log.e(TAG,"dir = "+dir.toString());
		if (dir.exists() && dir.isDirectory()) {
			for (String f : dir.list()) {
				Log.e(TAG,"f = " + f);
				list.add(getSDPath(MyApplication.out_path+f));
				
			}
		}
		return list;
	}
	
	
	// 检查内存卡,如果可用返回true
	private static boolean checkMedia() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		} else {
			return false;
		}
	}
	
	// 构建存储数据的目录
	public static  void checkDirs() {
		Log.d(TAG,"check Dirs");
		if (checkMedia()) {
			Log.d(TAG,"check Media finish");
			String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
			checkDir(sdcardPath + MyApplication.APP_SD_DIR);
			Log.d(TAG,"group_path = "+MyApplication.group_path);
			checkDir(sdcardPath +MyApplication.group_path);
			checkDir(sdcardPath +MyApplication.tmp_path);
			checkDir(sdcardPath +MyApplication.out_path);
		}
		else{
			Log.e(TAG,"media card is not mounted");
		}
	}
	
	public static void checkDir(String path){
		File dir = new File(path);
		if (!dir.exists()) {
			dir.mkdir();
		}
	}



	
	
}
