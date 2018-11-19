package com.glong.reader.config;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.Size;

/**
 * Created by Garrett on 2018/11/17.
 * contact me krouky@outlook.com
 */
public class ReaderConfig {

    private int mTextSize;

    private int[] mPadding;

    private int mLineSpace;

    private ReaderBackground mReaderBackground;

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

    public int getLineSpace() {
        return mLineSpace;
    }

    public void setLineSpace(int lineSpace) {
        mLineSpace = lineSpace;
    }

    public ReaderBackground getReaderBackground() {
        return mReaderBackground;
    }

    public void setReaderBackground(ReaderBackground readerBackground) {
        mReaderBackground = readerBackground;
    }

    public static class Builder {

        private int mTextSize = 60;

        private int[] mPadding = {40, 80, 40, 80};

        private int mLineSpace = 50;

        private ReaderBackground mReaderBackground = new ReaderBackground(new ColorDrawable(Color.WHITE), Color.BLACK);

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

        public Builder setReaderBackground(ReaderBackground readerBackground) {
            this.mReaderBackground = readerBackground;
            return this;
        }

        void apply(ReaderConfig readerConfig) {
            readerConfig.mTextSize = this.mTextSize;
            readerConfig.mPadding = this.mPadding;
            readerConfig.mLineSpace = this.mLineSpace;
            readerConfig.mReaderBackground = this.mReaderBackground;
        }

        public ReaderConfig build() {
            ReaderConfig readerConfig = new ReaderConfig();
            apply(readerConfig);
            return readerConfig;
        }

    }
}
