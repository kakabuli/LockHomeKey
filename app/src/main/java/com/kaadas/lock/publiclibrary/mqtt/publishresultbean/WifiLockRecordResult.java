package com.kaadas.lock.publiclibrary.mqtt.publishresultbean;

import java.io.Serializable;

public class WifiLockRecordResult implements Serializable {


   /**
   {
"msgtype": "event",
"func": "wfevent",
"msgId": 103,
"devtype": "kdswflock",
"lockId": "W5145C2004180AA1330607",
"eventtype": "record",
"eventparams": {
"eventType": 1,
"eventSource": 4,
"eventCode": 1,
"userID": 255,
"appID": 0
 },
"wfId": "TSF1201610173",
"timestamp": "1588145640"
}
    */

    private String  msgtype;
    private String func;
    private int msgId;
    private String devtype;
    private String lockId;
    private String eventtype;
    private RecordParam eventparams;
    private String wfId;
    private String timestamp;

    public class RecordParam {
        private int eventType;
        private int eventSource;
        private int eventCode;
        private int userID;
        private int appID;

        public int getEventType() {
            return eventType;
        }

        public void setEventType(int eventType) {
            this.eventType = eventType;
        }

        public int getEventSource() {
            return eventSource;
        }

        public void setEventSource(int eventSource) {
            this.eventSource = eventSource;
        }

        public int getEventCode() {
            return eventCode;
        }

        public void setEventCode(int eventCode) {
            this.eventCode = eventCode;
        }

        public int getUserID() {
            return userID;
        }

        public void setUserID(int userID) {
            this.userID = userID;
        }

        public int getAppID() {
            return appID;
        }

        public void setAppID(int appID) {
            this.appID = appID;
        }

        @Override
        public String toString() {
            return "RecordParam{" +
                    "eventType=" + eventType +
                    ", eventSource=" + eventSource +
                    ", eventCode=" + eventCode +
                    ", userID=" + userID +
                    ", appID=" + appID +
                    '}';
        }
    }

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public String getFunc() {
        return func;
    }

    public void setFunc(String func) {
        this.func = func;
    }

    public int getMsgId() {
        return msgId;
    }

    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }

    public String getDevtype() {
        return devtype;
    }

    public void setDevtype(String devtype) {
        this.devtype = devtype;
    }

    public String getLockId() {
        return lockId;
    }

    public void setLockId(String lockId) {
        this.lockId = lockId;
    }

    public String getEventtype() {
        return eventtype;
    }

    public void setEventtype(String eventtype) {
        this.eventtype = eventtype;
    }

    public RecordParam getEventparams() {
        return eventparams;
    }

    public void setEventparams(RecordParam eventparams) {
        this.eventparams = eventparams;
    }

    public String getWfId() {
        return wfId;
    }

    public void setWfId(String wfId) {
        this.wfId = wfId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "WifiLockRecordResult{" +
                "msgtype='" + msgtype + '\'' +
                ", func='" + func + '\'' +
                ", msgId=" + msgId +
                ", devtype='" + devtype + '\'' +
                ", lockId='" + lockId + '\'' +
                ", eventtype='" + eventtype + '\'' +
                ", eventparams=" + eventparams +
                ", wfId='" + wfId + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
