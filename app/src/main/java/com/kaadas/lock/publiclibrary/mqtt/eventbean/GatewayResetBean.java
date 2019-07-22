package com.kaadas.lock.publiclibrary.mqtt.eventbean;

import java.io.Serializable;

public class GatewayResetBean implements Serializable {


    /**
     * userId : EMPTY
     * msgId : 22
     * msgtype : event
     * deviceId : GW01182510033
     * func : gatewayReset
     * returnCode : 0
     * gwId : GW01182510033
     * timestamp : 1563760769285
     */

    private String userId;
    private int msgId;
    private String msgtype;
    private String deviceId;
    private String func;
    private String returnCode;
    private String gwId;
    private String timestamp;

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

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
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

    public String getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }

    public String getGwId() {
        return gwId;
    }

    public void setGwId(String gwId) {
        this.gwId = gwId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }


}
