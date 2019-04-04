package com.kaadas.lock.publiclibrary.http.postbean;

/**
 * Create By lxj  on 2019/3/3
 * Describe
 */
public class DeleteDeviceBean {

    /**
     * adminid : 5c70ac053c554639ea93cc85
     * devname : GI132231004
     */

    private String adminid;
    private String devname;

    public DeleteDeviceBean(String adminid, String devname) {
        this.adminid = adminid;
        this.devname = devname;
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
