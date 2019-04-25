package com.kaadas.lock.publiclibrary.mqtt.publishbean;

import java.io.Serializable;

public class UpdateDevNickNameBean implements Serializable {
    private String func;
    private String uid;
    private String devuuid;
    private String deviceId;
    private String nickName;

    public UpdateDevNickNameBean(String func, String uid, String devuuid, String deviceId, String nickName) {
        this.func = func;
        this.uid = uid;
        this.devuuid = devuuid;
        this.deviceId = deviceId;
        this.nickName = nickName;
    }

    public UpdateDevNickNameBean() {
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

    public String getDevuuid() {
        return devuuid;
    }

    public void setDevuuid(String devuuid) {
        this.devuuid = devuuid;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
