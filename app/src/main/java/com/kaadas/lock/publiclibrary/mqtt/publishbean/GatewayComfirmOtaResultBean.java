package com.kaadas.lock.publiclibrary.mqtt.publishbean;

import java.io.Serializable;

public class GatewayComfirmOtaResultBean implements Serializable {


    /**
     * msgId : 123654
     * msgtype : response
     * userId :
     * gwId :
     * deviceId :
     * func : otaApprovateResult
     * code : 200
     * msg : 成功
     * timestamp : 1556440964753
     * data : null
     */

    private int msgId;
    private String msgtype;
    private String userId;
    private String gwId;
    private String deviceId;
    private String func;
    private String code;
    private String msg;
    private String timestamp;
    private Object data;

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

    public String getFunc() {
        return func;
    }

    public void setFunc(String func) {
        this.func = func;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
