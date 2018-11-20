package com.glong.reader.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.widget.Scroller;

public abstract class Effect {

    protected int mEffectWidth;
    protected int mEffectHeight;

    protected Bitmap mCurrPageBitmap;
    protected Bitmap mNextPageBitmap;

    protected PageChangedCallback mPageChangedCallback;

    protected Scroller mScroller;
    protected VelocityTracker mVelocityTracker = VelocityTracker.obtain();

    protected int mLongClickTime;
    protected int mTouchSlop;

    public Effect(Context context) {
        initViewConfig(ViewConfiguration.get(context));
        mScroller = new Scroller(context);
    }

    private void initViewConfig(ViewConfiguration configuration) {
        mLongClickTime = configuration.getLongPressTimeout();
        mTouchSlop = configuration.getScaledPagingTouchSlop();
    }

    void config(int effectWidth, int effectHeight, Bitmap currPageBitmap, Bitmap nextPageBitmap) {
        this.mEffectWidth = effectWidth;
        this.mEffectHeight = effectHeight;
        this.mCurrPageBitmap = currPageBitmap;
        this.mNextPageBitmap = nextPageBitmap;
    }

    public PageChangedCallback getPageChangedCallback() {
        return mPageChangedCallback;
    }

    public void setPageChangedCallback(PageChangedCallback pageChangedCallback) {
        mPageChangedCallback = pageChangedCallback;
    }

    /**
     * 承接View的onTouchEvent(MotionEvent event)
     */
    public abstract boolean onTouchEvent(MotionEvent event);

    /**
     * 承接View的onDraw(Canvas canvas)
     */
    public abstract void onDraw(Canvas canvas);

    /**
     * 承接View的computeScroll()
     */
    public void computeScroll() {
    }

    public void abortAnimation() {
        if (!mScroller.isFinished()) {
            mScroller.abortAnimation();
        }
    }
}
