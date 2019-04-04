package com.kaadas.lock.bean;

import java.io.Serializable;

/**
 * Created by David on 2019/2/18
 */
public class FingerprintBean implements Serializable {
    private String serialNumber;
    private String name;

    public FingerprintBean(String serialNumber, String name) {
        this.serialNumber = serialNumber;
        this.name = name;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
