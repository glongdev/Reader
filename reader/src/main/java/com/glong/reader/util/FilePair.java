package com.glong.reader.util;

/**
 * Created by Garrett on 2018/11/22.
 * contact me krouky@outlook.com
 */
class FilePair {
    String mFileName;
    byte[] mBinaryData;

    public FilePair(String fileName, byte[] data) {
        this.mFileName = fileName;
        this.mBinaryData = data;
    }
}
