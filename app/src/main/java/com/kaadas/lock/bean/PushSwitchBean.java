package com.kaadas.lock.bean;

import java.io.Serializable;

/**
 * Create By denganzhi  on 2019/5/15
 * Describe
 */

public class PushSwitchBean implements Serializable {

    private String uid;
    private boolean openlockPushSwitch;


    public PushSwitchBean(String uid, boolean openlockPushSwitch) {
        this.uid = uid;
        this.openlockPushSwitch = openlockPushSwitch;
    }

    public boolean isOpenlockPushSwitch() {
        return openlockPushSwitch;
    }

    public void setOpenlockPushSwitch(boolean openlockPushSwitch) {
        this.openlockPushSwitch = openlockPushSwitch;
    }

    public PushSwitchBean() {
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
