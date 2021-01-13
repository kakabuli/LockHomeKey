package com.kaadas.lock.publiclibrary.mqtt.publishbean.ClothesHangerMachineBean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SetHangerMotorBean implements Serializable {

    /**
     {
     "msgtype": "request",
     "msgId":4,
     "userId": "5cad4509dc938989e2f542c8",//APP 下发
     "wfId": "WF123456789",
     "func": "setMotor",
     "params": {
     "action":0/1/2
     },
     "timestamp": "13433333333",
     }
     */
    private String msgtype;
    private int msgId;
    private String userId;
    private String wfId;
    private String func;
    private SetHangerMotorParam params;
    private String timestamp;
    private String devtype;

    public SetHangerMotorBean(String msgtype, int msgId, String userId, String wfId, String func, SetHangerMotorParam params, String devtype,String timestamp) {
        this.msgtype = msgtype;
        this.msgId = msgId;
        this.userId = userId;
        this.wfId = wfId;
        this.func = func;
        this.params = params;
        this.timestamp = timestamp;
        this.devtype = devtype;
    }

    public static class SetHangerMotorParam implements Serializable{
        private int action;

        public int getAction() {
            return action;
        }

        public void setAction(int action) {
            this.action = action;
        }

        @Override
        public String toString() {
            return "SetHangerMotorParam{" +
                    "action=" + action +
                    '}';
        }
    }

    public String getDevtype() {
        return devtype;
    }

    public void setDevtype(String devtype) {
        this.devtype = devtype;
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

    public SetHangerMotorParam getParams() {
        return params;
    }

    public void setParams(SetHangerMotorParam params) {
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
        return "SetHangerMotorBean{" +
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
