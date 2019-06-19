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

    public AddDeviceBean() {
    }

    public AddDeviceBean(String devmac, String devname, String user_id, String password1, String password2, String model,String bleVersion,String deviceSN) {
        this.devmac = devmac;
        this.devname = devname;
        this.user_id = user_id;
        this.password1 = password1;
        this.password2 = password2;
        this.model = model;
        this.bleVersion = bleVersion;
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
}
