package com.kaadas.lock.publiclibrary.http.temp.postbean;

/**
 * Create By lxj  on 2019/1/14
 * Describe
 */
public class OpenLockAuth {


    /**
     * devname : string
     * is_admin : string
     * open_type : string
     * user_id : string
     */

    private String devname;
    private String is_admin;
    private String open_type;
    private String user_id;


    public OpenLockAuth() {
    }

    public OpenLockAuth(String devname, String is_admin, String open_type, String user_id) {
        this.devname = devname;
        this.is_admin = is_admin;
        this.open_type = open_type;
        this.user_id = user_id;
    }

    public String getDevname() {
        return devname;
    }

    public void setDevname(String devname) {
        this.devname = devname;
    }

    public String getIs_admin() {
        return is_admin;
    }

    public void setIs_admin(String is_admin) {
        this.is_admin = is_admin;
    }

    public String getOpen_type() {
        return open_type;
    }

    public void setOpen_type(String open_type) {
        this.open_type = open_type;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
