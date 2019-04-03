package com.kaadas.lock.publiclibrary.http.postbean;

/**
 * Create By lxj  on 2019/3/3
 * Describe
 */
public class ModifyPasswordNickBean {

    /**
     * uid : 5c70d9493c554639ea93cc90
     * devName : GI132231004
     * pwdType : 1
     * num : 01
     * nickName : 密码1
     */

    private String uid;
    private String devName;
    private int pwdType;
    private String num;
    private String nickName;

    public ModifyPasswordNickBean(String uid, String devName, int pwdType, String num, String nickName) {
        this.uid = uid;
        this.devName = devName;
        this.pwdType = pwdType;
        this.num = num;
        this.nickName = nickName;
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

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
