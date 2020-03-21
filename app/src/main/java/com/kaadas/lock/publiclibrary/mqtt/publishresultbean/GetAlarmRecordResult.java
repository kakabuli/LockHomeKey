package com.kaadas.lock.publiclibrary.mqtt.publishresultbean;

import java.util.List;

public class GetAlarmRecordResult {

    /**
     * msgId : 123654
     * msgtype : response
     * userId : 5c4fe492dc93897aa7d8600b
     * gwId : GW05191910010
     * deviceId : ZG05191910001
     * func : getAlarmList
     * code : 200
     * msg : 成功
     * timestamp : 1557818080462
     * data : [{"_id":"5cda5d3ed98d1d2f0cd28837","devName":"ZG05191910001","warningTime":1541468973342,"warningType":4,"content":""}]
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
    private List<DataBean> data;

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

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * _id : 5cda5d3ed98d1d2f0cd28837
         * devName : ZG05191910001
         * warningTime : 1541468973342
         * warningType : 4
         * content :
         */

        private String _id;
        private String devName;
        private long warningTime;
        private int warningType;
        private String content;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getDevName() {
            return devName;
        }

        public void setDevName(String devName) {
            this.devName = devName;
        }

        public long getWarningTime() {
            return warningTime;
        }

        public void setWarningTime(long warningTime) {
            this.warningTime = warningTime;
        }

        public int getWarningType() {
            return warningType;
        }

        public void setWarningType(int warningType) {
            this.warningType = warningType;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
