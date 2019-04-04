package com.kaadas.lock.publiclibrary.http.postbean;

/**
 * Create By lxj  on 2019/3/3
 * Describe
 */
public class GetPasswordBean {

    /**
     * uid : 5c70d9493c554639ea93cc90
     * devName : GI132231004
     * pwdType : 1
     */

    private String uid;
    private String devName;
    private int pwdType;

    public GetPasswordBean(String uid, String devName, int pwdType) {
        this.uid = uid;
        this.devName = devName;
        this.pwdType = pwdType;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDevName() {
        return devName;
    }

    public void setDevName(String devName) {
        this.devName = devName;
    }

    public int getPwdType() {
        return pwdType;
    }

    public void setPwdType(int pwdType) {
        this.pwdType = pwdType;
    }
}
