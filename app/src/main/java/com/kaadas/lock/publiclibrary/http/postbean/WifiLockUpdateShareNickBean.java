package com.kaadas.lock.publiclibrary.http.postbean;

public class WifiLockUpdateShareNickBean {

    public WifiLockUpdateShareNickBean(String shareId, String userNickName) {
        this.shareId = shareId;
        this.userNickName = userNickName;
    }

    /**
     * shareId : GI132231004
     * userNickName : 老二
     */



    private String shareId;
    private String userNickName;

    public String getShareId() {
        return shareId;
    }

    public void setShareId(String shareId) {
        this.shareId = shareId;
    }

    public String getUserNickName() {
        return userNickName;
    }

    public void setUserNickName(String userNickName) {
        this.userNickName = userNickName;
    }
}
