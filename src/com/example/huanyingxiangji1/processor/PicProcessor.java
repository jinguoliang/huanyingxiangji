package com.example.huanyingxiangji1.processor;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

//import com.example.huanyingxiangji1.thirdpart.AnimatedGifEncoder;

public class PicProcessor {
	private static final String TAG = "PicProcessor";
	String tag = "GifProcessor";
	// AnimatedGifEncoder animatedGifEncoder;

	static final public float SCALE_SMALL = 0.2f;
	static final public float SCALE_MID = 0.5f;
	static final public float SCALE_BIG = 1f;

	public PicProcessor() {
		// animatedGifEncoder = new AnimatedGifEncoder();
		// animatedGifEncoder.setQuality(1);
	}

	// 通过uri获得bitmap
	public static Bitmap getBitmapFromUri(Context c, Uri uri, float scale)
			throws Exception {
		Bitmap b = null;
		if (uri.getScheme().equals("content")) {
			// SomeTool.makeToast("从文件管理选择吧，暂时不能从相簿选择，以后会好的，相信我", this);
			b = BitmapFactory.decodeStream(c.getContentResolver()
					.openInputStream(uri));
		} else {
			Log.d(TAG, "Bitmap uri :" + uri);
			b = BitmapFactory.decodeFile(uri.getPath());

		}
		if (b == null) {
			throw new Exception("找不着这图片呀，这是为什么呢？您删了吗？");
		}
		Bitmap tmp = Bitmap.createScaledBitmap(b, (int) (b.getWidth() * scale),
				(int) (b.getHeight() * scale), true);
		b.recycle();
		return tmp;
	}

	static public void storePic(Bitmap bitmap, String picPath)
			throws IOException {
		File file = new File(picPath);
		BufferedOutputStream out = new BufferedOutputStream(
				new FileOutputStream(file));
		bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
		out.flush();
		out.close();
	}

	// 旋转图片
	static public Bitmap rotatePic(Bitmap bitmap) {
		// Log.e("imageview","width:"+mengImageView.getWidth()+" : height: "+mengImageView.getHeight());
		// Log.e("surface","width:"+surfaceView.getWidth()+" : height: "+surfaceView.getHeight());
		// Log.e("bitmap","width:"+bitmap.getWidth()+" : height: "+bitmap.getHeight());
		Matrix matrix = new Matrix();
		matrix.reset();
		matrix.setRotate(-90);
		Bitmap tmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);
		bitmap.recycle();
		// Log.e("bitmap","width:"+bitmap.getWidth()+" : height: "+bitmap.getHeight());
		return tmp;
	}

	// 生成gif
	public void generateGif(String parentDir, List<String> PicPathList,
			String gifPath, int delay) {
		// animatedGifEncoder.start(gifPath);
		// animatedGifEncoder.setDelay(delay);
		// for (Iterator iterator = PicPathList.iterator(); iterator.hasNext();)
		// {
		// String picPath = parentDir + (String) iterator.next();
		// animatedGifEncoder.addFrame(BitmapFactory.decodeFile(picPath));
		// }
		// animatedGifEncoder.finish();
	}

	//
	// //分离gif，通过参数来得到分离的图片，返回值是两图片间的延迟
	// public int seperateGif(String gifPath,ArrayList<BufferedImage>list) {
	// ArrayList<BufferedImage>tmp=new ArrayList<BufferedImage>();
	// int t = 0;
	// GifDecoder d = new GifDecoder();
	// d.read(gifPath);
	// int n = d.getFrameCount();
	// for (int i = 0; i < n; i++) {
	// BufferedImage frame = d.getFrame(i); // frame i
	// t = d.getDelay(i); // display duration of frame in milliseconds
	// list.add(frame);
	// }
	//
	// return t;
	// }

	// 将多个图片排列组合成一个图片
	public void PicCombinate(ArrayList<String> picPathList,
			String destFilePath, int orientation) throws FileNotFoundException {
		Bitmap bitmap = null;
		Bitmap destBitmap = null;
		Canvas canvas = null;
		int cellWidth = 0, cellHeight = 0;

		for (int i = 0; i < picPathList.size(); i++) {
			String fileName = picPathList.get(i);
			Log.e(tag, fileName);
			bitmap = BitmapFactory.decodeFile(fileName);
			// 在第一个图片时确定好目的图片的大小
			if (i == 0) {
				cellWidth = bitmap.getWidth();
				cellHeight = bitmap.getHeight();
				if (orientation == 0) {// 水平排列,
					destBitmap = Bitmap.createBitmap(picPathList.size()
							* cellWidth, cellHeight, Bitmap.Config.RGB_565);
				} else if (orientation == 1) {// 垂直排列
					destBitmap = Bitmap.createBitmap(cellWidth,
							picPathList.size() * cellHeight,
							Bitmap.Config.RGB_565);
				}
			} else {
				resizePicture(bitmap, cellWidth, cellHeight);
			}

			canvas = new Canvas(destBitmap);
			// 把图片画在目的图片上
			if (orientation == 0) {// 水平排列
				canvas.drawBitmap(bitmap, i * cellWidth, 0, null);
			} else if (orientation == 1) {// 垂直排列
				canvas.drawBitmap(bitmap, 0, i * cellHeight, null);
			}
		}

		Log.e(tag, destFilePath);
		destBitmap.compress(Bitmap.CompressFormat.JPEG, 100,
				new FileOutputStream(new File(destFilePath)));
	}

	public Bitmap resizePicture(Bitmap bitmap, int cw, int ch) {
		int w = bitmap.getWidth(), h = bitmap.getHeight();
		float ratioX = (float) cw / w;
		float ratioY = (float) ch / h;
		Matrix matrix = new Matrix();
		matrix.postScale(ratioX, ratioY);
		Bitmap tmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
		bitmap.recycle();
		return tmp;
	}
	
	public static Bitmap turnPicture(Bitmap b){
		Paint p=new Paint();
		Matrix m=new Matrix();
		Bitmap result=Bitmap.createBitmap(b.getWidth(), b.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas=new Canvas(result);
		
		Camera c=new Camera();
		c.rotateY(180);
		c.getMatrix(m);
		m.postTranslate(b.getWidth(), 0);
		
		canvas.drawBitmap(b, m, p);
		b.recycle();

		return result;
	}

}
