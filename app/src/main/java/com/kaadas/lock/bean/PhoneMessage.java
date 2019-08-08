package com.kaadas.lock.bean;

/**
 * Created by denganzhi on 2019/8/8.
 */

public class PhoneMessage {

    private String uid;
    private  String account;
    private  String  model;
    private  String manufacturer;
    private  String  version;

    public PhoneMessage(String uid, String account, String model, String manufacturer, String version) {
        this.uid = uid;
        this.account = account;
        this.model = model;
        this.manufacturer = manufacturer;
        this.version = version;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public PhoneMessage() {
    }
}
