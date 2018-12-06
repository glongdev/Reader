package com.glong.sample.api;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Garrett on 2018/12/6.
 * contact me krouky@outlook.com
 */
public class Api {

    public static final String KEY = "d96c770925b95fb3d0404a4df89c3aa5";
    private static Api sApi;
    private Retrofit retrofit;

    private Api() {
        initRetrofit();
    }

    public static Api getInstance() {
        if (sApi == null) {
            synchronized (Api.class) {
                if (sApi == null) {
                    sApi = new Api();
                }
            }
        }
        return sApi;
    }

    private void initRetrofit() {
        retrofit = new Retrofit.Builder().baseUrl("http://apis.juhe.cn")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public <T> T getService(Class<T> clazz) {
        return retrofit.create(clazz);
    }
}
