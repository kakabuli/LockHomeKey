package com.kaadas.lock.publiclibrary.http.postbean;

public class WifiLockShareBean {

    /**
     * wifiSN : WF132231004
     * uid : 5c70ac053c554639ea93cc85
     * uname : 8618826545200
     * userNickname : 小萝卜头
     */

    private String wifiSN;
    private String uid;
    private String uname;
    private String userNickname;

    public WifiLockShareBean(String wifiSN, String uid, String uname, String userNickname) {
        this.wifiSN = wifiSN;
        this.uid = uid;
        this.uname = uname;
        this.userNickname = userNickname;
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

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }
}
