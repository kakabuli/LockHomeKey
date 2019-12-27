package com.kaadas.lock.publiclibrary.http.postbean;

public class WiFiLockBindBean {

    /**
     * wifiSN : WF132231004
     * lockNickName : 我的锁
     * uid : 5c70ac053c554639ea93cc85
     * randomCode : j123hk1h3kj1h4kjh12k
     */

    private String wifiSN;
    private String lockNickName;
    private String uid;
    private String randomCode;
    /**
     * wifiName : wodewifi
     */

    private String wifiName;

    public WiFiLockBindBean(String wifiSN, String lockNickName, String uid, String randomCode, String wifiName) {
        this.wifiSN = wifiSN;
        this.lockNickName = lockNickName;
        this.uid = uid;
        this.randomCode = randomCode;
        this.wifiName = wifiName;
    }



    public String getWifiSN() {
        return wifiSN;
    }

    public void setWifiSN(String wifiSN) {
        this.wifiSN = wifiSN;
    }

    public String getLockNickName() {
        return lockNickName;
    }

    public void setLockNickName(String lockNickName) {
        this.lockNickName = lockNickName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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
