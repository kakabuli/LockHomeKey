package com.kaadas.lock.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;


public class DeviceDetailBean implements Serializable {

    //设备名称
    private String deviceName;

    //0代表猫眼，1代表网关锁，2代表网关，3代表蓝牙
    private int type;


    //0代表离线，1代表在线
    private String event_str;

    //电量
    private int power=-1;

    private Serializable showCurentBean;



    public DeviceDetailBean() {

    }


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

    public Serializable getShowCurentBean() {
        return showCurentBean;
    }

    public void setShowCurentBean(Serializable showCurentBean) {
        this.showCurentBean = showCurentBean;
    }
}
