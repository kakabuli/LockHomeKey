package com.kaadas.lock.publiclibrary.http.postbean;

/**
 * Create By lxj  on 2019/3/3
 * Describe
 */
public class AddDeviceBean {

    /**
     * devmac : macadr2
     * devname : GI132231004
     * user_id : 5c70ac053c554639ea93cc85
     * password1 : 123123
     * password2 : 258258
     */


    private String devmac;
    private String devname;
    private String user_id;
    private String password1;
    private String password2;
    private String model;
    private String bleVersion;
    private String deviceSN;
    /**
     * peripheralId : 5c70ac053c554639ea931111
     * softwareVersion : 1.1.0
     * functionSet : 00
     */

    private String peripheralId;
    private String softwareVersion;
    private String functionSet;

    public AddDeviceBean() {
    }

    public AddDeviceBean(String devmac, String devname, String user_id, String password1, String password2, String model, String bleVersion, String deviceSN, String peripheralId, String softwareVersion, String functionSet) {
        this.devmac = devmac;
        this.devname = devname;
        this.user_id = user_id;
        this.password1 = password1;
        this.password2 = password2;
        this.model = model;
        this.bleVersion = bleVersion;
        this.deviceSN = deviceSN;
        this.peripheralId = peripheralId;
        this.softwareVersion = softwareVersion;
        this.functionSet = functionSet;
    }



    public String getDeviceSN() {
        return deviceSN;
    }

    public void setDeviceSN(String deviceSN) {
        this.deviceSN = deviceSN;
    }

    public String getBleVersion() {
        return bleVersion;
    }

    public void setBleVersion(String bleVersion) {
        this.bleVersion = bleVersion;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

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

    public String getPeripheralId() {
        return peripheralId;
    }

    public void setPeripheralId(String peripheralId) {
        this.peripheralId = peripheralId;
    }

    public String getSoftwareVersion() {
        return softwareVersion;
    }

    public void setSoftwareVersion(String softwareVersion) {
        this.softwareVersion = softwareVersion;
    }

    public String getFunctionSet() {
        return functionSet;
    }

    public void setFunctionSet(String functionSet) {
        this.functionSet = functionSet;
    }
}
