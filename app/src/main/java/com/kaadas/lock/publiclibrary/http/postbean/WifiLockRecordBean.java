package com.kaadas.lock.publiclibrary.http.postbean;

public class WifiLockRecordBean {


    public WifiLockRecordBean(String wifiSN, int page) {
        this.wifiSN = wifiSN;
        this.page = page;
    }

    /**
     * wifiSN : WF132231004
     * page : 1
     */



    private String wifiSN;
    private int page;

    public String getWifiSN() {
        return wifiSN;
    }

    public void setWifiSN(String wifiSN) {
        this.wifiSN = wifiSN;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
