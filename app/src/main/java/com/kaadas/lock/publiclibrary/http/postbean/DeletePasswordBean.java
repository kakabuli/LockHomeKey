package com.kaadas.lock.publiclibrary.http.postbean;

import java.util.List;

/**
 * Create By lxj  on 2019/3/3
 * Describe
 */
public class DeletePasswordBean {


    /**
     * uid	是	String	管理员用户ID
     * devName	是	String	设备唯一编号
     * pwdList	是	list	密钥列表
     */

    private String uid;
    private String devName;
    private List<DeletePassword> pwdList;

    public DeletePasswordBean(String uid, String devName, List<DeletePassword> pwdList) {
        this.uid = uid;
        this.devName = devName;
        this.pwdList = pwdList;
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

    public List<DeletePassword> getPwdList() {
        return pwdList;
    }

    public void setPwdList(List<DeletePassword> pwdList) {
        this.pwdList = pwdList;
    }

    public static class DeletePassword {
        /**
         * pwdType	是	String	密钥类型：1永久密码 2临时密码 3指纹密码 4卡片密码
         * num	是	String	密钥编号
         */

        private int pwdType;
        private String num;

        public DeletePassword(int pwdType, String num) {
            this.pwdType = pwdType;
            this.num = num;
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
}
