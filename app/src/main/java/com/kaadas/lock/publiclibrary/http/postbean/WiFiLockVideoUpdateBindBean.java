package com.kaadas.lock.publiclibrary.http.postbean;

public class WiFiLockVideoUpdateBindBean {

    /**
     {
     "uid":"5c4fe492dc93897aa7d8600b",
     "wifiSN":"WF132231004",
     "randomCode":"randomCode666",
     "wifiName": "wifiName",
     "functionSet": 100,
     "device_did":"",
     "p2p_password":""
     }
     */

    private String wifiSN;
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

    private String device_did;
    private String p2p_password;


    public WiFiLockVideoUpdateBindBean(String wifiSN, String uid, String randomCode, String wifiName,
                                       int functionSet,String device_did, String p2p_password) {
        this.wifiSN = wifiSN;
        this.uid = uid;
        this.randomCode = randomCode;
        this.wifiName = wifiName;
        this.functionSet = functionSet;
        this.device_did = device_did;
        this.p2p_password = p2p_password;
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


    @Override
    public String toString() {
        return "WiFiLockVideoBindBean{" +
                "wifiSN='" + wifiSN + '\'' +
                ", uid='" + uid + '\'' +
                ", randomCode='" + randomCode + '\'' +
                ", wifiName='" + wifiName + '\'' +
                ", functionSet=" + functionSet +
                ", device_did='" + device_did + '\'' +
                ", p2p_password='" + p2p_password + '\'' +
                '}';
    }
}
