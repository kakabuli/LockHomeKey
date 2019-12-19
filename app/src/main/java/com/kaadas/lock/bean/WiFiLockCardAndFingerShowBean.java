package com.kaadas.lock.bean;

import java.io.Serializable;

public class WiFiLockCardAndFingerShowBean implements Serializable {
    private int num;
    private long createTime;
    private String nickName;


    public WiFiLockCardAndFingerShowBean(int num, long createTime, String nickName) {
        this.num = num;
        this.createTime = createTime;
        this.nickName = nickName;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
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
}
