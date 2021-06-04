package com.kaadas.lock.publiclibrary.http.postbean;

import java.io.Serializable;
import java.util.List;

public class SettingPwdDuressAccountBean {

    /**
     {
     "uid" : "60533d6c7bb9e33be4f0f80d",
     "wifiSN" :"AO95211720019",
     "pwdType" : 1,
     "num" : 2,
     "accountType" : 1
     "duressAlarmAccount" : "xxxxx"
     }
     */

    private String uid;
    private String wifiSN;
    private int pwdType;
    private int num;
    private int accountType;
    private String duressAlarmAccount;



    public SettingPwdDuressAccountBean(String uid, String wifiSN, int pwdType, int num, int accountType, String duressAlarmAccount) {
        this.uid = uid;
        this.wifiSN = wifiSN;
        this.pwdType = pwdType;
        this.num = num;
        this.accountType = accountType;
        this.duressAlarmAccount = duressAlarmAccount;
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

    public int getAccountType() {
        return accountType;
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }

    public String getDuressAlarmAccount() {
        return duressAlarmAccount;
    }

    public void setDuressAlarmAccount(String duressAlarmAccount) {
        this.duressAlarmAccount = duressAlarmAccount;
    }

    @Override
    public String toString() {
        return "SettingPwdDuressAccountBean{" +
                "uid='" + uid + '\'' +
                ", wifiSN='" + wifiSN + '\'' +
                ", pwdType=" + pwdType +
                ", num=" + num +
                ", accountType=" + accountType +
                ", duressAlarmAccount='" + duressAlarmAccount + '\'' +
                '}';
    }
}
