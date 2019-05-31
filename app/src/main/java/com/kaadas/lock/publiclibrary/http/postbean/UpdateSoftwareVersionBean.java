package com.kaadas.lock.publiclibrary.http.postbean;

public class UpdateSoftwareVersionBean {


    /**
     * devname : BT05191410011
     * user_id : 5c4fe492dc93897aa7d8600b
     * softwareVersion : 1.0.2
     * deviceSN : ZG01191810001
     * peripheralId : 5c4fe492dc93897aa7dccccc
     */

    private String devname;
    private String user_id;
    private String softwareVersion;
    private String deviceSN;
    private String peripheralId;

    public UpdateSoftwareVersionBean(String devname, String user_id, String softwareVersion, String deviceSN, String peripheralId) {
        this.devname = devname;
        this.user_id = user_id;
        this.softwareVersion = softwareVersion;
        this.deviceSN = deviceSN;
        this.peripheralId = peripheralId;
    }

    public UpdateSoftwareVersionBean() {
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

    public String getSoftwareVersion() {
        return softwareVersion;
    }

    public void setSoftwareVersion(String softwareVersion) {
        this.softwareVersion = softwareVersion;
    }

    public String getDeviceSN() {
        return deviceSN;
    }

    public void setDeviceSN(String deviceSN) {
        this.deviceSN = deviceSN;
    }

    public String getPeripheralId() {
        return peripheralId;
    }

    public void setPeripheralId(String peripheralId) {
        this.peripheralId = peripheralId;
    }
}
