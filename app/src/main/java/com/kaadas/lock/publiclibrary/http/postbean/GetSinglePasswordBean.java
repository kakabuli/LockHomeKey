package com.kaadas.lock.publiclibrary.http.postbean;

/**
 * Create By lxj  on 2019/3/3
 * Describe
 */
public class GetSinglePasswordBean {

    /**
     * uid : 5c70d9493c554639ea93cc90
     * devName : GI132231004
     * pwdType : 1
     * num : 01
     */

    private String uid;
    private String devName;
    private int pwdType;
    private String num;

    public GetSinglePasswordBean(String uid, String devName, int pwdType, String num) {
        this.uid = uid;
        this.devName = devName;
        this.pwdType = pwdType;
        this.num = num;
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

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }
}
