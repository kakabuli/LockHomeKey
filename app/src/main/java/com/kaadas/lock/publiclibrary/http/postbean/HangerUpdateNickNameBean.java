package com.kaadas.lock.publiclibrary.http.postbean;

public class HangerUpdateNickNameBean {

    /**
     * wifiSN : WF132231004
     * uid : 5c4fe492dc93897aa7d8600b
     * lockNickname : wode
     */

    private String wifiSN;
    private String uid;
    private String hangerNickname;

    public HangerUpdateNickNameBean(String wifiSN, String uid, String lockNickname) {
        this.wifiSN = wifiSN;
        this.uid = uid;
        this.hangerNickname = lockNickname;
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

    public String getLockNickname() {
        return hangerNickname;
    }

    public void setLockNickname(String lockNickname) {
        this.hangerNickname = lockNickname;
    }
}
