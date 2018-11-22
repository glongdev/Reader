package com.glong.reader;

import com.google.gson.Gson;

/**
 * Created by Garrett on 2018/11/21.
 * contact me krouky@outlook.com
 */
public class TestGson {

    public static String json = "{\n" +
            "    \"code\":0,\n" +
            "    \"msg\":\"success\",\n" +
            "    \"data\":{\n" +
            "        \"id\":\"123\",\n" +
            "        \"name\":\"aldkf\",\n" +
            "        \"content\":\"dlajfljaflfjlajldfjlasglajldfjafl\"\n" +
            "    }\n" +
            "}";

    public static void main(String[] args) {
        Gson gson = new Gson();
        TestBean testBean = gson.fromJson(json, TestBean.class);
        System.out.println("testBean:" + testBean.toString());

        System.out.println(Method.GET.toString());
    }
}

enum Method {
    POST,
    GET
}
