package com.kaadas.lock.publiclibrary.http.temp.postbean;

/**
 * Create By lxj  on 2019/1/9
 * Describe 手机号登陆的请求
 */
public class PhoneNumberLogin {


    /**
     *
     * tel : string
     * password : string
     */

    private String tel;
    private String password;


    
    public PhoneNumberLogin(String tel, String password) {
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
