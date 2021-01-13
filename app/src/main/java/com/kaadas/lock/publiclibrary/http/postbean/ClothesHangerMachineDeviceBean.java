package com.kaadas.lock.publiclibrary.http.postbean;

public class ClothesHangerMachineDeviceBean {

    private String wifiSN;

    private String uid;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getWifiSN() {
        return wifiSN;
    }

    public void setWifiSN(String wifiSN) {
        this.wifiSN = wifiSN;
    }

    public ClothesHangerMachineDeviceBean(String wifiSN, String uid) {
        this.wifiSN = wifiSN;
        this.uid = uid;
    }
}
