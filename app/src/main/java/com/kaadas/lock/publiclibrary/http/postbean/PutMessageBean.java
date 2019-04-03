package com.kaadas.lock.publiclibrary.http.postbean;

/**
 * Create By lxj  on 2019/3/3
 * Describe
 */
public class PutMessageBean {

    /**
     * uid : 5c6be6b314fd2131ecdc07d7
     * suggest : 这个APP很完美，没毛病
     */

    private String uid;
    private String suggest;

    public PutMessageBean(String uid, String suggest) {
        this.uid = uid;
        this.suggest = suggest;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getSuggest() {
        return suggest;
    }

    public void setSuggest(String suggest) {
        this.suggest = suggest;
    }
}
