package com.kaadas.lock.publiclibrary.mqtt;

import java.io.Serializable;

public class PowerResultBean implements Serializable {
    private String gatewayId;
    private String deviceId;
    private int power;
    private String timestamp;


    public String getGatewayId() {
        return gatewayId;
    }

    public void setGatewayId(String gatewayId) {
        this.gatewayId = gatewayId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public PowerResultBean(String gatewayId, String deviceId,int power,String timestamp) {
        this.gatewayId = gatewayId;
        this.deviceId = deviceId;
        this.timestamp = timestamp;
        this.power = power;
    }

    public PowerResultBean() {
    }
}
