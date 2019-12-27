package com.kaadas.lock.publiclibrary.http.postbean;

public class WifiLockUpdateShareNickBean {


    /**
     * shareId : GI132231004
     * userNickName : 老二
     */



    private String shareId;
    private String nickname;

    /**
     * uid : 5c4fe492dc93897aa7d8600b
     */

    private String uid;


    public WifiLockUpdateShareNickBean(String shareId, String nickname, String uid) {
        this.shareId = shareId;
        this.nickname = nickname;
        this.uid = uid;
    }

    public String getShareId() {
        return shareId;
    }

    public void setShareId(String shareId) {
        this.shareId = shareId;
    }

    public String getUserNickName() {
        return nickname;
    }

    public void setUserNickName(String userNickName) {
        this.nickname = userNickName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
