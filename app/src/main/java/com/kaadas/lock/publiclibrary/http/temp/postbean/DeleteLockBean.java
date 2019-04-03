package com.kaadas.lock.publiclibrary.http.temp.postbean;

public class DeleteLockBean  {
    public DeleteLockBean(String adminid, String devname) {
        this.adminid = adminid;
        this.devname = devname;
    }

    /**
     * adminid
     * devname
     * */

    private String adminid;

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

    private String devname;
}
