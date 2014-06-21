package com.example.huanyingxiangji1.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.SimpleAdapter.ViewBinder;

import com.example.huanyingxiangji1.MyApplication;
import com.example.huanyingxiangji1.R;
import com.example.huanyingxiangji1.processor.FileProcessor;
import com.example.huanyingxiangji1.processor.PicProcessor;

public class GroupList extends ListActivity implements OnItemClickListener{
	private static final int CREATE_GROUP = 1;
	String tag="GroupList";
	List<Map<String, Object>>list;
	MyApplication application;
	FileProcessor fileProcessor;
	PicProcessor picProcessor;
	private String groupName;
	private String dataDir;
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.group_list);
		application=(MyApplication) getApplication();
			AbsListView l;
		list=getData();
		SimpleAdapter adapter=new SimpleAdapter(this, list, R.layout.group_list_item, 
				new String[]{"groupName","preview0","preview1","preview2","preview3"},
				new int[]{R.id.groupName,R.id.preview0,R.id.preview1,R.id.preview2,R.id.preview3});
		adapter.setViewBinder(new ViewBinder() {
			@Override
			public boolean setViewValue(View view, Object data,
					String textRepresentation) {
				if(view instanceof ImageView){
					((ImageView)view).setImageBitmap((Bitmap) data);
					Log.e(tag,textRepresentation);
					return true;
				}
				return false;
			}
		});
		setListAdapter(adapter);
		registerForContextMenu(getListView());
		getListView().setOnItemClickListener(this);
		
	}
	private List<Map<String, Object>> getData() {
		List<Map<String, Object>>list;
		Map<String, Object>map;
		
		checkDir();
		
		dataDir=Environment.getExternalStorageDirectory().getAbsolutePath()+
				"/"+application.APP_SD_DIR;
		fileProcessor=new FileProcessor(dataDir);
		
		list=new ArrayList<Map<String,Object>>();
		List<String>groupNames=fileProcessor.getAllGroupName();
		for (Iterator<String> iterator = groupNames.iterator(); iterator.hasNext();) {
			String groupName = (String) iterator.next();
			map=new HashMap<String, Object>();
			map.put("groupName", groupName);
//			读取组内的图片
			List<String>filePaths=fileProcessor.getGroup(groupName);
			for (int i=0;i<filePaths.size();i++) {
				String picPath = filePaths.get(i);
				Log.e(tag,picPath);
				map.put("preview"+i, BitmapFactory.decodeFile(picPath));
				if (i==3) {
					break;
				}
			}
			list.add(map);
		}
//		PicProcessor gifProcessor=new PicProcessor();
		//			gifProcessor.PicCombinate(dataDir+application.group, fileNames,
//					dataDir+application.group+"aa.jpg", 1);
//		gifProcessor.generateGif("", fileProcessor.getGroup("jin"),dataDir+application.group+"aa.gif", 1000);
		return list;
	}
	//检查内存卡,如果可用返回true
	private boolean checkMedia() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}else {
		    return false;
		}
	}
//	构建存储数据的目录
	private void checkDir() {
		if (checkMedia()) {
			File sdcard=Environment.getExternalStorageDirectory();
			File appDataDirFile=new File(sdcard.getAbsolutePath()+"/"+MyApplication.APP_SD_DIR);
			if (!appDataDirFile.exists()) {
				appDataDirFile.mkdir();
			}
			File groupDirFile=new File(MyApplication.group_path);
			File tmpDirFile=new File(MyApplication.tmp_path);
			File outDirFile=new File(MyApplication.out_path);
			if (!groupDirFile.exists()) {
				groupDirFile.mkdir();
			}
			if (!tmpDirFile.exists()) {
				tmpDirFile.mkdir();
			}
			if (!outDirFile.exists()) {
				outDirFile.mkdir();
			}
		}
	}
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.group_list_context_menu, menu);
	}
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		int id=(int) info.id; 
		if (-1 == id) {
		      super.onContextItemSelected(item);
		}
		Map<String, Object>map=list.get(id);
		groupName=(String)map.get("groupName");
		switch (item.getItemId()) {
		  case R.id.newGroup:
			  Log.e(tag,"new");
			  Intent i=new Intent(this, CreateNewGroup.class);
			  startActivityForResult(i,CREATE_GROUP);
		    return true;
		  case R.id.deleteGroup:
			  Log.e(tag,"del");
			  fileProcessor.removeGroup(groupName, false);
			  list.remove(id);
			  ((BaseAdapter) getListAdapter()).notifyDataSetChanged();
			  return true;
		  case R.id.generateGif:
			  return true;
		  case R.id.combinate_h:
			  picProcessor=new PicProcessor();
			  try {
				  String destPic= MyApplication.out_path+groupName+".jpg";
				picProcessor.PicCombinate(fileProcessor.getGroup(groupName),destPic, 0);
				Toast.makeText(this, "已保存到"+MyApplication.out_path, Toast.LENGTH_LONG).show();
			  } catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			  return true;
		  case R.id.combinate_v:
			  picProcessor=new PicProcessor();
			  try {
				  String destPic= MyApplication.out_path+groupName+".jpg";
				picProcessor.PicCombinate(fileProcessor.getGroup(groupName),destPic, 1);
				Toast.makeText(this, "已保存到"+MyApplication.out_path, Toast.LENGTH_LONG).show();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			  return true;
		  case R.id.add_new_pic:
			  Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.addCategory(Intent.CATEGORY_DEFAULT);
				 intent.setType("image/*");
				 startActivityForResult(intent, 7);
			  return true;
		  default:
		    return super.onContextItemSelected(item);
		  }
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
		Intent i=new Intent(this, ViewPicture.class);
		i.putExtra("groupName", list.get(pos).get("groupName").toString());
		startActivity(i);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode==CREATE_GROUP&&resultCode==5) {
			Map<String, Object>map=new HashMap<String, Object>();
			String groupName= data.getExtras().getString("groupName");
			map.put("groupName",groupName);
			List<String>filePaths=fileProcessor.getGroup(groupName);
			for (int i=0;i<filePaths.size();i++) {
				String picPath = filePaths.get(i);
				Log.e(tag,picPath);
				map.put("preview"+i, BitmapFactory.decodeFile(picPath));
				if (i==3) {
					break;
				}
			}
			list.add(map);
			  ((BaseAdapter) getListAdapter()).notifyDataSetChanged();
		}else if (requestCode==7) {
			fileProcessor.addToGroup(groupName,data.getData().toString().substring(7) );
			//还应该更新列表
		}
	}
	
}

