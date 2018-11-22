package com.glong.reader.widget;

import android.graphics.Canvas;
import android.support.annotation.NonNull;

import com.glong.reader.TurnStatus;

import java.io.File;

/**
 * Created by Garrett on 2018/11/17.
 * contact me krouky@outlook.com
 */
interface IReaderManager {

    /**
     * 上一页
     *
     * @return 跳转结果
     */
    TurnStatus toPrevPage();

    /**
     * 下一页
     *
     * @return 跳转结果
     */
    TurnStatus toNextPage();

    /**
     * 上一章
     *
     * @return 跳转结果
     */
    TurnStatus toPrevChapter();

    /**
     * 下一章
     *
     * @return 跳转结果
     */
    TurnStatus toNextChapter();

    /**
     * 跳转到指定章节
     *
     * @param chapterIndex 指定章节的索引
     * @param charIndex    指定跳转到该章节的第charIndex个字符处
     * @return 跳转结果
     */
    TurnStatus toSpecifiedChapter(int chapterIndex, int charIndex);

    /**
     * 从缓存启动阅读器
     *
     * @param key          缓存键
     * @param chapterIndex 章节索引
     * @param charIndex    跳转到缓存的那个字符
     */
    void startFromCache(String key, int chapterIndex, int charIndex, @NonNull String chapterName);

    /**
     * 从缓存启动阅读器
     *
     * @param cacheDir     缓存路径
     * @param key          缓存键
     * @param chapterIndex 章节索引
     * @param charIndex    跳转到缓存的那个字符
     */
    void startFromCache(File cacheDir, String key, int chapterIndex, int charIndex, @NonNull String chapterName);

    void drawPage(Canvas canvas);
}
