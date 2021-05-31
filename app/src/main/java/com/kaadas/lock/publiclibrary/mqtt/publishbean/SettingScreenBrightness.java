package com.kaadas.lock.publiclibrary.mqtt.publishbean;

import java.io.Serializable;
import java.util.Arrays;

public class SettingScreenBrightness implements Serializable {


    /**
     * {
     * "msgtype": "request", "msgId":4, "userId": "5cad4509dc938989e2f542c8",//AP P 下发
     * "wfId": "WF123456789",
     * "func":"setCamera",
     * "params": {
     * "screemLightLevel" : 80
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

    public SettingScreenBrightness(String msgtype, int msgId, String userId, String wfId, String func, ParamsBean params, String timestamp, int code) {
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
        private int screemLightLevel;

        public ParamsBean(int screemLightLevel) {
            this.screemLightLevel = screemLightLevel;
        }

        public int getScreemLightLevel() {
            return screemLightLevel;
        }

        public void setScreemLightLevel(int screemLightLevel) {
            this.screemLightLevel = screemLightLevel;
        }

        @Override
        public String toString() {
            return "ParamsBean{" +
                    "screemLightLevel=" + screemLightLevel +
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
