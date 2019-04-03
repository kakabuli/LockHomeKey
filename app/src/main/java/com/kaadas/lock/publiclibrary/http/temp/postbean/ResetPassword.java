package com.kaadas.lock.publiclibrary.http.temp.postbean;

public class ResetPassword {

    /**
     * name : string
     * pwd : string
     * type : 0
     * tokens : string
     */

    private String name;
    private String pwd;
    private int type;
    private String tokens;

    public ResetPassword(String name, String pwd, int type, String tokens) {
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
