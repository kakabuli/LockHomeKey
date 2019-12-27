package com.kaadas.lock.publiclibrary.http.postbean;

public class WifiLockUpdateInfoBean {

    /**
     * uid : 5c4fe492dc93897aa7d8600b
     * wifiSN : WF132231004
     * randomCode : randomCode666
     */

    private String uid;
    private String wifiSN;
    private String randomCode;
    /**
     * wifiName : wifiName
     */

    private String wifiName;

    public WifiLockUpdateInfoBean(String uid, String wifiSN, String randomCode, String wifiName) {
        this.uid = uid;
        this.wifiSN = wifiSN;
        this.randomCode = randomCode;
        this.wifiName = wifiName;
    }

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

    public String getRandomCode() {
        return randomCode;
    }

    public void setRandomCode(String randomCode) {
        this.randomCode = randomCode;
    }

    public String getWifiName() {
        return wifiName;
    }

    public void setWifiName(String wifiName) {
        this.wifiName = wifiName;
    }
}
