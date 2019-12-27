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
    private String username;
    private String userNickname;
    /**
     * adminNickname : 我是管理员
     */

    private String adminNickname;

    public WifiLockShareBean(String wifiSN, String uid, String uname, String userNickname,String adminNickname) {
        this.wifiSN = wifiSN;
        this.uid = uid;
        this.username = uname;
        this.userNickname = userNickname;
        this.adminNickname = adminNickname;
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
        return username;
    }

    public void setUname(String uname) {
        this.username = uname;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public String getAdminNickname() {
        return adminNickname;
    }

    public void setAdminNickname(String adminNickname) {
        this.adminNickname = adminNickname;
    }
}
