package com.kaadas.lock.publiclibrary.mqtt.publishresultbean;

import java.io.Serializable;

public class GetLockRecordTotalResult implements Serializable {


    /**
     * msgId : 123654
     * msgtype : response
     * userId :
     * gwId :
     * deviceId : ZGCCXXYY58266
     * func : countOpenLockRecord
     * code : 200
     * msg : 成功
     * timestamp : 1558589393816
     * data : {"count":1}
     */

    private int msgId;
    private String msgtype;
    private String userId;
    private String gwId;
    private String deviceId;
    private String func;
    private String code;
    private String msg;
    private String timestamp;
    private DataBean data;

    public int getMsgId() {
        return msgId;
    }

    public void setMsgId(int msgId) {
        this.msgId = msgId;
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

    public String getGwId() {
        return gwId;
    }

    public void setGwId(String gwId) {
        this.gwId = gwId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getFunc() {
        return func;
    }

    public void setFunc(String func) {
        this.func = func;
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * count : 1
         */

        private int count;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }
}
