package com.kaadas.lock.utils.greenDao.bean;

import com.kaadas.lock.publiclibrary.mqtt.PowerResultBean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class GatewayLockBaseInfo {
    @Id
    private String deviceUId;
    private String gatewayId;
    private String deviceId;
    private String macaddr;
    private String model;
    private String firmware;
    private String hwversion;
    private String swversion;
    private String manufact;
    private String linkquality;
    private String uid;

    @Generated(hash = 73491696)
    public GatewayLockBaseInfo(String deviceUId, String gatewayId, String deviceId,
            String macaddr, String model, String firmware, String hwversion,
            String swversion, String manufact, String linkquality, String uid) {
        this.deviceUId = deviceUId;
        this.gatewayId = gatewayId;
        this.deviceId = deviceId;
        this.macaddr = macaddr;
        this.model = model;
        this.firmware = firmware;
        this.hwversion = hwversion;
        this.swversion = swversion;
        this.manufact = manufact;
        this.linkquality = linkquality;
        this.uid = uid;
    }

    @Generated(hash = 262657088)
    public GatewayLockBaseInfo() {
    }

    public String getDeviceUId() {
        return deviceUId;
    }

    public void setDeviceUId(String deviceUId) {
        this.deviceUId = deviceUId;
    }

    public String getGatewayId() {
        return gatewayId;
    }

    public void setGatewayId(String gatewayId) {
        this.gatewayId = gatewayId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getMacaddr() {
        return macaddr;
    }

    public void setMacaddr(String macaddr) {
        this.macaddr = macaddr;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getFirmware() {
        return firmware;
    }

    public void setFirmware(String firmware) {
        this.firmware = firmware;
    }

    public String getHwversion() {
        return hwversion;
    }

    public void setHwversion(String hwversion) {
        this.hwversion = hwversion;
    }

    public String getSwversion() {
        return swversion;
    }

    public void setSwversion(String swversion) {
        this.swversion = swversion;
    }

    public String getManufact() {
        return manufact;
    }

    public void setManufact(String manufact) {
        this.manufact = manufact;
    }

    public String getLinkquality() {
        return linkquality;
    }

    public void setLinkquality(String linkquality) {
        this.linkquality = linkquality;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
