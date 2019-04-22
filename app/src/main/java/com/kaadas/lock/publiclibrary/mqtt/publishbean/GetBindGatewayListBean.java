package com.kaadas.lock.publiclibrary.mqtt.publishbean;

import java.io.Serializable;

public class GetBindGatewayListBean implements Serializable {
    private String uid;

    private String func;

    public GetBindGatewayListBean(String uid, String func) {
        this.uid = uid;
        this.func = func;
    }

    public GetBindGatewayListBean() {
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
