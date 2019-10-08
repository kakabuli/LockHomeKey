package com.zhouyou.http.model;

/**
 * Created by yanliang
 * on 2018/8/27 11:04
 */

public class HttpResult {
    private int code;
    private String message;
    private String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
