package com.kaadas.lock.publiclibrary.http.postbean;

public class UpdateBleVersionBean {


    /**
     * devname : BT05191410011
     * user_id : 5c4fe492dc93897aa7d8600b
     * bleVersion : 1.0.1
     */

    private String devname;
    private String user_id;
    private String bleVersion;

    public UpdateBleVersionBean(String devname, String user_id, String bleVersion) {
        this.devname = devname;
        this.user_id = user_id;
        this.bleVersion = bleVersion;
    }

    public UpdateBleVersionBean() {
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

    public String getBleVersion() {
        return bleVersion;
    }

    public void setBleVersion(String bleVersion) {
        this.bleVersion = bleVersion;
    }
}
