package com.kaadas.lock.bean;

public class DeviceDetailBean {

    //设备名称
    private String deviceName;

    //0代表猫眼，1代表网关锁，2代表网关，3代表网关
    private int type;


    //0代表离线，1代表在线
    private String event_str;

    //电量
    private int power=-1;

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

    public String getEvent_str() {
        return event_str;
    }

    public void setEvent_str(String event_str) {
        this.event_str = event_str;
    }
}
