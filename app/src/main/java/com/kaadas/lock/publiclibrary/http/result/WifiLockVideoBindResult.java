package com.kaadas.lock.publiclibrary.http.result;

import java.io.Serializable;

public class WifiLockVideoBindResult extends BaseResult {
    /**
     * {
     *     "code": "444",
     *     "msg": "没有登录",
     *     "nowTime": 1600051661,
     *     "data": null
     * }
     */

    private long nowTime;
    private DataBean data;

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public long getNowTime() {
        return nowTime;
    }

    public void setNowTime(long nowTime) {
        this.nowTime = nowTime;
    }

    public static class DataBean{
        private String uname;

        public String getUname() {
            return uname;
        }

        public void setUname(String uname) {
            this.uname = uname;
        }
    }
}
