package com.kaadas.lock.publiclibrary.mqtt.publishbean;

public class GetAlarmListBean {
    /**
     * msgId : 123654
     * msgtype : request
     * func : getAlarmList
     * userId : 5c4fe492dc93897aa7d8600b
     * gwId : GW05191910010
     * deviceId : ZG05191910001
     * pageNum : 1
     */

    private int msgId;
    private String msgtype;
    private String func;
    private String userId;
    private String gwId;
    private String deviceId;
    private int pageNum;

    public GetAlarmListBean(int msgId, String msgtype, String func, String userId, String gwId, String deviceId, int pageNum) {
        this.msgId = msgId;
        this.msgtype = msgtype;
        this.func = func;
        this.userId = userId;
        this.gwId = gwId;
        this.deviceId = deviceId;
        this.pageNum = pageNum;
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

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }
}
