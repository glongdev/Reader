package com.glong.sample.entry;

/**
 * Created by Garrett on 2018/12/6.
 * contact me krouky@outlook.com
 */
public class Result<T> {
    private int resultcode;
    private String reason;
    private T result;

    public int getResultcode() {
        return resultcode;
    }

    public void setResultcode(int resultcode) {
        this.resultcode = resultcode;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
