package com.kaadas.lock.publiclibrary.http.postbean;

/**
 * author : zhangjierui
 * time   : 2021/12/28
 * desc   :
 */
public class AccountLogoutBean {
    public String userAccount;
    public String uid;
    public int accountType;
    public String code;

    public AccountLogoutBean(String userAccount, String uid, int accountType, String code) {
        this.userAccount = userAccount;
        this.uid = uid;
        this.accountType = accountType;
        this.code = code;
    }
}
