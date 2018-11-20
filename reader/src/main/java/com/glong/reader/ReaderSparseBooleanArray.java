package com.glong.reader;

import android.util.SparseBooleanArray;

/**
 * Created by Garrett on 2018/11/20.
 * contact me krouky@outlook.com
 */
public class ReaderSparseBooleanArray {

    private SparseBooleanArray mSparseBooleanArray = new SparseBooleanArray();

    public boolean get(int key) {
        return mSparseBooleanArray.get(key);
    }

    public void delete(int key) {
        synchronized (ReaderSparseBooleanArray.this) {

            mSparseBooleanArray.delete(key);
        }
    }

    public void put(int key, boolean value) {
        synchronized (ReaderSparseBooleanArray.this) {
            mSparseBooleanArray.put(key, value);
        }
    }

    public synchronized void clear() {
        synchronized (ReaderSparseBooleanArray.this) {
            mSparseBooleanArray.clear();
        }
    }

    public int size() {
        synchronized (ReaderSparseBooleanArray.this) {
            return mSparseBooleanArray.size();
        }
    }

    public boolean contains(int key) {
        synchronized (ReaderSparseBooleanArray.this) {
            for (int i = 0; i < size(); i++) {
                int indexKey = mSparseBooleanArray.keyAt(i);
                if (indexKey == key) {
                    return true;
                }
            }
            return false;
        }
    }

}
