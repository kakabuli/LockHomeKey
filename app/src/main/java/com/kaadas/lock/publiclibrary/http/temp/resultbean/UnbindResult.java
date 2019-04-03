package com.kaadas.lock.publiclibrary.http.temp.resultbean;

/**
 * Create By lxj  on 2019/1/10
 * Describe
 */
public class UnbindResult {
    /**
     * code : 202
     * msg : 已绑定
     * data : null
     */

    private String code;
    private String msg;
    private Object data;

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

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
