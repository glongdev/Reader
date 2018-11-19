package com.glong.reader.cache;

import java.io.File;

/**
 * Created by Garrett on 2018/11/17.
 * contact me krouky@outlook.com
 */
public abstract class Cache {

    private File mCacheDir;

    /**
     * 缓存章节数
     */
    private int mCacheNumber = 3;

    public Cache(File cacheDir) {
        checkIsDirectory(cacheDir);
        this.mCacheDir = cacheDir;
    }

    private void checkIsDirectory(File cacheDir) {
        if (!cacheDir.isDirectory()) {
            throw new IllegalArgumentException("cacheDir must be a directory!");
        }
    }

    public abstract void put(String key, String value);

    public abstract <T> void put(String key, T t);

    public abstract String get(String key);

    public abstract <T> T get(String key, Class<T> clazz);

    public File getCacheDir() {
        return mCacheDir;
    }

    public void setCacheDir(File cacheDir) {
        checkIsDirectory(cacheDir);
        this.mCacheDir = cacheDir;
    }

    public int getCacheNumber() {
        return mCacheNumber;
    }

    public void setCacheNumber(int cacheNumber) {
        mCacheNumber = cacheNumber;
    }
}
