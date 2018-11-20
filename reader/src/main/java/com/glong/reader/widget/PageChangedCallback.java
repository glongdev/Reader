package com.glong.reader.widget;

import com.glong.reader.TurnStatus;

/**
 * Created by Garrett on 2018/11/18.
 * contact me krouky@outlook.com
 */
public interface PageChangedCallback {

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

    /**
     * 获取上一页的时候调用
     *
     * @return 获取结果
     */
    TurnStatus toPrevPage();

    /**
     * 获取下一页的时候调用
     *
     * @return 获取结果
     */
    TurnStatus toNextPage();
}
