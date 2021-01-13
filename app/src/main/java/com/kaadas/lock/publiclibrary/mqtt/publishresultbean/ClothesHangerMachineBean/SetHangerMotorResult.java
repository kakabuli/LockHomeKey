package com.kaadas.lock.publiclibrary.mqtt.publishresultbean.ClothesHangerMachineBean;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SetHangerMotorResult implements Serializable {
    /**
{
"msgtype": "respone",
"msgId":4,
"userId": "5cad4509dc938989e2f542c8",//APP 下发
"wfId": "WF123456789",
"func": " setMotor",
"code": 200,
"params": {
"action":0/1/2
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
        private int action;
        private int status;

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getAction() {
            return action;
        }

        public void setAction(int action) {
            this.action = action;
        }

        @Override
        public String toString() {
            return "Params{" +
                    "action=" + action +
                    ", status=" + status +
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
        return "SetHangerMotorResult{" +
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
