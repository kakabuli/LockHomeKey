package com.kaadas.lock.publiclibrary.http.postbean;

public class WifiLockDeleteShareBean {

    /**
     * shareId : WF132231004
     * uid : 5c70ac053c554639ea93cc85
     */

    private String shareId;
    private String uid;
    /**
     * adminNickname : 我是管理员
     */

    private String adminNickname;

    public WifiLockDeleteShareBean(String shareId, String uid, String adminNickname) {
        this.shareId = shareId;
        this.uid = uid;
        this.adminNickname = adminNickname;
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

    public String getAdminNickname() {
        return adminNickname;
    }

    public void setAdminNickname(String adminNickname) {
        this.adminNickname = adminNickname;
    }
}
