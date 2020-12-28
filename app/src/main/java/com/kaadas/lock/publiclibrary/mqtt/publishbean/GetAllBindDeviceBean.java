package com.kaadas.lock.publiclibrary.mqtt.publishbean;

public class GetAllBindDeviceBean {
    /**
     * msgId : 123456
     * msgtype : request
     * func : getAllBindDevice
     * uid : 5902aca835736f21ae1e7a82
     * modelSearchType : 2  型号检索方式，1为研发型号检索（默认），2为PID检索。
     */

    private int msgId;
    private String msgtype;
    private String func;
    private String uid;
    private int modelSearchType;

    public GetAllBindDeviceBean(int msgId, String msgtype, String func, String uid,int modelSearchType) {
        this.msgId = msgId;
        this.msgtype = msgtype;
        this.func = func;
        this.uid = uid;
        this.modelSearchType = modelSearchType;
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

    public int getModelSearchType() {
        return modelSearchType;
    }

    public void setModelSearchType(int modelSearchType) {
        this.modelSearchType = modelSearchType;
    }
}
