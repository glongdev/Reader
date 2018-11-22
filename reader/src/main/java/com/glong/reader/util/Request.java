package com.glong.reader.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Garrett on 2018/11/22.
 * contact me krouky@outlook.com
 */
public class Request {

    private final String mBaseUrl;
    private final String mMethod;
    private final Map<String, Object> mUrlParams;
    private final Map<String, Object> mBody;
    private final Map<String, FilePair> mFiles;
    private final Map<String, String> mHeaders;

    private Request(Builder builder) {
        this.mBaseUrl = builder.mBaseUrl;
        this.mMethod = builder.mMethod;
        this.mUrlParams = builder.mUrlParams;
        this.mBody = builder.mBody;
        this.mFiles = builder.mFiles;
        this.mHeaders = builder.mHeaders;
    }

    String getBaseUrl() {
        return this.mBaseUrl;
    }

    String getMethod() {
        return mMethod;
    }

    Map<String, Object> getParam() {
        return mUrlParams;
    }

    Map<String, FilePair> getFilePair() {
        return mFiles;
    }

    Map<String, Object> getBody() {
        return mBody;
    }

    Map<String, String> getHeaders() {
        return mHeaders;
    }

    public static class Builder {
        String mBaseUrl;
        String mMethod;
        Map<String, Object> mUrlParams;
        Map<String, Object> mBody;
        Map<String, FilePair> mFiles;
        Map<String, String> mHeaders;

        public Builder() {
            mMethod = "GET";
        }

        public Builder baseUrl(String baseUrl) {
            if (baseUrl == null) throw new NullPointerException("baseUrl == null");
            this.mBaseUrl = baseUrl;
            return this;
        }

        public Builder addUrlParams(String key, Object value) {
            if (key == null) throw new NullPointerException("key == null");
            if (value == null) throw new NullPointerException("value == null");
            if (this.mUrlParams == null) {
                mUrlParams = new HashMap<>();
            }
            mUrlParams.put(key, value);
            return this;
        }

        public Builder addBody(String key, Object value) {
            if (key == null) throw new NullPointerException("key == null");
            if (value == null) throw new NullPointerException("value == null");
            if (mBody == null) {
                mBody = new HashMap<>();
            }
            mBody.put(key, value);
            return this;
        }

        public Builder addFile(String key, FilePair filePair) {
            if (key == null) throw new NullPointerException("key == null");
            if (filePair == null) throw new NullPointerException("filePair == null");
            if (mFiles == null) {
                mFiles = new HashMap<>();
            }
            mFiles.put(key, filePair);
            return this;
        }

        public Builder addHeader(String key, String value) {
            if (key == null) throw new NullPointerException("key == null");
            if (value == null) throw new NullPointerException("value == null");
            if (mHeaders == null)
                mHeaders = new HashMap<>();
            mHeaders.put(key, value);
            return this;
        }

        public Builder post() {
            mMethod = "POST";
            return this;
        }

        public Builder get() {
            mMethod = "GET";
            return this;
        }

        public Request build() {
            if (mBaseUrl == null) throw new NullPointerException("mBaseUrl == null");
            return new Request(this);
        }
    }
}
