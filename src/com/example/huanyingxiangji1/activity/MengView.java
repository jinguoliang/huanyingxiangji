package com.example.huanyingxiangji1.activity;

import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.widget.ImageView;

public class MengView extends ImageView {

	int mAlpha = 5;
	
	

	public MengView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
	}

	// 触摸控制，起始位置
	float ox, oy;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			ox = x;
			oy = y;
			return true;
		case MotionEvent.ACTION_MOVE:
			if (x > ox + 20) {// 右划
			} else if (x < ox - 20) {// 左划
			} else if (y > oy + 20) {// 下划
				if (mAlpha != 0) {
					mAlpha -= 5;
					this.setAlpha(mAlpha);
				}
				oy = y;
			} else if (y < oy - 20) {// 上划
				if (mAlpha != 240) {
					mAlpha += 5;
					this.setAlpha(mAlpha);
				}
				oy = y;
			}

			return true;
		case MotionEvent.ACTION_UP:
			return true;
		default:
			return true;
		}
	}

}
