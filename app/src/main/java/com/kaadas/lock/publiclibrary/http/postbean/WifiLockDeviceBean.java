package com.kaadas.lock.publiclibrary.http.postbean;

public class WifiLockDeviceBean {


    private String uid;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public WifiLockDeviceBean(String uid) {
        this.uid = uid;
    }
}
