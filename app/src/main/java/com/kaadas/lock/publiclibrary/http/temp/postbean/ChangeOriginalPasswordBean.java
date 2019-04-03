package com.kaadas.lock.publiclibrary.http.temp.postbean;

public class ChangeOriginalPasswordBean {
    /**
     * uid*	string
     *
     * 用户登录返回的uid
     * newpwd*	string
     *
     * 用户修改的新密码
     * oldpwd*	string
     *
     * 用户的旧密码
     * */
    private String uid;
    private String newpwd;
    private String oldpwd;

    public ChangeOriginalPasswordBean(String uid, String newpwd, String oldpwd) {
        this.uid = uid;
        this.newpwd = newpwd;
        this.oldpwd = oldpwd;
    }
}
