package com.kaadas.lock.publiclibrary.mqtt.publishbean.ClothesHangerMachineBean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ClothesHangerMachineOnlineBean implements Serializable {
    /*
    {
	"msgtype": "event",
	"func": "wfevent",
	"msgId": 4,
	"devtype": "KdsMxchipHanger",
	"eventtype": "hangerOnlineState",
	"onlineState": "1",
	"eventparams": {},
	"wfId": "KV51203710106",
	"timestamp": 1610326414
}
     */
    private String msgtype;
    private String func;
    private String msgId;
    private String devtype;
    private String eventtype;
    private int onlineState;
    private Param eventparams;
    private String wfId;
    private String timestamp;

    public int getOnlineState() {
        return onlineState;
    }

    public void setOnlineState(int onlineState) {
        this.onlineState = onlineState;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
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

    public Param getEventparams() {
        return eventparams;
    }

    public void setEventparams(Param eventparams) {
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

    public class Param{

    }


}
