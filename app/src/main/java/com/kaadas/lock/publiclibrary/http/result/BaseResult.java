package com.kaadas.lock.publiclibrary.http.result;

import java.io.Serializable;

/**
 * Create By lxj  on 2019/3/3
 * Describe
 */
public class BaseResult implements Serializable {
    protected String code;
    protected String msg;
    private LoginResult.DataBean data;


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }


    public boolean isSuccess(){
        if ("200".equals(getCode()) || "201".equals(getCode()) || "202".equals(getCode())){
            return true;
        }

        return false;
    }

    @Override
    public String toString() {
        return "BaseResult{" +
                "code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
