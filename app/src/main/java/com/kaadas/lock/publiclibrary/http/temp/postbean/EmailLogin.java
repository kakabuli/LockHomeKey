package com.kaadas.lock.publiclibrary.http.temp.postbean;

/**
 * Create By lxj  on 2019/1/9
 * Describe  邮箱登陆的请求参数类
 */
public class EmailLogin {

    /**
     * mail : string
     * password : string
     */

    private String mail;
    private String password;

    public EmailLogin(String mail, String password) {
        this.mail = mail;
        this.password = password;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
