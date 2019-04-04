package com.kaadas.lock.publiclibrary.http.postbean;

public class DeleteMessageBean {
    private String uid;

    private String mid;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public DeleteMessageBean(String uid, String mid) {
        this.uid = uid;
        this.mid = mid;
    }

    public DeleteMessageBean() {

    }
}
