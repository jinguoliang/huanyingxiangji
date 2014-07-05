package com.example.huanyingxiangji1.activity;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.huanyingxiangji1.MyApplication;
import com.example.huanyingxiangji1.R;
import com.example.huanyingxiangji1.processor.FileProcessor;

@SuppressLint("ValidFragment")
public class ViewPicture extends FragmentActivity {
	String tag = ViewPicture.class.getName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pic_view);
		// 通过全局变量传过来的图片数据
		String groupName = getIntent().getStringExtra("groupName");
		String dataDir = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/" + MyApplication.APP_SD_DIR;
		FileProcessor processor = new FileProcessor(dataDir);
		ArrayList<String> list = processor.getGroup(groupName);
		ViewPager vp = (ViewPager) findViewById(R.id.gallary);
		vp.setAdapter(new FragmentPager(this, list));

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		return super.onContextItemSelected(item);
	}

	class FragmentPager extends FragmentPagerAdapter {

		private ArrayList<String> filePath;
		private FragmentActivity mContext;

		public FragmentPager(FragmentActivity c, ArrayList<String> filePath) {
			super(c.getSupportFragmentManager());
			mContext = c;
			this.filePath = filePath;
		}

		@Override
		public Fragment getItem(int position) {
			return new ViewFragment(filePath.get(position));
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return filePath.size();
		}

	}

	class ViewFragment extends Fragment {
		private String mImagePath;

		@SuppressLint("ValidFragment")
		public ViewFragment(String path) {
			mImagePath = path;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			ImageView imageView = new ImageView(getActivity());

			imageView.setImageBitmap(BitmapFactory.decodeFile(mImagePath));
			return imageView;
		}
	}

}
