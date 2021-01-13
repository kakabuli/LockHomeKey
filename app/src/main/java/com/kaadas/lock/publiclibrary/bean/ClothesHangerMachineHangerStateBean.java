package com.kaadas.lock.publiclibrary.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ClothesHangerMachineHangerStateBean implements Serializable {
    private String _id;
    private long updateTime;
    private int childLock;
    private int loudspeaker;
    private int overload;
    private int status;


    private ClothesHangerMachineMotorBean motor;
    private ClothesHangerMachineLightingBean UV;
    private ClothesHangerMachineLightingBean airDry;
    private ClothesHangerMachineLightingBean baking;
    private ClothesHangerMachineLightingBean light;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ClothesHangerMachineMotorBean getMotor() {
        return motor;
    }

    public void setMotor(ClothesHangerMachineMotorBean motor) {
        this.motor = motor;
    }

    public ClothesHangerMachineLightingBean getUV() {
        return UV;
    }

    public void setUV(ClothesHangerMachineLightingBean UV) {
        this.UV = UV;
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

    public ClothesHangerMachineLightingBean getLight() {
        return light;
    }

    public void setLight(ClothesHangerMachineLightingBean light) {
        this.light = light;
    }
}
