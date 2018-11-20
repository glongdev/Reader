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

    /**
     * 开始下载当前所需章节时调用（方便弹出提示等等）
     * 当下载缓存时不会回调
     *
     * @param chapterIndex 章节索引
     */
    void onChapterDownloadStart(int chapterIndex);

    /**
     * 当前所需章节下载成功后回调
     * 仅下载缓存时不会回调
     *
     * @param chapterIndex 章节索引
     */
    void onChapterDownloadSuccess(int chapterIndex);

    /**
     * 当前所需章节下载成功后回调
     * 仅下载缓存时不会回调
     *
     * @param chapterIndex 章节索引
     */
    void onChapterDownloadError(int chapterIndex);
}
