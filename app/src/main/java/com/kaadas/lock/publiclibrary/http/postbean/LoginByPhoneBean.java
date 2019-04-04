package com.kaadas.lock.publiclibrary.http.postbean;

/**
 * Create By lxj  on 2019/3/3
 * Describe
 */
public class LoginByPhoneBean {


    /**
     * tel	是	String	手机号码
     * password	是	String	密码
     */


    private String tel;
    private String password;


    public LoginByPhoneBean(String tel, String password) {
        this.tel = tel;
        this.password = password;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
