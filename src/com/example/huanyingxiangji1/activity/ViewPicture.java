package com.example.huanyingxiangji1.activity;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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

import com.example.huanyingxiangji1.R;
import com.example.huanyingxiangji1.processor.FileProcessor;
import com.example.huanyingxiangji1.view.MengView;

import java.util.ArrayList;

@SuppressLint("ValidFragment")
public class ViewPicture extends FragmentActivity {
	String tag = ViewPicture.class.getName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pic_view);
		String groupName = getIntent().getStringExtra("groupName");
		FileProcessor processor = new FileProcessor();
		ArrayList<String> list = processor.getGroup(groupName);
		ViewPager vp = (ViewPager) findViewById(R.id.gallary);
		vp.setAdapter(new FragmentPager(this, list));

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
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
			ImageView imageView = new MengView(getActivity());

			imageView.setImageBitmap(BitmapFactory.decodeFile(mImagePath));
			return imageView;
		}
	}

}
