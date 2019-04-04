package com.kaadas.lock.publiclibrary.http.postbean;

/**
 * Create By lxj  on 2019/3/3
 * Describe
 */
public class ModifyPasswordBean {

    /**
     * uid : 8618954359822
     * newpwd : ll123654
     * oldpwd : ll123456
     */

    private String uid;
    private String newpwd;
    private String oldpwd;


    public ModifyPasswordBean(String uid, String newpwd, String oldpwd) {
        this.uid = uid;
        this.newpwd = newpwd;
        this.oldpwd = oldpwd;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNewpwd() {
        return newpwd;
    }

    public void setNewpwd(String newpwd) {
        this.newpwd = newpwd;
    }

    public String getOldpwd() {
        return oldpwd;
    }

    public void setOldpwd(String oldpwd) {
        this.oldpwd = oldpwd;
    }
}
