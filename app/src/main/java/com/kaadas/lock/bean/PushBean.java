package com.kaadas.lock.bean;

/**
 * Create By denganzhi  on 2019/5/10
 * Describe
 */

public class PushBean {

    private String uid;
    private  String JPushId;
    private  int  type;

    public PushBean(String uid, String JPushId, int type) {
        this.uid = uid;
        this.JPushId = JPushId;
        this.type = type;
    }

    public PushBean() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getJPushId() {
        return JPushId;
    }

    public void setJPushId(String JPushId) {
        this.JPushId = JPushId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
