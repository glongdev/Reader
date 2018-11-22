package com.glong.reader.config;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Size;

/**
 * Created by Garrett on 2018/11/17.
 * contact me krouky@outlook.com
 */
public class ReaderConfig {

    /**
     * 正文文字大小
     */
    private int mTextSize;

    /**
     * 正文的边界
     */
    private int[] mPadding;

    /**
     * 电池宽度和高度
     */
    private int[] mBatteryWidthAndHeight;

    /**
     * 正文行间距
     */
    private int mLineSpace;

    /**
     * 阅读界面所有颜色相关
     */
    private ColorsConfig mColorsConfig;

    private ReaderConfig() {
    }

    public int getTextSize() {
        return mTextSize;
    }

    public void setTextSize(int textSize) {
        mTextSize = textSize;
    }

    public int[] getPadding() {
        return mPadding;
    }

    public void setPadding(int[] padding) {
        mPadding = padding;
    }

    public int[] getBatteryWidthAndHeight() {
        return mBatteryWidthAndHeight;
    }

    public void setBatteryWidthAndHeight(int[] batteryWidthAndHeight) {
        mBatteryWidthAndHeight = batteryWidthAndHeight;
    }

    public int getLineSpace() {
        return mLineSpace;
    }

    public void setLineSpace(int lineSpace) {
        mLineSpace = lineSpace;
    }

    public ColorsConfig getColorsConfig() {
        return mColorsConfig;
    }

    public void setColorsConfig(ColorsConfig colorsConfig) {
        mColorsConfig = colorsConfig;
    }

    public static class Builder {

        private int mTextSize = 60;

        private int[] mPadding = {40, 70, 40, 60};

        private int mLineSpace = 10;

        private int[] mBatteryWidthAndHeight = new int[]{60, 30};

        private ColorsConfig mColorsConfig = new ColorsConfig(Color.BLACK, Color.BLACK);

        public Builder setTextSize(@Size int textSize) {
            this.mTextSize = textSize;
            return this;
        }

        public Builder setPadding(int[] padding) {
            this.mPadding = padding;
            return this;
        }

        public Builder setLineSpace(@Size int lineSpace) {
            this.mLineSpace = lineSpace;
            return this;
        }

        public Builder setColorsConfig(ColorsConfig colorsConfig) {
            this.mColorsConfig = colorsConfig;
            return this;
        }

        public Builder setBatteryWidthAndHeight(@NonNull int[] widthAndHeight) {
            if (widthAndHeight.length < 2) {
                throw new IllegalArgumentException("int[] widthAndHeight length must == 2");
            }
            this.mBatteryWidthAndHeight = widthAndHeight;
            return this;
        }

        void apply(ReaderConfig readerConfig) {
            readerConfig.mTextSize = this.mTextSize;
            readerConfig.mPadding = this.mPadding;
            readerConfig.mLineSpace = this.mLineSpace;
            readerConfig.mColorsConfig = this.mColorsConfig;
            readerConfig.mBatteryWidthAndHeight = this.mBatteryWidthAndHeight;
        }

        public ReaderConfig build() {
            ReaderConfig readerConfig = new ReaderConfig();
            apply(readerConfig);
            return readerConfig;
        }

    }
}
