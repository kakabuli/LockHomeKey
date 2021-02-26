package com.kaadas.lock.publiclibrary.http.postbean;

public class HttpAllBindDevicesBean {


    private String uid;
    private int modelSearchType;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getModelSearchType() {
        return modelSearchType;
    }

    public void setModelSearchType(int modelSearchType) {
        this.modelSearchType = modelSearchType;
    }

    public HttpAllBindDevicesBean(String uid, int modelSearchType) {
        this.uid = uid;
        this.modelSearchType = modelSearchType;
    }
}
