package com.kaadas.lock.bean;

public class DeviceDetailBean {

    //电量
    private int power=-1;

    //设备名称
    private String deviceName;

    //1代表bluetooth，2代表zigbee,3代表猫眼，4网关，5代表猫眼套件
    private int type;

    private int deviceImage;

    //0代表离线，1代表在线
    private int lineStatus;

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getDeviceImage() {
        return deviceImage;
    }

    public void setDeviceImage(int deviceImage) {
        this.deviceImage = deviceImage;
    }

    public int getLineStatus() {
        return lineStatus;
    }

    public void setLineStatus(int lineStatus) {
        this.lineStatus = lineStatus;
    }
}
