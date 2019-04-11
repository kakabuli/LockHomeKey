package com.kaadas.lock.publiclibrary.mqtt.bean;

public class GetPower {


    /**
     * deviceId : devuuid
     * func :  getPower
     * gwId :
     * msgId : 1
     * msgtype : request
     * params : {}
     * returnCode : 0
     * returnData : {}
     * timestamp : 13433333333
     * userId :
     */

    private String deviceId;
    private String func;
    private String gwId;
    private int msgId;
    private String msgtype;
    private ParamsBean params;
    private int returnCode;
    private ReturnDataBean returnData;
    private String timestamp;
    private String userId;

    public GetPower() {
    }

    public GetPower(String deviceId, String func, String gwId, int msgId, String msgtype, ParamsBean params,
                    int returnCode, ReturnDataBean returnData,  String userId) {
        this.deviceId = deviceId;
        this.func = func;
        this.gwId = gwId;
        this.msgId = msgId;
        this.msgtype = msgtype;
        this.params = params;
        this.returnCode = returnCode;
        this.returnData = returnData;
        this.userId = userId;
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

    public String getGwId() {
        return gwId;
    }

    public void setGwId(String gwId) {
        this.gwId = gwId;
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

    public ParamsBean getParams() {
        return params;
    }

    public void setParams(ParamsBean params) {
        this.params = params;
    }

    public int getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(int returnCode) {
        this.returnCode = returnCode;
    }

    public ReturnDataBean getReturnData() {
        return returnData;
    }

    public void setReturnData(ReturnDataBean returnData) {
        this.returnData = returnData;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public static class ParamsBean {
    }

    public static class ReturnDataBean {
    }
}
