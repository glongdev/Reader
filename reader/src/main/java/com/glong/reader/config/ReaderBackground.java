package com.glong.reader.config;

import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;

/**
 * Created by Garrett on 2018/11/18.
 * contact me krouky@outlook.com
 */
public class ReaderBackground {

    /**
     * 阅读器背景
     */
    private Drawable mBackground;

    /**
     * 阅读器文字颜色
     */
    private int mTextColor;

    public ReaderBackground(@NonNull Drawable background, @ColorInt int textColor) {
        this.mBackground = background;
        this.mTextColor = textColor;
    }

    public Drawable getBackground() {
        return mBackground;
    }

    public void setBackground(Drawable background) {
        mBackground = background;
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int textColor) {
        mTextColor = textColor;
    }
}
