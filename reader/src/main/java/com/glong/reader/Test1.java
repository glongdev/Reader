package com.glong.reader;

import java.lang.reflect.ParameterizedType;

/**
 * Created by Garrett on 2018/11/18.
 * contact me krouky@outlook.com
 */
public class Test1 {
    public static void main(String[] args) {
        Adapter adapter = new Adapter();
        System.out.println(adapter.getRealTypeK());
//        System.out.println(adapter.getRealTypeT());
    }
}

class Adapter extends ParentAdapter<String> {
    public static void main(String[] args) {
        Adapter adapter = new Adapter();
        System.out.println(adapter.getRealTypeK());
//        System.out.println(adapter.getRealTypeT());
    }
}

class ParentAdapter<K> {
    Class<K> classK;
//    Class<T> classT;

    public Class getRealTypeK() {
        // 获取当前new的对象的泛型的父类类型
        ParameterizedType pt = (ParameterizedType) this.getClass().getGenericSuperclass();
        // 获取第一个类型参数的真实类型
        this.classK = (Class<K>) pt.getActualTypeArguments()[0];
        return classK;
    }

//    public Class getRealTypeT() {
//        // 获取当前new的对象的泛型的父类类型
//        ParameterizedType pt = (ParameterizedType) this.getClass().getGenericSuperclass();
//        // 获取第一个类型参数的真实类型
//        this.classT = (Class<T>) pt.getActualTypeArguments()[1];
//        return classT;
//    }
}
