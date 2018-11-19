package com.glong.reader.cache;

import com.google.gson.Gson;

import java.io.File;

/**
 * Created by Garrett on 2018/11/17.
 * contact me krouky@outlook.com
 */
public class DiskCache extends Cache {

    private Gson mGson;

    public DiskCache(File cacheDir) {
        super(cacheDir);
        mGson = new Gson();
    }

    @Override
    public void put(String key, String value) {
        ACache.get(getCacheDir()).put(key, value);
    }

    @Override
    public <T> void put(String key, T t) {
        put(key, mGson.toJson(t));
    }

    @Override
    public String get(String key) {
        return ACache.get(getCacheDir()).getAsString(key);
    }

    @Override
    public <T> T get(String key, Class<T> clazz) {
        String cache = ACache.get(getCacheDir()).getAsString(key);
        return mGson.fromJson(cache, clazz);
    }
}
