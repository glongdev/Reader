package com.glong.reader;

/**
 * Created by Garrett on 2018/11/17.
 * contact me krouky@outlook.com
 */
public enum TurnStatus {

    /**
     * 未加载
     */
    IDLE,

    /**
     * 加载成功
     */
    LOAD_SUCCESS,

    /**
     * 加载失败
     */
    LOAD_FAILURE,

    /**
     * 下载中
     */
    DOWNLOADING,

    /**
     * 没有下一章
     */
    NO_NEXT_CHAPTER,

    /**
     * 没有上一章
     */
    NO_PREV_CHAPTER

//    /**
//     * 等待中，代表上次的下载还未完成
//     * 这个时候不处理翻页请求
//     */
//    LOADING,

//    /**
//     * 没有下一页
//     */
//    NO_NEXT_PAGE,
//
//    /**
//     * 没有上一页
//     */
//    NO_PREV_PAGE,

}
