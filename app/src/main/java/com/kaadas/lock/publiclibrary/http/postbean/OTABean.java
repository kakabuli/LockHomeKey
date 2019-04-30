package com.kaadas.lock.publiclibrary.http.postbean;

public class OTABean {
    private int customer;
    private String deviceName;
    private String version;


    public OTABean(int customer, String deviceName, String version) {
        this.customer = customer;
        this.deviceName = deviceName;
        this.version = version;
    }

    public OTABean() {
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
