package com.kaadas.lock.publiclibrary.mqtt.publishresultbean;

import java.io.Serializable;

public class SettingScreenTimeResult implements Serializable {


    /**
     * {
     * "msgtype": "request", "msgId":4, "userId": "5cad4509dc938989e2f542c8",//AP P 下发
     * "wfId": "WF123456789",
     * "func":"setCamera",
     * "params": {
     * "screemLightSwitch" : 1
     * "screemLightTime" : 1
     * },
     * "timestamp": "13433333333",
     * }
     * //
     */

    private String msgtype;
    private String userId;
    private int msgId;
    private String wfId;
    private String func;
    private int code;
    private ParamsBean params;
    private String timestamp;

    public SettingScreenTimeResult(String msgtype, int msgId, String userId, String wfId, String func, ParamsBean params, String timestamp, int code) {
        this.msgtype = msgtype;
        this.userId = userId;
        this.msgId = msgId;
        this.wfId = wfId;
        this.func = func;
        this.params = params;
        this.timestamp = timestamp;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getMsgId() {
        return msgId;
    }

    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }

    public String getWfId() {
        return wfId;
    }

    public void setWfId(String wfId) {
        this.wfId = wfId;
    }

    public String getFunc() {
        return func;
    }

    public void setFunc(String func) {
        this.func = func;
    }

    public ParamsBean getParams() {
        return params;
    }

    public void setParams(ParamsBean params) {
        this.params = params;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public static class ParamsBean {
        private int screemLightSwitch;
        private int screemLightTime;

        public ParamsBean(int screemLightSwitch, int screemLightTime) {
            this.screemLightSwitch = screemLightSwitch;
            this.screemLightTime = screemLightTime;
        }

        public int getScreemLightSwitch() {
            return screemLightSwitch;
        }

        public void setScreemLightSwitch(int screemLightSwitch) {
            this.screemLightSwitch = screemLightSwitch;
        }

        public int getScreemLightTime() {
            return screemLightTime;
        }

        public void setScreemLightTime(int screemLightTime) {
            this.screemLightTime = screemLightTime;
        }

        @Override
        public String toString() {
            return "ParamsBean{" +
                    "screemLightSwitch=" + screemLightSwitch +
                    ", screemLightTime=" + screemLightTime +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "SettingScreenBrightness{" +
                "msgtype='" + msgtype + '\'' +
                ", userId='" + userId + '\'' +
                ", msgId=" + msgId +
                ", wfId='" + wfId + '\'' +
                ", func='" + func + '\'' +
                ", params=" + params +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
