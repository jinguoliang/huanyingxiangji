package com.example.huanyingxiangji1.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

public class MengView extends ImageView {

	private static final String TAG = MengView.class.getName();
	private static final float TOILERENCE = 5;
	int mAlpha = 5;

	float mInitWidth;
	float mInitHeight;
	private Bitmap mBitmap;

	PointF Origin = new PointF(0, 0);

	public MengView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MengView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void setImageBitmap(Bitmap bm) {
		super.setImageBitmap(bm);
		mInitHeight = bm.getHeight();
		mInitWidth = bm.getWidth();
		mBitmap = bm;
	}

	// the touch down pointer x, y,
	// if two pointer, the distance between them, the center pointer between
	// them
	float ox, oy, oPointerDistence;
	PointF oCenterPosition;

	float ddx, ddy;
	private float tmpWidth;
	private float tmpHeight;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();

		int touchCount = event.getPointerCount();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:

			ox = x; // the touch down position
			oy = y;

			return true;
		case MotionEvent.ACTION_MOVE:
			if (touchCount == 1) { // on pointer move
				//processOneTouch(x - ox, y - oy);
			} else if (touchCount == 2) { // two pointer move

				float x1 = event.getX(1);
				float y1 = event.getY(1);
				float x2 = event.getX(0);
				float y2 = event.getY(0);
				Log.e(TAG, "x1 = " + x1);
				Log.e(TAG, "y1 = " + y1);
				Log.e(TAG, "x2 = " + x2);
				Log.e(TAG, "y2 = " + y2);

				processTwoTouch(x1, y1, x2, y2);

			}

			return true;
		case MotionEvent.ACTION_UP:

			Log.e(TAG, "touch up");
			oPointerDistence = 0;
//			mScale = mNowScale;
//			mSHeight = mNowHeight;
//			mSWidth = mNowWidth;
//			Origin.x = mBitmapOffsetX;
//			Origin.y = mBitmapOffsetY;
			mBitmapOffsetX = mBitmapOffsetY =0;

			return true;
		default:
			return true;
		}

	}
	
	float mBitmapOffsetX=0;
	float mBitmapOffsetY=0;

	private void processTwoTouch(float x1, float y1, float x2, float y2) {

		float pointerDistence = (float) Math.sqrt(Math.pow(x1 - x2, 2)
				+ Math.pow(y1 - y2, 2));
		PointF targetCenter = new PointF((x1 + x2) / 2, (y1 + y2) / 2);
		Log.e(TAG, "center = " + targetCenter);
		Log.e(TAG, "touch two");
		Log.e(TAG, "pointerDistence =" + pointerDistence);

		Log.e(TAG, "touch move");
		if (oPointerDistence == 0) {
			oPointerDistence = pointerDistence;
			oCenterPosition = targetCenter;
			return;
		}
		
		if (Math.abs(oPointerDistence - pointerDistence) > TOILERENCE * 2
				|| Math.abs(targetCenter.x - oCenterPosition.x) > TOILERENCE
				|| Math.abs(targetCenter.y - oCenterPosition.y) > TOILERENCE) {
			
			Matrix matrix = getImageMatrix();

			float scale = pointerDistence / oPointerDistence;
			matrix.postScale(scale, scale);
			
			Log.e(TAG,"scale = "+scale);
			mNowScale = mNowScale * scale;

			
			
			tmpWidth = (oCenterPosition.x - Origin.x);
			tmpHeight = (oCenterPosition.y - Origin.y);

			PointF center2 = new PointF(tmpWidth * scale, tmpHeight
					* scale);

			float dx = targetCenter.x - center2.x;
			float dy = targetCenter.y - center2.y;

			mBitmapOffsetX+=dx;
			mBitmapOffsetX+=dy;
			matrix.postTranslate(dx, dy);

			Log.e(TAG,"matrix = "+matrix);
			oPointerDistence = pointerDistence;
			oCenterPosition = targetCenter;
			
			invalidate();
		}

	}

	private void processOneTouch(float dx, float dy) {

		if (dx > TOILERENCE) { // swipe to right
			// TODO What's up?
		} else if (dx < -TOILERENCE) {// swipe to left
			// TODO what's up?
		} else if (dy > TOILERENCE) {// swipe to down -- minus the
										// aplha
			if (mAlpha != 0) {
				mAlpha -= 5;
				this.setAlpha(mAlpha);
			}
			oy = oy + dy;
		} else if (dy < -TOILERENCE) {// swipe to up -- plus the
										// alpha
			if (mAlpha != 240) {
				mAlpha += 5;
				this.setAlpha(mAlpha);
			}
			oy = oy + dy;
		}
	}

//	float mScale = 1;
	float mNowScale = 1;

	float mSWidth = 1;
	float mSHeight = 1;
	float mNowWidth = 1;
	float mNowHeight = 1;

//	private void changeScale(float value) {
//		Log.e(TAG, "the scale is " + value);
//		Matrix matrix = getImageMatrix();
//		mNowScale = mScale * value;
//		matrix.setScale(mNowScale, mNowScale);
//		if (mSWidth == 1) {
//			mSWidth = mInitWidth;
//			mSHeight = mInitHeight;
//		}
//		mNowHeight = mSWidth * value;
//		mNowWidth = mSHeight * value;
//		matrix.postTranslate(-(mNowWidth - mSWidth) / 2,
//				-(mNowHeight - mSHeight) / 2);
//
//	}
}
