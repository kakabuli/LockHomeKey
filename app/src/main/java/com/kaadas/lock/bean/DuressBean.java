package com.kaadas.lock.bean;

import java.io.Serializable;

public class DuressBean implements Serializable {
    private String wifiSN;
    private int pwdType;
    private int pwdDuressSwitch;
    private String duressAlarmAccount;
    private boolean isHead = false;
    private int num;
    private long createTime;
    private String nickName;

    public DuressBean(String wifiSN, int pwdType, boolean isHead) {
        this.wifiSN = wifiSN;
        this.pwdType = pwdType;
        this.isHead = isHead;
    }

    public DuressBean(String wifiSN, int pwdType, int pwdDuressSwitch, String duressAlarmAccount, int num, long createTime, String nickName) {
        this.wifiSN = wifiSN;
        this.pwdType = pwdType;
        this.pwdDuressSwitch = pwdDuressSwitch;
        this.duressAlarmAccount = duressAlarmAccount;
        this.num = num;
        this.createTime = createTime;
        this.nickName = nickName;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getNum() {
        return num;
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

    public int getPwdDuressSwitch() {
        return pwdDuressSwitch;
    }

    public void setPwdDuressSwitch(int pwdDuressSwitch) {
        this.pwdDuressSwitch = pwdDuressSwitch;
    }

    public String getDuressAlarmAccount() {
        return duressAlarmAccount;
    }

    public void setDuressAlarmAccount(String duressAlarmAccount) {
        this.duressAlarmAccount = duressAlarmAccount;
    }

    public boolean isHead() {
        return isHead;
    }

    public void setHead(boolean head) {
        isHead = head;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    @Override
    public String toString() {
        return "PhilipsDuressBean{" +
                "pwdType=" + pwdType +
                ", pwdDuressSwitch=" + pwdDuressSwitch +
                ", duressAlarmAccount=" + duressAlarmAccount +
                ", isHead=" + isHead +
                ", num='" + num + '\'' +
                ", createTime=" + createTime +
                ", nickName='" + nickName + '\'' +
                '}';
    }
}
