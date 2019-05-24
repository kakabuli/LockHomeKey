package com.kaadas.lock.publiclibrary.mqtt.publishbean;

import java.io.Serializable;

public class GetLockRecordTotal implements Serializable {


    /**
     * msgId : 123654
     * func : countOpenLockRecord
     * uid : 5c4fe492dc93897aa7d8600b
     * devuuid : GW04191410002
     * deviceId : ZG01184112588
     */

    private int msgId;
    private String func;
    private String uid;
    private String devuuid;
    private String deviceId;

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

    public GetLockRecordTotal(int msgId, String func, String uid, String devuuid, String deviceId) {
        this.msgId = msgId;
        this.func = func;
        this.uid = uid;
        this.devuuid = devuuid;
        this.deviceId = deviceId;
    }

    public GetLockRecordTotal() {
    }
}
