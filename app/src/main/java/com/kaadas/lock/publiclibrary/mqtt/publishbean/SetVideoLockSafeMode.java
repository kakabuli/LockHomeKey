package com.kaadas.lock.publiclibrary.mqtt.publishbean;

import java.io.Serializable;

public class SetVideoLockSafeMode implements Serializable {


    /**
     * {
     * 	"msgtype": "request",
     * 	"msgId": 4,
     * 	"userId": "5cad4509dc938989e2f542c8", //APP 下发
     * 	  "wfId": "WF123456789",
     * 		"func": "setLock",
     * 		  "params": {
     * 			""safeMode":0/1
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
    private int code;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

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
         * "safeMode":0/1
         */

        private int safeMode;

        public int getSafeMode() {
            return safeMode;
        }

        public void setSafeMode(int safeMode) {
            this.safeMode = safeMode;
        }
    }

    public SetVideoLockSafeMode(String msgtype, String userId, int msgId, String wfId, String func, ParamsBean params, String timestamp,int code) {
        this.msgtype = msgtype;
        this.userId = userId;
        this.msgId = msgId;
        this.wfId = wfId;
        this.func = func;
        this.params = params;
        this.timestamp = timestamp;
        this.code = code;
    }
}
