package com.kaadas.lock.publiclibrary.http.postbean;

public class WiFiLockBindBean {

    /**
     * wifiSN : WF132231004
     * productSN : 34353245191810001
     * productModel : k8
     * lockNickName : 我的锁
     * uid : 5c70ac053c554639ea93cc85
     * softwareVersion : 1.1.0
     * functionSet : 00
     */

    private String wifiSN;
    private String productSN;
    private String productModel;
    private String lockNickName;
    private String uid;
    private String softwareVersion;
    private String functionSet;

    public WiFiLockBindBean(String wifiSN, String productSN, String productModel, String lockNickName, String uid, String softwareVersion, String functionSet) {
        this.wifiSN = wifiSN;
        this.productSN = productSN;
        this.productModel = productModel;
        this.lockNickName = lockNickName;
        this.uid = uid;
        this.softwareVersion = softwareVersion;
        this.functionSet = functionSet;
    }

    public String getWifiSN() {
        return wifiSN;
    }

    public void setWifiSN(String wifiSN) {
        this.wifiSN = wifiSN;
    }

    public String getProductSN() {
        return productSN;
    }

    public void setProductSN(String productSN) {
        this.productSN = productSN;
    }

    public String getProductModel() {
        return productModel;
    }

    public void setProductModel(String productModel) {
        this.productModel = productModel;
    }

    public String getLockNickName() {
        return lockNickName;
    }

    public void setLockNickName(String lockNickName) {
        this.lockNickName = lockNickName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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
