package com.kaadas.lock.publiclibrary.mqtt.publishbean.ClothesHangerMachineBean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SetHangerLightingBean implements Serializable {

    /**
    {
"msgtype": "request",
"msgId":4,
"userId": "5cad4509dc938989e2f542c8",//APP 下发
"wfId": "WF123456789",
"func": "setLight",
"params": {
"switch":0/1
},
"timestamp": "13433333333",
}
     */
    private String msgtype;
    private int msgId;
    private String userId;
    private String wfId;
    private String func;
    private SetHangerLightingParam params;
    private String timestamp;
    private String devtype;

    public SetHangerLightingBean(String msgtype, int msgId, String userId, String wfId, String func, SetHangerLightingParam params, String devtype,String timestamp) {
        this.msgtype = msgtype;
        this.msgId = msgId;
        this.userId = userId;
        this.wfId = wfId;
        this.func = func;
        this.params = params;
        this.timestamp = timestamp;
        this.devtype = devtype;
    }

    public String getDevtype() {
        return devtype;
    }

    public void setDevtype(String devtype) {
        this.devtype = devtype;
    }

    public static class SetHangerLightingParam implements Serializable{
        @SerializedName("switch")
        private int single;

        public int getSingle() {
            return single;
        }

        public void setSingle(int single) {
            this.single = single;
        }

        @Override
        public String toString() {
            return "SetHangerLightingParam{" +
                    "single=" + single +
                    '}';
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

    public SetHangerLightingParam getParams() {
        return params;
    }

    public void setParams(SetHangerLightingParam params) {
        this.params = params;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "SetHangerLightingBean{" +
                "msgtype='" + msgtype + '\'' +
                ", msgId=" + msgId +
                ", userId='" + userId + '\'' +
                ", wfId='" + wfId + '\'' +
                ", func='" + func + '\'' +
                ", params=" + params +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
