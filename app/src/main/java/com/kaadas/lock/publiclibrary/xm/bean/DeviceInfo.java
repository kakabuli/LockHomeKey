package com.kaadas.lock.publiclibrary.xm.bean;


public class DeviceInfo {
    private String deviceSn;
    private String deviceDid;
    private String serviceString;
    // 默认0
    private int cameraChannel=0;
    private String p2pPassword;

    public String getDeviceSn() {
        return deviceSn;
    }

    public void setDeviceSn(String deviceSN) {
        this.deviceSn = deviceSN;
    }

    public String getDeviceDid() {
        return deviceDid;
    }

    public void setDeviceDid(String deviceDid) {
        this.deviceDid = deviceDid;
    }

    public String getServiceString() {
        return serviceString;
    }

    public void setServiceString(String serviceString) {
        this.serviceString = serviceString;
    }

    public int getCameraChannel() {
        return cameraChannel;
    }

    public void setCameraChannel(int cameraChannel) {
        this.cameraChannel = cameraChannel;
    }

    public String getP2pPassword() {
        return p2pPassword;
    }

    public void setP2pPassword(String p2pPassword) {
        this.p2pPassword = p2pPassword;
    }
}
