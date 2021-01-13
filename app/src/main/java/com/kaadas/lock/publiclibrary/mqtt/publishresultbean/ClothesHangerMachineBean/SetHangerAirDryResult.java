package com.kaadas.lock.publiclibrary.mqtt.publishresultbean.ClothesHangerMachineBean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SetHangerAirDryResult implements Serializable {

    /**
     {
     "msgtype": "respone",
     "msgId":4,
     "userId": "5cad4509dc938989e2f542c8",//APP 下 发
     "wfId": "WF123456789",
     "func": " setAirDry",
     "code": 200,
     "params": {
     "switch":0/1/2, "countdown":120
     },
     "timestamp": "13433333333" ， }
     */

    private String msgtype;
    private int msgId;
    private String userId;
    private String wfId;
    private String func;
    private int code;
    private Params params;
    private String timestamp;

    public class Params implements Serializable{
        @SerializedName("switch")
        private int single;
        private int countdown;

        public int getCountdown() {
            return countdown;
        }

        public void setCountdown(int countdown) {
            this.countdown = countdown;
        }

        public int getSingle() {
            return single;
        }

        public void setSingle(int single) {
            this.single = single;
        }

        @Override
        public String toString() {
            return "Params{" +
                    "single=" + single +
                    ", countdown=" + countdown +
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

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
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

    @Override
    public String toString() {
        return "SetHangerLightingResult{" +
                "msgtype='" + msgtype + '\'' +
                ", msgId=" + msgId +
                ", userId='" + userId + '\'' +
                ", wfId='" + wfId + '\'' +
                ", func='" + func + '\'' +
                ", code=" + code +
                ", params=" + params +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
