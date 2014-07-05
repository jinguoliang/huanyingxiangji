package com.example.huanyingxiangji1.activity;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.huanyingxiangji1.R;
import com.example.huanyingxiangji1.activity.WorkSetActivity.ImageData;
import com.example.huanyingxiangji1.gif.GifView;
import com.example.huanyingxiangji1.processor.FileProcessor;
import com.example.huanyingxiangji1.processor.SomeTool;
import com.example.huanyingxiangji1.processor.SomeTool.FileType;

@SuppressLint("NewApi")
public class WorkSetActivity extends Activity implements
		LoaderCallbacks<List<ImageData>> {
	public static final String TAG = WorkSetActivity.class.getCanonicalName();
	private GridView mGallery;
	public List<ImageData> mWorks;
	private WorksAdapter mAdapter;
	private Handler mHandler = new Handler() {
		public void dispatchMessage(android.os.Message msg) {
			mGallery.setAdapter(mAdapter);
			mAdapter.notifyDataSetChanged();
		};
	};
	   private Handler gifHandler=new Handler() {

	        @Override
	        public void handleMessage(Message msg) {
	        	((View)(msg.obj)).invalidate();
	            Log.e(TAG,"invalidate redraw");
	        }
	    };
	
	static class ImageData {
		FileType mType;
		Object mData;
		public ImageData(FileType type,Object data) {
			mType=type;
			mData=data;
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.works_galary);

		mGallery = (GridView) findViewById(R.id.works_grid);
		mAdapter = new WorksAdapter();

		setListShown(false);
		
//		List<ImageData> list = new ArrayList<ImageData>();
//		for (String path : FileProcessor.getWorksPaths()) {
//			Log.e(TAG, "path: " + path);
//			FileType fileType = SomeTool.getFileType(path);
//			if (fileType == SomeTool.FileType.GIF) {
//				GifView gifView = new GifView(this);
//				gifView.setGifImageType(GifView.GifImageType.ANIMATION);
//
//				try {
//
//					gifView.setGifImage(new FileInputStream(path));
//					/*
//					 * FileInputStream fileInputStream=new
//					 * FileInputStream(file); byte[] bytes=new
//					 * byte[fileInputStream.available()];
//					 * fileInputStream.read(bytes); gf2.setGifImage(bytes);
//					 */
//					// gf2.setOnClickListener(this);
//					list.add(new ImageData(FileType.GIF, gifView));
//				} catch (IOException e) {
//					Log.e(TAG, "hello");
//					e.printStackTrace();
//				}
//			} else if (fileType == SomeTool.FileType.JPG) {
//				ImageView iv = new ImageView(this);
//				iv.setImageBitmap(BitmapFactory.decodeFile(path));
//				list.add(new ImageData(FileType.JPG, iv));
//			} else {
//			}
//		}
//		mWorks=list;
//		mGallery.setAdapter(mAdapter);
//		setListShown(true);
//		mAdapter.notifyDataSetInvalidated();
		
		// Prepare the loader. Either re-connect with an existing one,
		// or start a new one.
		getLoaderManager().initLoader(0, null, this);
	}

	public class WorksAdapter extends BaseAdapter {
		public WorksAdapter() {
		}

		public View getView(int position, View convertView, ViewGroup parent) {

			return (View) mWorks.get(position).mData;
		}

		public final int getCount() {
			Log.e(TAG,"getCount");
			return mWorks.size();
		}

		public final Object getItem(int position) {
			return mWorks.get(position);
		}

		public final long getItemId(int position) {
			return position;
		}

		public void setData(List<ImageData> list) {
			mWorks = list;
			
		}
	}

	@Override
	public Loader<List<ImageData>> onCreateLoader(int id, Bundle args) {
		// TODO Auto-generated method stub
		return new WorksLoader(WorkSetActivity.this,
				FileProcessor.getWorksPaths(),gifHandler);
	}

	@Override
	public void onLoadFinished(Loader<List<ImageData>> loader, List<ImageData> data) {
		mAdapter.setData(data);
		Log.e(TAG, "onLoadFinished");
		// The list should now be shown.
		mGallery.setAdapter(mAdapter);
		setListShown(true);
		mAdapter.notifyDataSetInvalidated();
	}

	@Override
	public void onLoaderReset(Loader<List<ImageData>> loader) {
		mAdapter.setData(null);

	}

	private void setListShown(boolean b) {
		mGallery.setVisibility(b == true ? View.VISIBLE : View.INVISIBLE);
//		if (b) {
//			mHandler.sendEmptyMessage(0);
//		}
	}
}

/**
 * A custom Loader that loads all of the installed applications.
 */
@SuppressLint("NewApi")
class WorksLoader extends AsyncTaskLoader<List<WorkSetActivity.ImageData>> {

	private static final String TAG = WorksLoader.class.getCanonicalName();
	List<WorkSetActivity.ImageData> mWorks;
	private ArrayList<String> mWorkPaths;
	private Context mContext;
	private Handler mHandler;

	@SuppressLint("NewApi")
	public WorksLoader(Context context, ArrayList<String> paths, Handler h) {
		super(context);
		this.mContext = context;
		this.mWorkPaths = paths;
		this.mHandler=h;

	}

	private List<WorkSetActivity.ImageData> loadWorksView(List<String> imagePaths) {
		Log.e(TAG,"imagePaths: "+imagePaths);
		Log.e(TAG, "loadWorksView");
		
		List<WorkSetActivity.ImageData> list = new ArrayList<WorkSetActivity.ImageData>();
		for (String path : imagePaths) {
			Log.e(TAG, "path: " + path);
			FileType fileType = SomeTool.getFileType(path);
			if (fileType == SomeTool.FileType.GIF) {
				GifView gifView = new GifView(mContext,mHandler);
				gifView.setGifImageType(GifView.GifImageType.ANIMATION);

				try {

					// 上面文件是为了得到InputStream.这里使用固定的文件,你看情况替换.
					gifView.setGifImage(new FileInputStream(path));
					/*
					 * FileInputStream fileInputStream=new
					 * FileInputStream(file); byte[] bytes=new
					 * byte[fileInputStream.available()];
					 * fileInputStream.read(bytes); gf2.setGifImage(bytes);
					 */
					// gf2.setOnClickListener(this);
					list.add(new WorkSetActivity.ImageData(FileType.GIF, gifView));
				} catch (IOException e) {
					Log.e(TAG, "hello");
					e.printStackTrace();
				}
			} else if (fileType == SomeTool.FileType.JPG) {
				ImageView iv = new ImageView(mContext);
				iv.setImageBitmap(BitmapFactory.decodeFile(path));
				list.add(new WorkSetActivity.ImageData(FileType.JPG,iv));
			} else {
			}
		}
		return list;
	}

	/**
	 * This is where the bulk of our work is done. This function is called in a
	 * background thread and should generate a new set of data to be published
	 * by the loader.
	 */
	@Override
	public List<WorkSetActivity.ImageData> loadInBackground() {
		if (mWorkPaths == null) {
			mWorkPaths = new ArrayList<String>();
		}
		Log.e(TAG,"mWorkPaths: "+mWorkPaths);
		return loadWorksView(mWorkPaths);
	}

	/**
	 * Called when there is new data to deliver to the client. The super class
	 * will take care of delivering it; the implementation here just adds a
	 * little more logic.
	 */
	@Override
	public void deliverResult(List<WorkSetActivity.ImageData> works) {
		if (isReset()) {
			// An async query came in while the loader is stopped. We
			// don't need the result.
			if (works != null) {
				onReleaseResources(works);
			}
		}
		List<WorkSetActivity.ImageData> oldWorks = works;
		mWorks = works;

		if (isStarted()) {
			// If the Loader is currently started, we can immediately
			// deliver its results.
			super.deliverResult(works);
		}

		// At this point we can release the resources associated with
		// 'oldApps' if needed; now that the new result is delivered we
		// know that it is no longer in use.
		if (oldWorks != null) {
			onReleaseResources(oldWorks);
		}
	}

	/**
	 * Handles a request to start the Loader.
	 */
	@Override
	protected void onStartLoading() {
		if (mWorks != null) {
			// If we currently have a result available, deliver it
			// immediately.
			deliverResult(mWorks);
		}

		if (takeContentChanged() || mWorks == null) {
			// If the data has changed since the last time it was loaded
			// or is not currently available, start a load.
			forceLoad();
		}
	}

	/**
	 * Handles a request to stop the Loader.
	 */
	@Override
	protected void onStopLoading() {
		// Attempt to cancel the current load task if possible.
		cancelLoad();
	}

	/**
	 * Handles a request to cancel a load.
	 */
	@Override
	public void onCanceled(List<WorkSetActivity.ImageData> apps) {
		super.onCanceled(apps);

		// At this point we can release the resources associated with
		// 'mWorks'
		// if needed.
		onReleaseResources(apps);
	}

	/**
	 * Handles a request to completely reset the Loader.
	 */
	@Override
	protected void onReset() {
		super.onReset();

		// Ensure the loader is stopped
		onStopLoading();

		// At this point we can release the resources associated with
		// 'mWorks'
		// if needed.
		if (mWorks != null) {
			onReleaseResources(mWorks);
			mWorks = null;
		}

	}

	/**
	 * Helper function to take care of releasing resources associated with an
	 * actively loaded data set.
	 */
	protected void onReleaseResources(List<WorkSetActivity.ImageData> apps) {
		// For a simple List<> there is nothing to do. For something
		// like a Cursor, we would close it here.
	}
}
