package com.kaadas.lock.publiclibrary.http.postbean;

public class WiFiLockWifiSNAndUid {

    public WiFiLockWifiSNAndUid(String wifiSN, String uid) {
        this.wifiSN = wifiSN;
        this.uid = uid;
    }

    /**
     * wifiSN : WF132231004
     * uid : 5c4fe492dc93897aa7d8600b
     */


    private String wifiSN;
    private String uid;

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
}
