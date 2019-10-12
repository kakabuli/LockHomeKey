package com.kaadas.lock.publiclibrary.http.postbean;

public class OTABean {
    private int customer;
    private String deviceName;
    private String version;
    private int devNum;


    public OTABean(int customer, String deviceName, String version,int type) {
        this.customer = customer;
        this.deviceName = deviceName;
        this.version = version;
        this.devNum = type;
    }

    public OTABean() {
    }

    public int getDevNum() {
        return devNum;
    }

    public void setDevNum(int devNum) {
        this.devNum = devNum;
    }

    public int getCustomer() {
        return customer;
    }

    public void setCustomer(int customer) {
        this.customer = customer;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
