package com.kaadas.lock.utils.greenDao.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class GatewayBaseInfo {
    @Id
    private String deviceIdUid;
    private String gatewayId;
    private String SW;
    private String znpVersion;
    private String lanIp;
    private String lanNetmask;
    private String wanIp;
    private String wanNetmask;
    private String wanType;
    private String channel; //信道

    /**
     * encryption : wpa/wpa2/wep/none
     * pwd : 12345678
     * ssid : abc
     */
    private String encryption;
    private String pwd;
    private String ssid;

    private String uid;

    @Generated(hash = 1237664054)
    public GatewayBaseInfo(String deviceIdUid, String gatewayId, String SW,
            String znpVersion, String lanIp, String lanNetmask, String wanIp,
            String wanNetmask, String wanType, String channel, String encryption,
            String pwd, String ssid, String uid) {
        this.deviceIdUid = deviceIdUid;
        this.gatewayId = gatewayId;
        this.SW = SW;
        this.znpVersion = znpVersion;
        this.lanIp = lanIp;
        this.lanNetmask = lanNetmask;
        this.wanIp = wanIp;
        this.wanNetmask = wanNetmask;
        this.wanType = wanType;
        this.channel = channel;
        this.encryption = encryption;
        this.pwd = pwd;
        this.ssid = ssid;
        this.uid = uid;
    }

    @Generated(hash = 1089470700)
    public GatewayBaseInfo() {
    }

    public String getDeviceIdUid() {
        return deviceIdUid;
    }

    public void setDeviceIdUid(String deviceIdUid) {
        this.deviceIdUid = deviceIdUid;
    }

    public String getGatewayId() {
        return gatewayId;
    }

    public void setGatewayId(String gatewayId) {
        this.gatewayId = gatewayId;
    }

    public String getSW() {
        return SW;
    }

    public void setSW(String SW) {
        this.SW = SW;
    }

    public String getZnpVersion() {
        return znpVersion;
    }

    public void setZnpVersion(String znpVersion) {
        this.znpVersion = znpVersion;
    }

    public String getLanIp() {
        return lanIp;
    }

    public void setLanIp(String lanIp) {
        this.lanIp = lanIp;
    }

    public String getLanNetmask() {
        return lanNetmask;
    }

    public void setLanNetmask(String lanNetmask) {
        this.lanNetmask = lanNetmask;
    }

    public String getWanIp() {
        return wanIp;
    }

    public void setWanIp(String wanIp) {
        this.wanIp = wanIp;
    }

    public String getWanNetmask() {
        return wanNetmask;
    }

    public void setWanNetmask(String wanNetmask) {
        this.wanNetmask = wanNetmask;
    }

    public String getWanType() {
        return wanType;
    }

    public void setWanType(String wanType) {
        this.wanType = wanType;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getEncryption() {
        return encryption;
    }

    public void setEncryption(String encryption) {
        this.encryption = encryption;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
