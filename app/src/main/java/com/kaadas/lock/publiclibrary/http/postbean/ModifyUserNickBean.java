package com.kaadas.lock.publiclibrary.http.postbean;

/**
 * Create By lxj  on 2019/3/3
 * Describe
 */
public class ModifyUserNickBean {


    /**
     * uid : 5c70ac053c554639ea93cc85
     * nickname : ahaha
     */

    private String uid;
    private String nickname;

    public ModifyUserNickBean(String uid, String nickname) {
        this.uid = uid;
        this.nickname = nickname;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
