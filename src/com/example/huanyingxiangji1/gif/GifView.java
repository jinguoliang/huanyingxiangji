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


public class GifView extends View implements GifAction {

    public static final String TAG="GifView";

    private GifDecoder gifDecoder=null;

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


    public enum GifImageType {

        WAIT_FINISH(0),

        SYNC_DECODER(1),

        COVER(2),

        ANIMATION(3);

        GifImageType(int i) {
            nativeInt=i;
        }

        final int nativeInt;
    }

    /**
     * This constructor is used jut in main thread
     * @param context
     */
    public GifView(Context context) {
        super(context);
        mHandler=new Handler(){
        	public void dispatchMessage(Message msg) {
        		invalidate();
        	};
        };
    }
    
    
    /**
     * If not created in main thread, we need git it a handler which in main thread.
     * beacause the handler need to be used to invalidate the view.
     * @param context
     * @param h
     */
    public GifView(Context context,Handler h) {
        super(context);
        mHandler=h;
    }

    public GifView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GifView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    public void setGifDecoderImage(byte[] gif) {
        if (gifDecoder!=null) {
            gifDecoder.free();
            gifDecoder=null;
        }
        gifDecoder=new GifDecoder(gif, this);
        gifDecoder.start();
    }


    private void setGifDecoderImage(InputStream is) {
        Log.d(TAG, "setGifDecoderImage.");
        if (gifDecoder!=null) {
            gifDecoder.free();
            gifDecoder=null;
        }
        gifDecoder=new GifDecoder(is, this);
        gifDecoder.start();
    }


    public void setGifImage(byte[] gif) {
        setGifDecoderImage(gif);
    }


    public void setGifImage(InputStream is) {
        setGifDecoderImage(is);
    }


    public void setGifImage(int resId) {
        Log.d(TAG, "setGifImage.");
        Resources r=this.getResources();
        InputStream is=r.openRawResource(resId);
        setGifDecoderImage(is);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(TAG, "onDraw.currentImage:"+currentImage);
        if (gifFrames==null||frameLength<1) {
            Log.d(TAG, "gifFrames:"+frameLength);
            return;
        }

        Log.d(TAG, "onDraw:ci:"+currentImage);

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
        Log.e(TAG, "onMeasure:"+widthMeasureSpec+" height:"+heightMeasureSpec);
        
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

        Log.d(TAG, "widthSize:"+widthSize+" heightSize:"+heightSize+" w:"+w+" h:"+h);
        heightSize=400;
        setMeasuredDimension(widthSize, heightSize);
    }

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


    public void setGifImageType(GifImageType type) {
        if (gifDecoder==null) {
            animationType=type;
        }
    }


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
                    currImageIdx=0;
                }

                currentImage=frame.image;
                reDraw();
                break;
        }
    }


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
            msg.obj=this;
            mHandler.sendMessage(msg);
        }
    }

    private Handler mHandler=null;
//    		new Handler() {
//
//        @Override
//        public void handleMessage(Message msg) {
//        	if (currentImage!=null) {
//				measure(currentImage.getWidth(), currentImage.getHeight());
//			}
//            invalidate();
//            Log.e(TAG,"invalidate redraw");
//        }
//    };


    private class DrawThread extends Thread {

        @Override
        public void run() {
            Log.d(TAG, "DrawThread.run.");
            if (gifFrames==null||frameLength<1) {
                return;
            }

            while (isRun) {
                GifFrame frame=gifFrames.get(currImageIdx++);
                if (currImageIdx>=frameLength) {
                    currImageIdx=0;
                    //break;
                }

                currentImage=frame.image;
                if (pause==false) {
                    long delay=frame.delay;
                    Log.d(TAG, "run.currentImage:"+currentImage+" pause:"+pause+" isRun:"+isRun+" delay:"+delay);
                    Message msg=mHandler.obtainMessage();
                    msg.obj=GifView.this;
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
    ArrayList<GifFrame> gifFrames=new ArrayList<GifFrame>();
    int currImageIdx=0;
    int frameLength=0;


//    public void setImageLoadCallback(IImageLoadCallback imageLoadCallback) {
//        this.imageLoadCallback=imageLoadCallback;
//    }
}
