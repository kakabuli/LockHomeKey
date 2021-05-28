package com.kaadas.lock.bean;

import java.io.Serializable;

public class DuressBean implements Serializable {
    private String wifiSN;
    private int type;
    private int duressToggle;
    private String duressPhone;
    private boolean isHead = false;
    private String num;
    private long createTime;
    private String nickName;

    public DuressBean(String wifiSN, int type, boolean isHead) {
        this.wifiSN = wifiSN;
        this.type = type;
        this.isHead = isHead;
    }

    public DuressBean(String wifiSN, int type, int duressToggle, String duressPhone, String num, long createTime, String nickName) {
        this.wifiSN = wifiSN;
        this.type = type;
        this.duressToggle = duressToggle;
        this.duressPhone = duressPhone;
        this.num = num;
        this.createTime = createTime;
        this.nickName = nickName;
    }

    public String getWifiSN() {
        return wifiSN;
    }

    public void setWifiSN(String wifiSN) {
        this.wifiSN = wifiSN;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getDuressToggle() {
        return duressToggle;
    }

    public void setDuressToggle(int duressToggle) {
        this.duressToggle = duressToggle;
    }

    public String getDuressPhone() {
        return duressPhone;
    }

    public void setDuressPhone(String duressPhone) {
        this.duressPhone = duressPhone;
    }

    public boolean isHead() {
        return isHead;
    }

    public void setHead(boolean head) {
        isHead = head;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
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
                "type=" + type +
                ", duressToggle=" + duressToggle +
                ", duressPhone=" + duressPhone +
                ", isHead=" + isHead +
                ", num='" + num + '\'' +
                ", createTime=" + createTime +
                ", nickName='" + nickName + '\'' +
                '}';
    }
}
