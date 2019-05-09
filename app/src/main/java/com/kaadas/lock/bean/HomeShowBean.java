package com.kaadas.lock.bean;

import java.io.Serializable;

public class  HomeShowBean implements Serializable {

    /**
     * 设备类型  猫眼
     */
    public static final int TYPE_CAT_EYE = 0;

    /**
     * 设备类型  网关锁
     */
    public static final int TYPE_GATEWAY_LOCK = 1;

    /**
     * 设备类型  网关
     */
    public static final int TYPE_GATEWAY = 2;

    /**
     * 设备类型  蓝牙锁
     */
    public static final int TYPE_BLE_LOCK = 3;

    //设备类型
    private int deviceType;
    // 设备唯一标识
    private String deviceId;

    //设备的昵称
    private String deviceNickName;
    //设备的具体数据
    private Object object;


    public HomeShowBean() {

    }

    public HomeShowBean(int deviceType, String deviceId, String deviceNickName, Object object) {
        this.deviceType = deviceType;
        this.deviceId = deviceId;
        this.deviceNickName = deviceNickName;
        this.object = object;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public String getDeviceNickName() {
        return deviceNickName;
    }

    public void setDeviceNickName(String deviceNickName) {
        this.deviceNickName = deviceNickName;
    }
}
