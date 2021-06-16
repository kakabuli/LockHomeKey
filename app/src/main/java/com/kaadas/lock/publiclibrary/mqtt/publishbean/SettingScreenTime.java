package com.kaadas.lock.publiclibrary.mqtt.publishbean;

import java.io.Serializable;

public class SettingScreenTime implements Serializable {


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
    private ParamsBean params;
    private String timestamp;

    public SettingScreenTime(String msgtype, int msgId, String userId, String wfId, String func, ParamsBean params, String timestamp, int code) {
        this.msgtype = msgtype;
        this.userId = userId;
        this.msgId = msgId;
        this.wfId = wfId;
        this.func = func;
        this.params = params;
        this.timestamp = timestamp;
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
        private int screenLightSwitch;
        private int screenLightTime;

        public ParamsBean(int screemLightSwitch, int screemLightTime) {
            this.screenLightSwitch = screemLightSwitch;
            this.screenLightTime = screemLightTime;
        }

        public int getScreenLightSwitch() {
            return screenLightSwitch;
        }

        public void setScreenLightSwitch(int screenLightSwitch) {
            this.screenLightSwitch = screenLightSwitch;
        }

        public int getScreenLightTime() {
            return screenLightTime;
        }

        public void setScreenLightTime(int screenLightTime) {
            this.screenLightTime = screenLightTime;
        }

        @Override
        public String toString() {
            return "ParamsBean{" +
                    "screemLightSwitch=" + screenLightSwitch +
                    ", screemLightTime=" + screenLightTime +
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
