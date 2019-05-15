package com.kaadas.lock.publiclibrary.mqtt.publishbean;

import java.io.Serializable;

public class BindGatewayMemeBean implements Serializable {


    /**
     * msgId : 123654
     * func : bindMeme
     * uid : 5c4fe492dc93897aa7d8600b
     * gwId : GW01182510163
     * devuuid : GW01182510163
     */

    private int msgId;
    private String func;
    private String uid;
    private String gwId;
    private String devuuid;

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

    public String getDevuuid() {
        return devuuid;
    }

    public void setDevuuid(String devuuid) {
        this.devuuid = devuuid;
    }

    public BindGatewayMemeBean(int msgId, String func, String uid, String gwId, String devuuid) {
        this.msgId = msgId;
        this.func = func;
        this.uid = uid;
        this.gwId = gwId;
        this.devuuid = devuuid;
    }

    public BindGatewayMemeBean() {
    }
}
