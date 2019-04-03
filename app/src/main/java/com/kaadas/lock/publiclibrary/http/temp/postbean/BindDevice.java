package com.kaadas.lock.publiclibrary.http.temp.postbean;

/**
 * Create By lxj  on 2019/1/10
 * Describe
 */
public class BindDevice {

    /**
     * devmac : string
     * devname : string
     * user_id : string
     * password1 : string
     * password2 : string
     */

    private String devmac;
    private String devname;
    private String user_id;
    private String password1;
    private String password2;

    public String getDevmac() {
        return devmac;
    }

    public void setDevmac(String devmac) {
        this.devmac = devmac;
    }

    public String getDevname() {
        return devname;
    }

    public void setDevname(String devname) {
        this.devname = devname;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPassword1() {
        return password1;
    }

    public void setPassword1(String password1) {
        this.password1 = password1;
    }

    public String getPassword2() {
        return password2;
    }

    public void setPassword2(String password2) {
        this.password2 = password2;
    }
}
