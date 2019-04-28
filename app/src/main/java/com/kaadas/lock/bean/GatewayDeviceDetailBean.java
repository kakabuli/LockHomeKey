package com.kaadas.lock.bean;

public class GatewayDeviceDetailBean {

    private String deviceName;
    private int power;
    private boolean deviceStatus;
    private String deviceType;

    public GatewayDeviceDetailBean(String deviceName, int power, boolean deviceStatus, String deviceType) {
        this.deviceName = deviceName;
        this.power = power;
        this.deviceStatus = deviceStatus;
        this.deviceType = deviceType;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public boolean isDeviceStatus() {
        return deviceStatus;
    }

    public void setDeviceStatus(boolean deviceStatus) {
        this.deviceStatus = deviceStatus;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }
}
