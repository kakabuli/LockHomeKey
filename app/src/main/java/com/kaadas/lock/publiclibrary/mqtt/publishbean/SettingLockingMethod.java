package com.kaadas.lock.publiclibrary.mqtt.publishbean;

import java.io.Serializable;

public class SettingLockingMethod implements Serializable {

    /**
     * msgtype : request
     * msgId : 4
     * userId : 5cad4509dc938989e2f542c8
     * wfId : WF123456789
     * func : setLockingMethod
     * params : {"lockingMethod":1}
     * timestamp : 13433333333
     */

    private String msgtype;
    private int msgId;
    private String userId;
    private String wfId;
    private String func;
    private ParamsBean params;
    private String timestamp;

    public SettingLockingMethod(String msgtype, int msgId, String userId, String wfId, String func, ParamsBean params, String timestamp) {
        this.msgtype = msgtype;
        this.msgId = msgId;
        this.userId = userId;
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

    public int getMsgId() {
        return msgId;
    }

    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
        /**
         * lockingMethod : 1
         */

        private int lockingMethod;

        public int getLockingMethod() {
            return lockingMethod;
        }

        public void setLockingMethod(int lockingMethod) {
            this.lockingMethod = lockingMethod;
        }

        @Override
        public String toString() {
            return "ParamsBean{" +
                    "lockingMethod=" + lockingMethod +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "SettingLockingMethod{" +
                "msgtype='" + msgtype + '\'' +
                ", msgId=" + msgId +
                ", userId='" + userId + '\'' +
                ", wfId='" + wfId + '\'' +
                ", func='" + func + '\'' +
                ", params=" + params +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
