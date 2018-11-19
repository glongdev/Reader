package com.glong.reader;

import com.google.gson.internal.Primitives;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by Garrett on 2018/11/18.
 * contact me krouky@outlook.com
 */
public class Test<T> {

    private Class<T> clazz;

    // 使用反射技术得到T的真实类型
    public Class getRealType() {
        // 获取当前new的对象的泛型的父类类型
        ParameterizedType pt = (ParameterizedType) this.getClass().getGenericSuperclass();
        // 获取第一个类型参数的真实类型
        this.clazz = (Class<T>) pt.getActualTypeArguments()[0];
        return clazz;
    }

}

class ClassB extends Test<String> {

    public static void main(String[] args) {
        ClassB classB = new ClassB();
        Class realType = classB.getRealType();
        System.out.println(realType.getName());
    }
}

class Bean<T> extends Parent<T> {
}

class Parent<T> {
}