package com.glong.reader.widget;

/**
 * Created by Garrett on 2018/11/24.
 * contact me krouky@outlook.com
 */
public interface PageDrawingCallback {
    /**
     * 刷新ReaderView时调用
     */
    void invalidate();

    /**
     * 画当前页的时候调用 {@link ReaderView#mCurrPageCanvas}
     */
    void drawCurrPage();

    /**
     * 画下一页的时候调用 {@link ReaderView#mNextPageCanvas}
     */
    void drawNextPage();
}
