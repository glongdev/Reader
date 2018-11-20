package com.glong.reader.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;

/**
 * Created by Garrett on 2018/11/20.
 * contact me krouky@outlook.com
 */
public class EffectOfReal extends Effect {

    public EffectOfReal(Context context) {
        super(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    @Override
    public void onDraw(Canvas canvas) {

    }
}
