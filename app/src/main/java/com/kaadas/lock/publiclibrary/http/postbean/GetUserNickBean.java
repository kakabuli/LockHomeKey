package com.kaadas.lock.publiclibrary.http.postbean;

/**
 * Create By lxj  on 2019/3/3
 * Describe
 */
public class GetUserNickBean {

    /**
     * uid : 5c70ac053c554639ea93cc85
     */
    private String uid;

    public GetUserNickBean(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
