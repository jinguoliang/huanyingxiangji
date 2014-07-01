package com.example.huanyingxiangji1.gif;

import java.io.InputStream;
import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * GifView<br>
 * 本类可以显示�?��gif动画，其使用方法和android的其它view（如imageview)�?���?br>
 * 如果要显示的gif太大，会出现OOM的问题�?
 *
 * @author liao
 * @author archko 修改为解析所有图�?然后传回来播�?
 */
public class GifView extends View implements GifAction {

    public static final String TAG="GifView";
    /**
     * gif解码�?
     */
    private GifDecoder gifDecoder=null;
    /**
     * 当前要画的帧的图
     */
    private Bitmap currentImage=null;

    private boolean isRun=true;

    private boolean pause=false;

    private int showWidth=-1;
    private int showHeight=-1;
    private Rect rect=null;

    public void setRun(boolean run) {
        isRun=run;
    }

    public void setPause(boolean pause) {
        this.pause=pause;
    }

    private DrawThread drawThread=null;

    private GifImageType animationType=GifImageType.ANIMATION;

    /**
     * 解码过程中，Gif动画显示的方�?br>
     * 如果图片较大，那么解码过程会比较长，这个解码过程中，gif如何显示
     *
     * @author liao
     */
    public enum GifImageType {
        /**
         * 在解码过程中，不显示图片，直到解码全部成功后，再显示，废�?
         */
        WAIT_FINISH(0),
        /**
         * 和解码过程同步，解码进行到哪里，图片显示到哪里，废除
         */
        SYNC_DECODER(1),
        /**
         * 只显示第�?��图片
         */
        COVER(2),
        /**
         * 动画显示�?���?
         */
        ANIMATION(3);

        GifImageType(int i) {
            nativeInt=i;
        }

        final int nativeInt;
    }

    public GifView(Context context) {
        super(context);
    }

    public GifView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GifView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * 设置图片，并�?��解码
     *
     * @param gif 要设置的图片
     */
    public void setGifDecoderImage(byte[] gif) {
        if (gifDecoder!=null) {
            gifDecoder.free();
            gifDecoder=null;
        }
        gifDecoder=new GifDecoder(gif, this);
        gifDecoder.start();
    }

    /**
     * 设置图片，开始解�?
     *
     * @param is 要设置的图片
     */
    private void setGifDecoderImage(InputStream is) {
        Log.d(TAG, "setGifDecoderImage.");
        if (gifDecoder!=null) {
            gifDecoder.free();
            gifDecoder=null;
        }
        gifDecoder=new GifDecoder(is, this);
        gifDecoder.start();
    }

    /**
     * 以字节数据形式设置gif图片
     *
     * @param gif 图片
     */
    public void setGifImage(byte[] gif) {
        setGifDecoderImage(gif);
    }

    /**
     * 以字节流形式设置gif图片
     *
     * @param is 图片
     */
    public void setGifImage(InputStream is) {
        setGifDecoderImage(is);
    }

    /**
     * 以资源形式设置gif图片
     *
     * @param resId gif图片的资源ID
     */
    public void setGifImage(int resId) {
        Log.d(TAG, "setGifImage.");
        Resources r=this.getResources();
        InputStream is=r.openRawResource(resId);
        setGifDecoderImage(is);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //Log.d(TAG, "onDraw.currentImage:"+currentImage);
        if (gifFrames==null||frameLength<1) {
            Log.d(TAG, "gifFrames:"+frameLength);
            return;
        }

        //Log.d(TAG, "onDraw:ci:"+currentImage);

        if (currentImage==null) {
            currentImage=gifFrames.get(currImageIdx).image;
        }

        if (currentImage==null) {
            return;
        }
        int saveCount=canvas.getSaveCount();
        canvas.save();
        canvas.translate(getPaddingLeft(), getPaddingTop());
        if (showWidth==-1) {
            canvas.drawBitmap(currentImage, 0, 0, null);
        } else {
            canvas.drawBitmap(currentImage, null, rect, null);
        }
        canvas.restoreToCount(saveCount);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d(TAG, "onMeasure:"+widthMeasureSpec+" height:"+heightMeasureSpec);
        int pleft=getPaddingLeft();
        int pright=getPaddingRight();
        int ptop=getPaddingTop();
        int pbottom=getPaddingBottom();

        int widthSize;
        int heightSize;

        int w;
        int h;

        Log.e(TAG,"gifDecoder = "+gifDecoder);
        if (gifDecoder==null) {
            w=1;
            h=1;
        } else {
        	
            w=gifDecoder.width;
            h=gifDecoder.height;
            Log.e(TAG,"("+w+", "+h+")");
        }

        w+=pleft+pright;
        h+=ptop+pbottom;

        w=Math.max(w, getSuggestedMinimumWidth());
        h=Math.max(h, getSuggestedMinimumHeight());

        widthSize=resolveSize(w, widthMeasureSpec);
        heightSize=resolveSize(h, heightMeasureSpec);

        //Log.d(TAG, "widthSize:"+widthSize+" heightSize:"+heightSize+" w:"+w+" h:"+h);

        setMeasuredDimension(widthSize, heightSize);
    }

    /**
     * 只显示第�?��图片<br>
     * 调用本方法后，gif不会显示动画，只会显示gif的第�?���?
     */
    public void showCover() {
        Log.d(TAG, "showCover.");
        if (gifFrames==null||frameLength<1) {
            return;
        }

        pause=true;
        isRun=false;
        currentImage=gifFrames.get(currImageIdx).image;
        invalidate();
    }

    /**
     * 继续显示动画<br>
     * 本方法在调用showCover后，会让动画继续显示，如果没有调用showCover方法，则没有任何效果
     */
    public void showAnimation() {
        Log.d(TAG, "showAnimation.");
        if (pause) {
            pause=false;
        }

        if (!isRun) {
            isRun=true;
        }

        if (drawThread==null) {
            drawThread=new DrawThread();
        } else {
            drawThread.interrupt();
            drawThread=new DrawThread();
        }
        drawThread.start();
    }

    /**
     * 设置gif在解码过程中的显示方�?br>
     * <strong>本方法只能在setGifImage方法之前设置，否则设置无�?/strong>
     *
     * @param type 显示方式
     */
    public void setGifImageType(GifImageType type) {
        if (gifDecoder==null) {
            animationType=type;
        }
    }

    /**
     * 设置要显示的图片的大�?br>
     * 当设置了图片大小 之后，会按照设置的大小来显示gif（按设置后的大小来进行拉伸或压缩�?
     *
     * @param width  要显示的图片�?
     * @param height 要显示的图片�?
     */
    public void setShowDimension(int width, int height) {
        Log.d(TAG, "setShowDimension.width:"+width+" height:"+height);
        if (width>0&&height>0) {
            showWidth=width;
            showHeight=height;
            rect=new Rect();
            rect.left=0;
            rect.top=0;
            rect.right=width;
            rect.bottom=height;
            requestLayout();
            invalidate();
        }
    }

    @Override
    public void parseOk(boolean parseStatus, int frameIndex) {
        Log.d(TAG, "parseOk.frameIndex:"+frameIndex);
        decodeFinish(parseStatus, frameIndex);
    }

    private void decodeFinish(boolean parseStatus, int frameIndex) {
        if (!parseStatus) {
            Log.d(TAG, "");
            /*if (null!=imageLoadCallback) {
                imageLoadCallback.loadError();
            }*/
            return;
        }

        if (gifDecoder==null) {
            Log.d(TAG, "");
            /*if (null!=imageLoadCallback) {
                   imageLoadCallback.loadError();
               }*/
            return;
        }

        gifFrames=gifDecoder.getFrameArrayList();
        currImageIdx=0;
        frameLength=gifFrames.size();

        //if (rect==null) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                /*if (null!=imageLoadCallback) {
                    imageLoadCallback.loadFinish();
                }*/
                
                Bitmap bitmap=gifFrames.get(0).image;
                Log.d(TAG, ""+gifFrames.get(0).delay);
                setShowDimension(bitmap.getWidth(), bitmap.getHeight());
            }
        });
        //}
        gifDecoder.free();
        gifDecoder=null;

        System.gc();

        startAnimate();
    }

    /*@Override
    public void dispatchWindowFocusChanged(boolean hasFocus){
        Log.d(TAG, "dispatchWindowFocusChanged:"+hasFocus);
    }*/

    //这个方法不一定执�?如果没有�?��资源,会导致cpu与内存占用率很高.
    @Override
    public void dispatchWindowVisibilityChanged(int visibility) {
        Log.d(TAG, "dispatchWindowVisibilityChanged:"+visibility);
        if (visibility==GONE||visibility==INVISIBLE) {
            stopAnimate();
        }
    }

    public void startAnimate() {
        Log.d(TAG, "startAnimate.animationType:"+animationType);
        switch (animationType) {
            case ANIMATION:
                Log.d(TAG, "ANIMATION.");
                if (frameLength>1) {
                    if (drawThread==null) {
                        drawThread=new DrawThread();
                    } else {
                        drawThread.interrupt();
                        drawThread=new DrawThread();
                    }
                    drawThread.start();
                } else if (frameLength==1) {
                    reDraw();
                }
                break;

            case COVER:
                Log.d(TAG, "COVER.");

                GifFrame frame=gifFrames.get(currImageIdx++);
                if (currImageIdx>=frameLength) {
                    currImageIdx=0;//重新播放�?
                }

                currentImage=frame.image;
                reDraw();
                break;
        }
    }

    /**
     * 停止动画与一切解码相关的操作.
     */
    public void stopAnimate() {
        Log.d(TAG, "stopAnimate.");
        isRun=false;
        pause=true;
        if(gifDecoder!=null){
            try {
                gifDecoder.interrupt();
                gifDecoder = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (drawThread!=null) {
            try {
                drawThread.interrupt();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void reDraw() {
        Log.d(TAG, "reDraw.");
        if (mHandler!=null) {
            Message msg=mHandler.obtainMessage();
            mHandler.sendMessage(msg);
        }
    }

    private Handler mHandler=new Handler() {

        @Override
        public void handleMessage(Message msg) {
            invalidate();
        }
    };

    /**
     * 动画线程
     *
     * @author liao
     */
    private class DrawThread extends Thread {

        @Override
        public void run() {
            //Log.d(TAG, "DrawThread.run.");
            if (gifFrames==null||frameLength<1) {
                return;
            }

            while (isRun) {
                GifFrame frame=gifFrames.get(currImageIdx++);
                if (currImageIdx>=frameLength) {
                    currImageIdx=0;//重新播放�?
                    //break;
                }

                currentImage=frame.image;
                if (pause==false) {
                    long delay=frame.delay;
                    //Log.d(TAG, "run.currentImage:"+currentImage+" pause:"+pause+" isRun:"+isRun+" delay:"+delay);
                    Message msg=mHandler.obtainMessage();
                    mHandler.sendMessage(msg);
                    SystemClock.sleep(delay);
                } else {
                    SystemClock.sleep(10);
                    break;
                }
            }

            Log.d(TAG, "finish run.");
        }
    }

    //////----------------------
    ArrayList<GifFrame> gifFrames=new ArrayList<GifFrame>(); //存储�?当前帧不应该太多,如果�?��gif较大,如超�?m会是个问�?
    int currImageIdx=0;//当前显示的解析图片索�?
    int frameLength=0; //帧的长度

    //回调方法,通过它可以回调解码失败或成功后的�?��操作.
    /*IImageLoadCallback imageLoadCallback;

    public void setImageLoadCallback(IImageLoadCallback imageLoadCallback) {
        this.imageLoadCallback=imageLoadCallback;
    }*/
}
