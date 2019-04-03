package com.kaadas.lock.publiclibrary.http.temp.resultbean;

/**
 * Create By lxj  on 2019/1/9
 * Describe
 */
public class LoginResult {

    /**
     * uid : 5c0630cc35736f395346ca9a
     * token : eyJfaWQiOiI1YzA2MzBjYzM1NzM2ZjM5NTM0NmNhOWEiLCJ1c2VybmFtZSI6Ijg2MTU2NzUzOTkwNzAiLCJpYXQiOjE1NDcwMTkwNzl9
     * meUsername : 9c7e2ec09e5c4077a8dbb3fed7f2ff6c
     * mePwd : af60b5b5de4148108c6220cbf76e469d
     */

    private String uid;
    private String token;
    private String meUsername;
    private String mePwd;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMeUsername() {
        return meUsername;
    }

    public void setMeUsername(String meUsername) {
        this.meUsername = meUsername;
    }

    public String getMePwd() {
        return mePwd;
    }

    public void setMePwd(String mePwd) {
        this.mePwd = mePwd;
    }

    @Override
    public String toString() {
        return "LoginResult{" +
                "uid='" + uid + '\'' +
                ", token='" + token + '\'' +
                ", meUsername='" + meUsername + '\'' +
                ", mePwd='" + mePwd + '\'' +
                '}';
    }
}
