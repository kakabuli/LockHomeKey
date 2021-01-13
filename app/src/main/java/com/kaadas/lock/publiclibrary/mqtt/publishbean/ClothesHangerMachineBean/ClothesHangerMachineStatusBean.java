package com.kaadas.lock.publiclibrary.mqtt.publishbean.ClothesHangerMachineBean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ClothesHangerMachineStatusBean implements Serializable {
    private String msgtype;
    private String func;
    private String devtype;
    private String eventtype;
    private Param eventparams;
    private String wfId;
    private String timestamp;


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

    public Param getEventparams() {
        return eventparams;
    }

    public void setEventparams(Param eventparams) {
        this.eventparams = eventparams;
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

    public class Param{
        private int status;
        private int childLock;
        private int loudspeaker;
        private int overload;
        private LightParam light;
        private LightParam airDry;
        private LightParam baking;
        private LightParam UV;
        private MotorParam motor;

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
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

        public LightParam getLight() {
            return light;
        }

        public void setLight(LightParam light) {
            this.light = light;
        }

        public LightParam getAirDry() {
            return airDry;
        }

        public void setAirDry(LightParam airDry) {
            this.airDry = airDry;
        }

        public LightParam getBaking() {
            return baking;
        }

        public void setBaking(LightParam baking) {
            this.baking = baking;
        }

        public LightParam getUV() {
            return UV;
        }

        public void setUV(LightParam UV) {
            this.UV = UV;
        }

        public MotorParam getMotor() {
            return motor;
        }

        public void setMotor(MotorParam motor) {
            this.motor = motor;
        }
    }

    public class LightParam{
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

    public class MotorParam{
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
    }
}
