package com.example.huanyingxiangji1.gif;

import android.graphics.Bitmap;

/**
 * @version 1.00.00
 * @description:
 * @author: archko 11-12-2
 */
public class GifFrame {

    public GifFrame(Bitmap image, int delay) {
        this.image=image;
        this.delay=delay;
    }

    /**
     */
    public Bitmap image;
    /**
     */
    public int delay;
}
