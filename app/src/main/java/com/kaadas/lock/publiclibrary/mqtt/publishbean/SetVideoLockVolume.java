package com.kaadas.lock.publiclibrary.mqtt.publishbean;

import java.io.Serializable;

public class SetVideoLockVolume implements Serializable {


    /**
     * {
     * 	"msgtype": "request",
     * 	"msgId": 4,
     * 	"userId": "5cad4509dc938989e2f542c8", //APP 下发
     * 	  "wfId": "WF123456789",
     * 		"func": "setLock",
     * 		  "params": {
     * 			"volume":0/1 //0 语音模式 1 静 音模式
     *                                        },
     *
     * 					 "timestamp": "13433333333",
     * }
     */

    private String msgtype;
    private String userId;
    private int msgId;
    private String wfId;
    private String func;
    private ParamsBean params;
    private String timestamp;


    public String getWfId() {
        return wfId;
    }

    public void setWfId(String wfId) {
        this.wfId = wfId;
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
        /**
         * "volume":0/1
         */

        private int volume;

        public int getVolume() {
            return volume;
        }

        public void setVolume(int volume) {
            this.volume = volume;
        }

        @Override
        public String toString() {
            return "ParamsBean{" +
                    "volume=" + volume +
                    '}';
        }
    }

    public SetVideoLockVolume(String msgtype, String userId, int msgId, String wfId, String func, ParamsBean params, String timestamp,int code) {
        this.msgtype = msgtype;
        this.userId = userId;
        this.msgId = msgId;
        this.wfId = wfId;
        this.func = func;
        this.params = params;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "SetVideoLockVolume{" +
                "msgtype='" + msgtype + '\'' +
                ", userId='" + userId + '\'' +
                ", msgId=" + msgId +
                ", wfId='" + wfId + '\'' +
                ", func='" + func + '\'' +
                ", params=" + params.toString() +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
