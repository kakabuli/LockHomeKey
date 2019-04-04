package com.kaadas.lock.publiclibrary.http.postbean;

/**
 * Create By lxj  on 2019/3/3
 * Describe
 */
public class RegisterByPhoneBean {


    /**
     * name	是	String	手机号码
     * password	是	String	密码
     * tokens	是	String	手机密码
     */

    private String name;
    private String password;
    private String tokens;

    public RegisterByPhoneBean(String name, String password, String tokens) {
        this.name = name;
        this.password = password;
        this.tokens = tokens;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTokens() {
        return tokens;
    }

    public void setTokens(String tokens) {
        this.tokens = tokens;
    }
}
