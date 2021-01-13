package com.kaadas.lock.publiclibrary.mqtt.publishbean.ClothesHangerMachineBean;

import java.io.Serializable;

public class GetHangerAllStatus implements Serializable {
    /**

"msgtype": "request",
"msgId":4,
"userId": "5cad4509dc938989e2f542c8",//APP 下发
"wfId": "WF123456789",
"func": "getAllStatus",
"params": {
},
"timestamp": "13433333333",
}
     */

    private String msgtype;
    private int msgId;
    private String userId;
    private String wfId;
    private String func;
    private Params params;
    private String devtype;
    private String timestamp;

    public GetHangerAllStatus(String msgtype, int msgId, String userId, String wfId, String func, Params params, String devtype,String timestamp) {
        this.msgtype = msgtype;
        this.msgId = msgId;
        this.userId = userId;
        this.wfId = wfId;
        this.func = func;
        this.params = params;
        this.devtype = devtype;
        this.timestamp = timestamp;
    }

    public static class Params implements Serializable{
        public Params() {
        }
    }

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public int getMsgId() {
        return msgId;
    }

    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getWfId() {
        return wfId;
    }

    public void setWfId(String wfId) {
        this.wfId = wfId;
    }

    public String getFunc() {
        return func;
    }

    public void setFunc(String func) {
        this.func = func;
    }

    public Params getParams() {
        return params;
    }

    public void setParams(Params params) {
        this.params = params;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
