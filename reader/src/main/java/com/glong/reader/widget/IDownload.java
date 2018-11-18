package com.glong.reader.widget;

/**
 * Created by Garrett on 2018/11/17.
 * contact me krouky@outlook.com
 */
interface IDownload<K, T> {
    /**
     * 同步下载
     *
     * @param k 下载参数
     * @return 下载内容
     */
    T downLoad(K k);
}
