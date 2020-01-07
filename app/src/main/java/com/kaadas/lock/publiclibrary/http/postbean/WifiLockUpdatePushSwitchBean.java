package com.kaadas.lock.publiclibrary.http.postbean;

import com.google.gson.annotations.SerializedName;

public class WifiLockUpdatePushSwitchBean {


    /**
     * wifiSN : WF132231004
     * uid : 5c4fe492dc93897aa7d8600b
     * switch : 0
     */

    private String wifiSN;
    private String uid;
    @SerializedName("switch")
    private int switchX;

    public WifiLockUpdatePushSwitchBean(String wifiSN, String uid, int switchX) {
        this.wifiSN = wifiSN;
        this.uid = uid;
        this.switchX = switchX;
    }

    public String getWifiSN() {
        return wifiSN;
    }

    public void setWifiSN(String wifiSN) {
        this.wifiSN = wifiSN;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getSwitchX() {
        return switchX;
    }

    public void setSwitchX(int switchX) {
        this.switchX = switchX;
    }
}
