package com.kaadas.lock.publiclibrary.http.postbean;

public class UploadOtaResultBean {


    /**
     * sn : BT01190410001
     * version : 1.1.0
     * customer : 2
     * resultCode : 0
     * devNum : 1
     */

    private String sn;
    private String version;
    private int customer;
    private String resultCode;
    private int devNum;

    public UploadOtaResultBean(String sn, String version, int customer, String resultCode, int devNum) {
        this.sn = sn;
        this.version = version;
        this.customer = customer;
        this.resultCode = resultCode;
        this.devNum = devNum;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getCustomer() {
        return customer;
    }

    public void setCustomer(int customer) {
        this.customer = customer;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public int getDevNum() {
        return devNum;
    }

    public void setDevNum(int devNum) {
        this.devNum = devNum;
    }
}
