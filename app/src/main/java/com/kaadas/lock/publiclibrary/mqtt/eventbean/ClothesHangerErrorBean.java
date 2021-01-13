package com.kaadas.lock.publiclibrary.mqtt.eventbean;

import java.io.Serializable;

public class ClothesHangerErrorBean implements Serializable {
    /**
    {
"msgtype":"event",
"func":"wfevent ",
"msgId":4,
"devtype":"KDS-MXCHIP-Hanger",
"eventtype":"errorNotify",
"errorCode": 1
"uid":"xxxx",
"wfId":"wfuuid",
"timestamp":"1541468973342"
}
     */

    private String msgtype;
    private String func;
    private int msgId;
    private String devtype;
    private String eventtype;
    private int errorCode;
    private String uid;
    private String wfId;
    private String timestamp;

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

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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
}
