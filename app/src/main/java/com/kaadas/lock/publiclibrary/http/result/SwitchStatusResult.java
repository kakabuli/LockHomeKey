package com.kaadas.lock.publiclibrary.http.result;

import java.io.Serializable;

/**
 * Create By denganzhi  on 2019/5/15
 * Describe
 */

public class SwitchStatusResult  {


    /**
     * code : 200
     * msg : 成功
     * data : {"openlockPushSwitch":true}
     */

    private String code;
    private String msg;
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

    public static class DataBean {
        /**
         * openlockPushSwitch : true
         */

        private boolean openlockPushSwitch;

        public boolean isOpenlockPushSwitch() {
            return openlockPushSwitch;
        }

        public void setOpenlockPushSwitch(boolean openlockPushSwitch) {
            this.openlockPushSwitch = openlockPushSwitch;
        }

        @Override
        public String toString() {
            return "WifiLockShareUser{" +
                    "openlockPushSwitch=" + openlockPushSwitch +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "SwitchStatusResult{" +
                "code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
