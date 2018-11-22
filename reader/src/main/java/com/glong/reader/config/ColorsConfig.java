package com.glong.reader.config;

import android.support.annotation.ColorInt;

/**
 * Created by Garrett on 2018/11/18.
 * contact me krouky@outlook.com
 */
public class ColorsConfig {

    /**
     * 阅读器文字颜色
     */
    private int mTextColor;

    /**
     * 画电池的画笔颜色
     */
    private int mBatteryColor;

    public ColorsConfig() {
    }

    public ColorsConfig( @ColorInt int textColor, @ColorInt int batteryColor) {
        this.mTextColor = textColor;
        this.mBatteryColor = batteryColor;
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int textColor) {
        mTextColor = textColor;
    }

    public int getBatteryColor() {
        return mBatteryColor;
    }

    public void setBatteryColor(int batteryColor) {
        mBatteryColor = batteryColor;
    }
}
