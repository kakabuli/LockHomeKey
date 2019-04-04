package com.kaadas.lock.publiclibrary.http.temp.postbean;

public class AdministratorOpenLockBean {
    /**
     * user_id
     * open_type
     * is_admin
     * devname
     * */
    private String user_id;
    private String open_type;
    private String is_admin;
    private String devname;

    public AdministratorOpenLockBean(String user_id, String open_type, String is_admin, String devname) {
        this.user_id = user_id;
        this.open_type = open_type;
        this.is_admin = is_admin;
        this.devname = devname;
    }
}
