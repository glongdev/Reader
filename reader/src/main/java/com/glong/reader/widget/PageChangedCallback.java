package com.glong.reader.widget;

import android.graphics.Canvas;

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
     * 画页的时候调用
     *
     * @param canvas {@link Canvas}
     */
    void drawPage(Canvas canvas);

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
