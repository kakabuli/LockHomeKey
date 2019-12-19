package com.kaadas.lock.publiclibrary.http.postbean;

public class WifiLockDeleteShareBean {

    /**
     * shareId : WF132231004
     * uid : 5c70ac053c554639ea93cc85
     */

    private String shareId;
    private String uid;

    public WifiLockDeleteShareBean(String shareId, String uid) {
        this.shareId = shareId;
        this.uid = uid;
    }

    public String getShareId() {
        return shareId;
    }

    public void setShareId(String shareId) {
        this.shareId = shareId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
