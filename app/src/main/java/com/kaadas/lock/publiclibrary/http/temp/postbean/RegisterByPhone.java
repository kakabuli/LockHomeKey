package com.kaadas.lock.publiclibrary.http.temp.postbean;

public class RegisterByPhone {

    /**
     * name : string
     * password : string
     * tokens : string
     */

    private String name;
    private String password;
    private String tokens;

    public RegisterByPhone(String name, String password, String tokens) {
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
