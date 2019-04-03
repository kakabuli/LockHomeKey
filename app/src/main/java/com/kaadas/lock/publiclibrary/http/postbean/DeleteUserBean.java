package com.kaadas.lock.publiclibrary.http.postbean;

/**
 * Create By lxj  on 2019/3/3
 * Describe
 */
public class DeleteUserBean {


    /**
     * dev_username : 8618954359825
     * adminid : 5c70ac053c554639ea93cc85
     * devname : GI132231004
     */

    private String dev_username;
    private String adminid;
    private String devname;

    public DeleteUserBean(String dev_username, String adminid, String devname) {
        this.dev_username = dev_username;
        this.adminid = adminid;
        this.devname = devname;
    }

    public String getDev_username() {
        return dev_username;
    }

    public void setDev_username(String dev_username) {
        this.dev_username = dev_username;
    }

    public String getAdminid() {
        return adminid;
    }

    public void setAdminid(String adminid) {
        this.adminid = adminid;
    }

    public String getDevname() {
        return devname;
    }

    public void setDevname(String devname) {
        this.devname = devname;
    }
}
