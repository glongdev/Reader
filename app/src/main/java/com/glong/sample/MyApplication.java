package com.glong.sample;

import android.app.Application;

/**
 * Created by Garrett on 2018/12/6.
 * contact me krouky@outlook.com
 */
public class MyApplication extends Application {

    private static Application sApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        sApplication = this;
    }

    public static Application getApplication() {
        return sApplication;
    }
}
