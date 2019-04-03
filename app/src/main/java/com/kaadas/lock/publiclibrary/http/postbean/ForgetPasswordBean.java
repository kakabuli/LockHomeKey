package com.kaadas.lock.publiclibrary.http.postbean;

/**
 * Create By lxj  on 2019/3/3
 * Describe
 */
public class ForgetPasswordBean {


    /**
     * name : 8618954359822
     * pwd : ll123456
     * type : 1
     * tokens : 123456
     */

    private String name;
    private String pwd;
    private int type;
    private String tokens;

    public ForgetPasswordBean(String name, String pwd, int type, String tokens) {
        this.name = name;
        this.pwd = pwd;
        this.type = type;
        this.tokens = tokens;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTokens() {
        return tokens;
    }

    public void setTokens(String tokens) {
        this.tokens = tokens;
    }
}
