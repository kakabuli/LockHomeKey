package com.kaadas.lock.publiclibrary.mqtt.publishbean;

public class GetAllBindDeviceBean {

    /**
     * msgId : 123456
     * msgtype : request
     * func : getAllBindDevice
     * uid : 5902aca835736f21ae1e7a82
     */

    private int msgId;
    private String msgtype;
    private String func;
    private String uid;

    public GetAllBindDeviceBean(int msgId, String msgtype, String func, String uid) {
        this.msgId = msgId;
        this.msgtype = msgtype;
        this.func = func;
        this.uid = uid;
    }

    public GetAllBindDeviceBean() {

    }

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

    public String getFunc() {
        return func;
    }

    public void setFunc(String func) {
        this.func = func;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
