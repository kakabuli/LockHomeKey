package com.kaadas.lock.bean;

import java.io.Serializable;

/**
 * Create By denganzhi  on 2019/5/15
 * Describe
 */

public class PushSwitch implements Serializable {

    private String uid;


    public PushSwitch(String uid) {
        this.uid = uid;
    }

    public PushSwitch() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public String toString() {
        return "PushSwitch{" +
                "uid='" + uid + '\'' +
                '}';
    }
}
