package com.kaadas.lock.publiclibrary.http.postbean;

public class GetHelpLogBean {
    private String uid;
    private int page;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public GetHelpLogBean(String uid, int page) {
        this.uid = uid;
        this.page = page;
    }
    public GetHelpLogBean() {

    }
}
