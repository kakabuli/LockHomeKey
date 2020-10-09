package com.kaadas.lock.publiclibrary.http.postbean;

public class WiFiLockViewBindFailBean {

    public WiFiLockViewBindFailBean(String wifiSN, int result) {
        this.wifiSN = wifiSN;
        this.result = result;
    }

    /**
     * wifiSN : WF132231004
     * uid : 5c4fe492dc93897aa7d8600b
     */


    private String wifiSN;
    private int result;

    public String getWifiSN() {
        return wifiSN;
    }

    public void setWifiSN(String wifiSN) {
        this.wifiSN = wifiSN;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }
}
