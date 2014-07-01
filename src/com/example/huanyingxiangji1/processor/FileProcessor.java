package com.example.huanyingxiangji1.processor;

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

import com.example.huanyingxiangji1.MyApplication;

import android.os.Environment;
import android.util.Log;

public class FileProcessor {
	private static final String TAG = FileProcessor.class.getName();

	final String tag = "FileProcessor";

	String groupDirName = "group/";
	String tmpDirName = "tmp/";

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
		// 得到俩个源文件和目的文件
		String destFilePath1 = groupDirFullPath + groupName + "&1.jpg";
		String destFilePath2 = groupDirFullPath + groupName + "&2.jpg";

		// 复制文件
		copyFile(file1, destFilePath1);
		copyFile(file2, destFilePath2);
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
			byte[] buffer = new byte[1024];

			while ((byteread = in.read(buffer)) != -1) {
				out.write(buffer, 0, byteread);
			}
			return true;
		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e) {
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

	public void addToGroup(String groupName, String filePath) {
		int currentIndex = getGroup(groupDirFullPath, groupName).size() + 1;
		String destFileName = groupDirFullPath + groupName + "&" + currentIndex
				+ ".jpg";
		System.out.println(destFileName);
		copyFile(filePath, destFileName);
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
	
	
}
