package com.kaadas.lock.publiclibrary.http.postbean;

public class SettingPwdDuressAlarmBean {

    /**
     {
     "uid" : "60533d6c7bb9e33be4f0f80d",
     "wifiSN" :"AO95211720019",
     "pwdType" : 1,
     "num" : 2,
     "pwdDuressSwitch" : 0
     }
     */

    private String uid;
    private String wifiSN;
    private int pwdType;
    private int num;
    private int pwdDuressSwitch;

    public SettingPwdDuressAlarmBean(String uid, String wifiSN, int pwdType, int num, int pwdDuressSwitch) {
        this.uid = uid;
        this.wifiSN = wifiSN;
        this.pwdType = pwdType;
        this.num = num;
        this.pwdDuressSwitch = pwdDuressSwitch;
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

    public int getPwdDuressSwitch() {
        return pwdDuressSwitch;
    }

    public void setPwdDuressSwitch(int pwdDuressSwitch) {
        this.pwdDuressSwitch = pwdDuressSwitch;
    }

    @Override
    public String toString() {
        return "SettingPwdDuressAlarmBean{" +
                "uid='" + uid + '\'' +
                ", wifiSN='" + wifiSN + '\'' +
                ", pwdType=" + pwdType +
                ", num=" + num +
                ", pwdDuressSwitch=" + pwdDuressSwitch +
                '}';
    }
}
