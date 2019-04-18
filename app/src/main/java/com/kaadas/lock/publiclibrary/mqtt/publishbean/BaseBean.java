package com.kaadas.lock.publiclibrary.mqtt.publishbean;

import java.io.Serializable;

public class BaseBean implements Serializable {
    private String uid;

    private String func;

    public BaseBean(String uid, String func) {
        this.uid = uid;
        this.func = func;
    }

    public BaseBean() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFunc() {
        return func;
    }

    public void setFunc(String func) {
        this.func = func;
    }
}
