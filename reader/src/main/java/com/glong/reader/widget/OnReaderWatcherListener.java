package com.glong.reader.widget;

/**
 * 当页码、章节发生变化时的回调接口
 * Created by Garrett on 2018/11/19.
 * contact me krouky@outlook.com
 */
public interface OnReaderWatcherListener {

    /**
     * 页码发生了变化
     *
     * @param pageIndex 第pageIndex页（从第0页开始）
     */
    void onPageChanged(int pageIndex);

    /**
     * 章节发生了变化
     *
     * @param chapterIndex 跳转到了第chapterIndex章
     * @param pageIndex    跳转到了这章的第pageIndex页（从第0页开始）
     */
    void onChapterChanged(int chapterIndex, int pageIndex);
}
