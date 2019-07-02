package com.kaadas.lock.publiclibrary.mqtt.publishbean;

import java.io.Serializable;

public class DeviceShareUserBean implements Serializable {


    /**
     * msgId : 666
     * msgtype : request
     * func : shareUserList
     * gwId : GW05191910010
     * deviceId : ZGCCXXYY58267
     * adminUid : 5c4fe492dc93897aa7d8600b
     */

    private int msgId;
    private String msgtype;
    private String func;
    private String gwId;
    private String deviceId;
    private String adminUid;

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

    public String getAdminUid() {
        return adminUid;
    }

    public void setAdminUid(String adminUid) {
        this.adminUid = adminUid;
    }

    public DeviceShareUserBean(int msgId, String msgtype, String func, String gwId, String deviceId, String adminUid) {
        this.msgId = msgId;
        this.msgtype = msgtype;
        this.func = func;
        this.gwId = gwId;
        this.deviceId = deviceId;
        this.adminUid = adminUid;
    }

    public DeviceShareUserBean() {
    }
}
