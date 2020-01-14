package com.kaadas.lock.publiclibrary.http.postbean;

public class WifiLockUpdatePwdNickBean {

    /**
     * uid : 5c70d9493c554639ea93cc90
     * wifiSN : GI132231004
     * pwdType : 1
     * num : 1
     * nickName : 密码1
     */

    private String uid;
    private String wifiSN;
    private int pwdType;
    private int num;
    private String nickName;
    /**
     * userNickname : 我是管理员
     */

    private String userNickname;

    public WifiLockUpdatePwdNickBean(String uid, String wifiSN, int pwdType, int num, String nickName, String userNickname) {
        this.uid = uid;
        this.wifiSN = wifiSN;
        this.pwdType = pwdType;
        this.num = num;
        this.nickName = nickName;
        this.userNickname = userNickname;
    }

    public WifiLockUpdatePwdNickBean(String uid, String wifiSN, int pwdType, int num, String nickName) {
        this.uid = uid;
        this.wifiSN = wifiSN;
        this.pwdType = pwdType;
        this.num = num;
        this.nickName = nickName;
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

    public int getPwdType() {
        return pwdType;
    }

    public void setPwdType(int pwdType) {
        this.pwdType = pwdType;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }
}
