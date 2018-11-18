package com.glong.reader.effect;

import android.graphics.Canvas;
import android.view.MotionEvent;
import android.widget.Scroller;

/**
 * Created by Garrett on 2018/11/18.
 * contact me krouky@outlook.com
 */
public abstract class Effect {

    protected int mEffectWidth;
    protected int mEffectHeight;

    protected Scroller mScroller;

    

    public abstract boolean onTouchEvent(MotionEvent event);

    public abstract void onDraw(Canvas canvas);

    public void computeScroll() {
    }
}
