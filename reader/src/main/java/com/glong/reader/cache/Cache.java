package com.glong.reader.cache;

import java.io.File;

/**
 * Created by Garrett on 2018/11/17.
 * contact me krouky@outlook.com
 */
public abstract class Cache {

    private File mCacheDir;

    public abstract void put(String key, String value);

    public abstract <T> void put(String key, T t);

    public abstract String get(String key);

    public abstract <T> T get(String key, Class<T> clazz);

    public File getmCacheDir() {
        return mCacheDir;
    }

    public void setmCacheDir(File mCacheDir) {
        this.mCacheDir = mCacheDir;
    }
}
