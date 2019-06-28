package com.kaadas.lock.publiclibrary.mqtt.publishbean;

import java.io.Serializable;

public class DeviceShareBean implements Serializable {


    /**
     * msgId : 666
     * func : shareDevice
     * type : 1
     * gwId : GW04191410002
     * deviceId :
     * adminUid : 5c4fe492dc93897aa7d8600b
     * userAccount : 8618954359822
     * userNickname : 小王吧
     * shareFlag : 1
     */

    private int msgId;
    private String func;
    private int type;
    private String gwId;
    private String deviceId;
    private String adminUid;
    private String userAccount;
    private String userNickname;
    private int shareFlag;

    public int getMsgId() {
        return msgId;
    }

    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }

    public String getFunc() {
        return func;
    }

    public void setFunc(String func) {
        this.func = func;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public int getShareFlag() {
        return shareFlag;
    }

    public void setShareFlag(int shareFlag) {
        this.shareFlag = shareFlag;
    }

    public DeviceShareBean(int msgId, String func, int type, String gwId, String deviceId, String adminUid, String userAccount, String userNickname, int shareFlag) {
        this.msgId = msgId;
        this.func = func;
        this.type = type;
        this.gwId = gwId;
        this.deviceId = deviceId;
        this.adminUid = adminUid;
        this.userAccount = userAccount;
        this.userNickname = userNickname;
        this.shareFlag = shareFlag;
    }

    public DeviceShareBean() {
    }
}
