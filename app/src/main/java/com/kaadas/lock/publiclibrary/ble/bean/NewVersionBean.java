package com.kaadas.lock.publiclibrary.ble.bean;

/**
 * 发现新的版本
 */
public class NewVersionBean {

    private String devname;
    private String bleVersion;


    public NewVersionBean(String devname, String bleVersion) {
        this.devname = devname;
        this.bleVersion = bleVersion;
    }

    public String getDevname() {
        return devname;
    }

    public void setDevname(String devname) {
        this.devname = devname;
    }

    public String getBleVersion() {
        return bleVersion;
    }

    public void setBleVersion(String bleVersion) {
        this.bleVersion = bleVersion;
    }
}
