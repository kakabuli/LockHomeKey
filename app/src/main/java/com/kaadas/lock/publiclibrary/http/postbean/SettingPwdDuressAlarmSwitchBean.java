package com.kaadas.lock.publiclibrary.http.postbean;

public class SettingPwdDuressAlarmSwitchBean {

    /**
     {
     "uid" : "60533d6c7bb9e33be4f0f80d",
     "wifiSN" : "AO95211720019",
     "duressAlarmSwitch" : 1
     */

    private String uid;
    private String wifiSN;
    private int duressAlarmSwitch;

    public SettingPwdDuressAlarmSwitchBean(String uid, String wifiSN, int duressAlarmSwitch) {
        this.uid = uid;
        this.wifiSN = wifiSN;
        this.duressAlarmSwitch = duressAlarmSwitch;
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

    public int getDuressAlarmSwitch() {
        return duressAlarmSwitch;
    }

    public void setDuressAlarmSwitch(int duressAlarmSwitch) {
        this.duressAlarmSwitch = duressAlarmSwitch;
    }

    @Override
    public String toString() {
        return "SettingPwdDuressAlarmSwitchBean{" +
                "uid='" + uid + '\'' +
                ", wifiSN='" + wifiSN + '\'' +
                ", duressAlarmSwitch=" + duressAlarmSwitch +
                '}';
    }
}
