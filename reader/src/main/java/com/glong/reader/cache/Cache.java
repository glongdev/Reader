package com.glong.reader.cache;

import java.io.File;

/**
 * Created by Garrett on 2018/11/17.
 * contact me krouky@outlook.com
 */
public abstract class Cache {

    private File mCacheDir;

    /**
     * 缓存章节数(默认前面3章，后面3章)
     */
    private int mCacheAmount = 3;

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

    public abstract boolean remove(String key);

    public abstract boolean removeAll();

    /**
     * 指定key是否已经有缓存
     *
     * @param key 缓存key
     * @return 已经有缓存返回true，否则返回false
     */
    public abstract boolean isCached(String key);

    public File getCacheDir() {
        return mCacheDir;
    }

    public void setCacheDir(File cacheDir) {
        checkIsDirectory(cacheDir);
        this.mCacheDir = cacheDir;
    }

    public int getCacheAmount() {
        return mCacheAmount;
    }

    public void setCacheAmount(int cacheAmount) {
        mCacheAmount = cacheAmount;
    }
}
