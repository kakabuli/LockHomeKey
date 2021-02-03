package com.kaadas.lock.publiclibrary.http.result;

import java.io.Serializable;

public class LoginErrorResult implements Serializable {
    /**
     * {
     *     "code": "101",
     *     "msg": "账号或密码错误",
     *     "nowTime": 1612144219,
     *     "data": {
     *         "restrictCount": 0, // 失败次数（小于5时是剩余登陆次数）
     *         "restrictTime": 1612144279 // 限制时间
     *     }
     * }
     */
    private String code;
    private String msg;
    private long nowTime;
    private Param data;

    public class Param{
        private int restrictCount;

        private int restrictTime;

        public int getRestrictCount() {
            return restrictCount;
        }

        public void setRestrictCount(int restrictCount) {
            this.restrictCount = restrictCount;
        }

        public int getRestrictTime() {
            return restrictTime;
        }

        public void setRestrictTime(int restrictTime) {
            this.restrictTime = restrictTime;
        }
    }


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

    public long getNowTime() {
        return nowTime;
    }

    public void setNowTime(long nowTime) {
        this.nowTime = nowTime;
    }

    public Param getData() {
        return data;
    }

    public void setData(Param data) {
        this.data = data;
    }

    public boolean isSuccess(){
        if ("200".equals(getCode() + "") || "201".equals(getCode() + "") || "202".equals(getCode() + "")){
            return true;
        }

        return false;
    }

    @Override
    public String toString() {
        return "LoginErrorResult{" +
                "code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                ", nowTime=" + nowTime +
                ", data=" + data +
                '}';
    }
}
