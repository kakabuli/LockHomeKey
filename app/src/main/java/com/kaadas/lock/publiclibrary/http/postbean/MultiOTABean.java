package com.kaadas.lock.publiclibrary.http.postbean;

import java.util.List;

public class MultiOTABean {
    private int customer;
    private String deviceName;
    private List<OTAParams> versions;


    public MultiOTABean(int customer, String deviceName, List<OTAParams> versions) {
        this.customer = customer;
        this.deviceName = deviceName;
        this.versions = versions;

    }

    public static class OTAParams{
        private int devNum;
        private String version;

        public OTAParams(int devNum, String version) {
            this.devNum = devNum;
            this.version = version;
        }

        public int getDevNum() {
            return devNum;
        }

        public void setDevNum(int devNum) {
            this.devNum = devNum;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }


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

    public List<OTAParams> getVersions() {
        return versions;
    }

    public void setVersions(List<OTAParams> versions) {
        this.versions = versions;
    }

}
