package com.glong.reader.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;

import com.glong.reader.TurnStatus;

/**
 * Created by Garrett on 2018/11/18.
 * contact me krouky@outlook.com
 */
public class EffectOfNo extends Effect {

    private float mDownX;

    public EffectOfNo(Context context) {
        super(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = x;
                return true;
            case MotionEvent.ACTION_UP:
                TurnStatus turnStatus;
                if (mDownX >= mEffectWidth / 2) {
                    turnStatus = mPageChangedCallback.toNextPage();
                } else {
                    turnStatus = mPageChangedCallback.toPrevPage();
                }
                if (turnStatus == TurnStatus.LOAD_SUCCESS) {
                    mPageChangedCallback.drawCurrPage();
                    mPageChangedCallback.invalidate();
                }
        }
        return false;
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawBitmap(mCurrPageBitmap, 0, 0, null);
    }
}
