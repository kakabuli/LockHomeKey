package com.kaadas.lock.publiclibrary.mqtt.publishbean;

import java.io.Serializable;

public class UnBindGatewayBean implements Serializable {


    /**
     * msgId : 456123
     * msgtype : response
     * userId :
     * gwId :
     * deviceId :
     * func : unbindGateway
     * code : 200
     * msg : 成功
     * timestamp : 1556270576406
     * data : {}
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
    private DataBean data;
    private String devuuid;
    private String uid;

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

    public String getDevuuid() {
        return devuuid;
    }

    public void setDevuuid(String devuuid) {
        this.devuuid = devuuid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
    }


    public UnBindGatewayBean() {
    }

    public UnBindGatewayBean(int msgId, String msgtype, String userId, String gwId, String deviceId, String func, String code, String msg, String timestamp, DataBean data) {
        this.msgId = msgId;
        this.msgtype = msgtype;
        this.userId = userId;
        this.gwId = gwId;
        this.deviceId = deviceId;
        this.func = func;
        this.code = code;
        this.msg = msg;
        this.timestamp = timestamp;
        this.data = data;
    }
    //解绑网关
    public UnBindGatewayBean(int msgId, String uid, String func, String devuuid) {
        this.msgId = msgId;
        this.uid = uid;
        this.func = func;
        this.devuuid = devuuid;
    }

    //解绑测试网关
    public UnBindGatewayBean(int msgId, String uid, String gwId, String func, String devuuid) {
        this.msgId = msgId;
        this.uid = uid;
        this.gwId = gwId;
        this.func = func;
        this.devuuid = devuuid;
    }


}
