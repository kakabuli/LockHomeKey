package com.kaadas.lock.publiclibrary.mqtt.publishbean;

import java.io.Serializable;

public class UpdateGatewayNickNameBean implements Serializable {


    /**
     * msgId : 123654
     * func : updateGwNickName
     * uid : 5c4fe492dc93897aa7d8600b
     * gwId : GW05191910010
     * nickName : 小网关
     */

    private int msgId;
    private String func;
    private String uid;
    private String gwId;
    private String nickName;

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

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public UpdateGatewayNickNameBean(int msgId, String func, String uid, String gwId, String nickName) {
        this.msgId = msgId;
        this.func = func;
        this.uid = uid;
        this.gwId = gwId;
        this.nickName = nickName;
    }

    public UpdateGatewayNickNameBean() {
    }
}
