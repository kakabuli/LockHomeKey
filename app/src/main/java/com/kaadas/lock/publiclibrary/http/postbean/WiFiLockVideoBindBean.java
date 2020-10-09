package com.kaadas.lock.publiclibrary.http.postbean;

public class WiFiLockVideoBindBean {

    /**
     "wifiSN": "WF132231004",
     "lockNickName": "我的锁",
     "uid": "5c70ac053c554639ea93cc85",
     "randomCode": "safdsaf1s",
     "wifiName":"wodewifi",
     "functionSet":100,
     "distributionNetwork": 1,
     "device_sn":"",
     "device_type":"",
     "device_model":"",
     "mac":"",
     "device_did":"",
     "p2p_password":""
     */

    private String wifiSN;
    private String lockNickName;
    private String uid;
    private String randomCode;
    /**
     * wifiName : wodewifi
     */

    private String wifiName;
    /**
     * functionSet : 100
     */

    private int functionSet;

    private int distributionNetwork;

    private String device_sn;
    private String device_type;
    private String device_model;
    private String mac;
    private String device_did;
    private String p2p_password;


    public WiFiLockVideoBindBean(String wifiSN, String lockNickName, String uid, String randomCode, String wifiName,
                                 int functionSet, int distributionNetwork,String device_sn,String device_type,String device_model,
                                 String mac,String device_did,String p2p_password) {
        this.wifiSN = wifiSN;
        this.lockNickName = lockNickName;
        this.uid = uid;
        this.randomCode = randomCode;
        this.wifiName = wifiName;
        this.functionSet = functionSet;
        this.distributionNetwork = distributionNetwork;
        this.device_sn = device_sn;
        this.device_type = device_type;
        this.device_model = device_model;
        this.mac = mac;
        this.device_did = device_did;
        this.p2p_password = p2p_password;
    }

    public WiFiLockVideoBindBean(String wifiSN, String lockNickName, String uid, String randomCode, String wifiName,
                                 int functionSet, int distributionNetwork,String device_sn,
                                 String mac,String device_did,String p2p_password) {
        this.wifiSN = wifiSN;
        this.lockNickName = lockNickName;
        this.uid = uid;
        this.randomCode = randomCode;
        this.wifiName = wifiName;
        this.functionSet = functionSet;
        this.distributionNetwork = distributionNetwork;
        this.device_sn = device_sn;
        this.mac = mac;
        this.device_did = device_did;
        this.p2p_password = p2p_password;
    }

    public String getDevice_sn() {
        return device_sn;
    }

    public void setDevice_sn(String device_sn) {
        this.device_sn = device_sn;
    }

    public String getDevice_type() {
        return device_type;
    }

    public void setDevice_type(String device_type) {
        this.device_type = device_type;
    }

    public String getDevice_model() {
        return device_model;
    }

    public void setDevice_model(String device_model) {
        this.device_model = device_model;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getDevice_did() {
        return device_did;
    }

    public void setDevice_did(String device_did) {
        this.device_did = device_did;
    }

    public String getP2p_password() {
        return p2p_password;
    }

    public void setP2p_password(String p2p_password) {
        this.p2p_password = p2p_password;
    }

    public String getWifiSN() {
        return wifiSN;
    }

    public void setWifiSN(String wifiSN) {
        this.wifiSN = wifiSN;
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

    public String getRandomCode() {
        return randomCode;
    }

    public void setRandomCode(String randomCode) {
        this.randomCode = randomCode;
    }

    public String getWifiName() {
        return wifiName;
    }

    public void setWifiName(String wifiName) {
        this.wifiName = wifiName;
    }

    public int getFunctionSet() {
        return functionSet;
    }

    public void setFunctionSet(int functionSet) {
        this.functionSet = functionSet;
    }

    public int getDistributionNetwork() {
        return distributionNetwork;
    }

    public void setDistributionNetwork(int distributionNetwork) {
        this.distributionNetwork = distributionNetwork;
    }

    @Override
    public String toString() {
        return "WiFiLockVideoBindBean{" +
                "wifiSN='" + wifiSN + '\'' +
                ", lockNickName='" + lockNickName + '\'' +
                ", uid='" + uid + '\'' +
                ", randomCode='" + randomCode + '\'' +
                ", wifiName='" + wifiName + '\'' +
                ", functionSet=" + functionSet +
                ", distributionNetwork=" + distributionNetwork +
                ", device_sn='" + device_sn + '\'' +
                ", device_type='" + device_type + '\'' +
                ", device_model='" + device_model + '\'' +
                ", mac='" + mac + '\'' +
                ", device_did='" + device_did + '\'' +
                ", p2p_password='" + p2p_password + '\'' +
                '}';
    }
}
