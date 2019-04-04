package com.kaadas.lock.publiclibrary.http.temp.postbean;

public class ModifyDeviceOrginaryUserPermissionBean {
    private String admin_id;
    private String items;
    private String dev_username;
    private String devname;
    private String open_purview;
    private String pen_purview;

    public ModifyDeviceOrginaryUserPermissionBean(String admin_id, String items, String dev_username, String devname, String open_purview, String pen_purview, String datestart, String dateend) {
        this.admin_id = admin_id;
        this.items = items;
        this.dev_username = dev_username;
        this.devname = devname;
        this.open_purview = open_purview;
        this.pen_purview = pen_purview;
        this.datestart = datestart;
        this.dateend = dateend;
    }

    private String datestart;
    private String dateend;
}
