package com.kaadas.lock.publiclibrary.mqtt.publishbean.ClothesHangerMachineBean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GetHangerStatus implements Serializable {
    /**
    {
"msgtype":"event",
设备所有状态一起上 报
"func":"wfevent ",
"msgId":4,
"devtype":"KdsMxchipHanger",
"eventtype":"HangerInf",
"eventparams":{
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
 "childLock":0/1, "loudspeaker":0/1,
"overload":0/1
},
"wfId":"wfuuid",
"timestamp":"1541468973342"
}
     */

    private String msgtype;
    private String func;
    private int msgId;
    private String devtype;
    private String eventtype;
    private String wfId;
    private String timestamp;
    private EventParams eventparams;


    private class EventParams implements Serializable{
        private Params light;
        private MotorParams motor;
        private Params airDry;
        private Params baking;
        private Params UV;
        private int childLock;
        private int loudspeaker;
        private int overload;

        public Params getLight() {
            return light;
        }

        public void setLight(Params light) {
            this.light = light;
        }

        public MotorParams getMotor() {
            return motor;
        }

        public void setMotor(MotorParams motor) {
            this.motor = motor;
        }

        public Params getAirDry() {
            return airDry;
        }

        public void setAirDry(Params airDry) {
            this.airDry = airDry;
        }

        public Params getBaking() {
            return baking;
        }

        public void setBaking(Params baking) {
            this.baking = baking;
        }

        public Params getUV() {
            return UV;
        }

        public void setUV(Params UV) {
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
    }

    private class MotorParams implements Serializable{
        private int action;
        private int status;

        public int getAction() {
            return action;
        }

        public void setAction(int action) {
            this.action = action;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        @Override
        public String toString() {
            return "MotorParams{" +
                    "action=" + action +
                    ", status=" + status +
                    '}';
        }
    }

    private class Params implements Serializable{
        @SerializedName("switch")
        private int single;
        private int countdown;

        public int getSingle() {
            return single;
        }

        public void setSingle(int single) {
            this.single = single;
        }

        public int getCountdown() {
            return countdown;
        }

        public void setCountdown(int countdown) {
            this.countdown = countdown;
        }
    }

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public String getFunc() {
        return func;
    }

    public void setFunc(String func) {
        this.func = func;
    }

    public int getMsgId() {
        return msgId;
    }

    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }

    public String getDevtype() {
        return devtype;
    }

    public void setDevtype(String devtype) {
        this.devtype = devtype;
    }

    public String getEventtype() {
        return eventtype;
    }

    public void setEventtype(String eventtype) {
        this.eventtype = eventtype;
    }

    public String getWfId() {
        return wfId;
    }

    public void setWfId(String wfId) {
        this.wfId = wfId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public EventParams getEventparams() {
        return eventparams;
    }

    public void setEventparams(EventParams eventparams) {
        this.eventparams = eventparams;
    }
}
