package com.kaadas.lock.publiclibrary.mqtt.publishresultbean;

import java.io.Serializable;

public class DoorbellingResult implements Serializable {


    /**
     * 	"msgtype": "event",
     * 	"func": "wfevent",
     * 	"msgId": 6,
     * 	"devtype": "xmkdswflock",
     * 	"eventtype": "alarm",
     * 	"wfId": "10V0204210002",
     * 	"timestamp": "1602661668",
     * 	"eventparams": {
     * 		"alarmCode": 96,
     * 		"clusterID": 257
     *        },
     * 	"thumbUrl": "http://test.juziwulian.com:8050/kx/api/upload/10V0204210002aec11a6719bc49ee991ca8502c72bc60.jpg",
     * 	"eventId": "10V0204210002aec11a6719bc49ee991ca8502c72bc60"
     */

    private String  msgtype;
    private String func;
    private int msgId;
    private String devtype;
    private String eventtype;
    private String wfId;
    private String timestamp;
    private String eventId;
    private String thumbUrl;
    private DoorbellingBean eventparams;


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

    public String getEventtype() {
        return eventtype;
    }

    public void setEventtype(String eventtype) {
        this.eventtype = eventtype;
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

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public DoorbellingBean getEventparams() {
        return eventparams;
    }

    public void setEventparams(DoorbellingBean eventparams) {
        this.eventparams = eventparams;
    }

    public class DoorbellingBean{
        private int alarmCode;
        private int clusterID;

        public int getAlarmCode() {
            return alarmCode;
        }

        public void setAlarmCode(int alarmCode) {
            this.alarmCode = alarmCode;
        }

        public int getClusterID() {
            return clusterID;
        }

        public void setClusterID(int clusterID) {
            this.clusterID = clusterID;
        }
    }
}
