package com.example.huanyingxiangji1.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import com.example.huanyingxiangji1.activity.WorkSetActivity;

import java.util.List;

/**
 * Created by jinux on 15/10/6.
 */
public class AdvancedGallary extends ViewGroup {

    public AdvancedGallary(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int hight = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);
            int w  = childView.getMeasuredWidth();
            int h = childView.getMeasuredHeight();
            hight += h;
        }

        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(hight, MeasureSpec.EXACTLY));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int r, int b) {
        int mViewGroupWidth  = getMeasuredWidth();  //当前ViewGroup的总宽度

        int mPainterPosX = left;  //当前绘图光标横坐标位置
        int mPainterPosY = top;  //当前绘图光标纵坐标位置

        int childCount = getChildCount();
        for ( int i = 0; i < childCount; i++ ) {

            View childView = getChildAt(i);

            int width  = childView.getMeasuredWidth();
            int height = childView.getMeasuredHeight();

            //执行ChildView的绘制
            childView.layout(mPainterPosX,mPainterPosY,mPainterPosX+width, mPainterPosY+height);

            mPainterPosY += height;
        }
    }

    public AdvancedGallary(Context context) {
        super(context);
    }

    public void addViewData(List<WorkSetActivity.ImageData> data) {
        for (WorkSetActivity.ImageData d : data) {
            addView((View) d.mData);
        }
    }
}
