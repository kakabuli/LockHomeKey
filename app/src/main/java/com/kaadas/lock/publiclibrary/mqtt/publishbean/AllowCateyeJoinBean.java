package com.kaadas.lock.publiclibrary.mqtt.publishbean;

public class AllowCateyeJoinBean {


    /**
     * msgtype : request
     * userId :
     * msgId : 1
     * gwId :
     * func : allowCateyeJoin
     * sn : SN
     * mac : MAC
     * allowTime : 120
     * returnCode : 0
     * timestamp : 13433333333
     */

    private String msgtype;
    private String userId;
    private int msgId;
    private String gwId;
    private String func;
    private String sn;
    private String mac;
    private int allowTime;
    private int returnCode;
    private String timestamp;


    public AllowCateyeJoinBean(String msgtype, String userId, int msgId, String gwId, String func, String sn, String mac, int allowTime, int returnCode, String timestamp) {
        this.msgtype = msgtype;
        this.userId = userId;
        this.msgId = msgId;
        this.gwId = gwId;
        this.func = func;
        this.sn = sn;
        this.mac = mac;
        this.allowTime = allowTime;
        this.returnCode = returnCode;
        this.timestamp = timestamp;
    }


    public AllowCateyeJoinBean() {
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

    public int getMsgId() {
        return msgId;
    }

    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }

    public String getGwId() {
        return gwId;
    }

    public void setGwId(String gwId) {
        this.gwId = gwId;
    }

    public String getFunc() {
        return func;
    }

    public void setFunc(String func) {
        this.func = func;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public int getAllowTime() {
        return allowTime;
    }

    public void setAllowTime(int allowTime) {
        this.allowTime = allowTime;
    }

    public int getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(int returnCode) {
        this.returnCode = returnCode;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
