package com.kaadas.lock.publiclibrary.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ClothesHangerMachineAllStatusBean implements Serializable {

    /*
    "msgtype": "respone",
"msgId":4,
"userId": "5cad4509dc938989e2f542c8",//APP 下发
"wfId": "WF123456789",
"func": "getAllStatus",
"code": 200,
"params": {
"light":{
switch: 0/1,
countdown:0-120
},
"motor":{
action: 0/1/2,
status:0/1/2/3
},
"airDry":{
switch: 0/1/2,
countdown:0-120/240
},
"baking":{
switch: 0/1/2,
countdown:0-120/240
},
"UV":{
switch: 0/1,
countdown:30
},
"childLock":0/1,
"loudspeaker":0/1,
"overload":0/1
},
"timestamp": "13433333333" ，
     */


    private String msgtype;
    private int msgId;
    private String userId;
    private String wfId;
    private String func;
    private int code;
    private Params params;
    private String timestamp;
    private String devtype;

    public class Params{
        private ClothesHangerMachineLightingBean light;
        private ClothesHangerMachineMotorBean motor;
        private ClothesHangerMachineLightingBean airDry;
        private ClothesHangerMachineLightingBean baking;
        private ClothesHangerMachineLightingBean UV;

        private int childLock;
        private int loudspeaker;
        private int overload;


        public ClothesHangerMachineLightingBean getLight() {
            return light;
        }

        public void setLight(ClothesHangerMachineLightingBean light) {
            this.light = light;
        }

        public ClothesHangerMachineMotorBean getMotor() {
            return motor;
        }

        public void setMotor(ClothesHangerMachineMotorBean motor) {
            this.motor = motor;
        }

        public ClothesHangerMachineLightingBean getAirDry() {
            return airDry;
        }

        public void setAirDry(ClothesHangerMachineLightingBean airDry) {
            this.airDry = airDry;
        }

        public ClothesHangerMachineLightingBean getBaking() {
            return baking;
        }

        public void setBaking(ClothesHangerMachineLightingBean baking) {
            this.baking = baking;
        }

        public ClothesHangerMachineLightingBean getUV() {
            return UV;
        }

        public void setUV(ClothesHangerMachineLightingBean UV) {
            this.UV = UV;
        }

        public int getChildLock() {
            return childLock;
        }

        public void setChildLock(int childLock) {
            this.childLock = childLock;
        }

        public int getLoudspeaker() {
            return loudspeaker;
        }

        public void setLoudspeaker(int loudspeaker) {
            this.loudspeaker = loudspeaker;
        }

        public int getOverload() {
            return overload;
        }

        public void setOverload(int overload) {
            this.overload = overload;
        }

        @Override
        public String toString() {
            return "Params{" +
                    "light=" + light +
                    ", motor=" + motor +
                    ", airDry=" + airDry +
                    ", baking=" + baking +
                    ", UV=" + UV +
                    ", childLock=" + childLock +
                    ", loudspeaker=" + loudspeaker +
                    ", overload=" + overload +
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
        return "ClothesHangerMachineAllStatusBean{" +
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
