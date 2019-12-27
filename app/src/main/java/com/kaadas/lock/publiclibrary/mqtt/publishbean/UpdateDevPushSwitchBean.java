package com.kaadas.lock.publiclibrary.mqtt.publishbean;

public class UpdateDevPushSwitchBean {

    /**
     * msgId : 123654
     * func : updateDevPushSwitch
     * uid : 5c4fe492dc93897aa7d8600b
     * gwId : GW04191410002
     * deviceId : ZG01184112588
     * pushSwitch : 1
     */

    private int msgId;
    private String func;
    private String uid;
    private String gwId;
    private String deviceId;
    private int pushSwitch;

    public UpdateDevPushSwitchBean(int msgId, String func, String uid, String gwId, String deviceId, int pushSwitch) {
        this.msgId = msgId;
        this.func = func;
        this.uid = uid;
        this.gwId = gwId;
        this.deviceId = deviceId;
        this.pushSwitch = pushSwitch;
    }

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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public int getPushSwitch() {
        return pushSwitch;
    }

    public void setPushSwitch(int pushSwitch) {
        this.pushSwitch = pushSwitch;
    }
}
