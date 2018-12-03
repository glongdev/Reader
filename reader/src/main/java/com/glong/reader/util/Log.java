package com.glong.reader.util;

/**
 * Created by Garrett on 2018/11/20.
 * contact me krouky@outlook.com
 */
public class Log {

    public static final boolean DEBUG = false;

    public static int v(String tag, String msg) {
        if (DEBUG) {
            return android.util.Log.v(tag, msg);
        } else {
            return -1;
        }
    }

    public static int d(String tag, String msg) {
        if (DEBUG) {
            return android.util.Log.d(tag, msg);
        } else {
            return -1;
        }
    }

    public static int i(String tag, String msg) {
        if (DEBUG) {
            return android.util.Log.i(tag, msg);
        } else {
            return -1;
        }
    }

    public static int w(String tag, String msg) {
        if (DEBUG) {
            return android.util.Log.w(tag, msg);
        } else {
            return -1;
        }
    }

    public static int e(String tag, String msg) {
        if (DEBUG) {
            return android.util.Log.e(tag, msg);
        } else {
            return -1;
        }
    }


}
