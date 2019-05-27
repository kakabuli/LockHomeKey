package com.kaadas.lock.utils.greenDao.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class GatewayLockRecord {
    @Id
    private String deviceIdUid;
    private String deviceId;
    private String gatewayId;
    private String uid;
    private String lockName;
    private String versionType;
    private String lockNickName;
    private String nickName;
    private String uname;
    private String open_purview;
    private String open_time;
    private String open_type;

    @Generated(hash = 1073630036)
    public GatewayLockRecord(String deviceIdUid, String deviceId, String gatewayId,
            String uid, String lockName, String versionType, String lockNickName,
            String nickName, String uname, String open_purview, String open_time,
            String open_type) {
        this.deviceIdUid = deviceIdUid;
        this.deviceId = deviceId;
        this.gatewayId = gatewayId;
        this.uid = uid;
        this.lockName = lockName;
        this.versionType = versionType;
        this.lockNickName = lockNickName;
        this.nickName = nickName;
        this.uname = uname;
        this.open_purview = open_purview;
        this.open_time = open_time;
        this.open_type = open_type;
    }

    @Generated(hash = 1528532566)
    public GatewayLockRecord() {
    }

    public String getDeviceIdUid() {
        return deviceIdUid;
    }

    public void setDeviceIdUid(String deviceIdUid) {
        this.deviceIdUid = deviceIdUid;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getGatewayId() {
        return gatewayId;
    }

    public void setGatewayId(String gatewayId) {
        this.gatewayId = gatewayId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getLockName() {
        return lockName;
    }

    public void setLockName(String lockName) {
        this.lockName = lockName;
    }

    public String getVersionType() {
        return versionType;
    }

    public void setVersionType(String versionType) {
        this.versionType = versionType;
    }

    public String getLockNickName() {
        return lockNickName;
    }

    public void setLockNickName(String lockNickName) {
        this.lockNickName = lockNickName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getOpen_purview() {
        return open_purview;
    }

    public void setOpen_purview(String open_purview) {
        this.open_purview = open_purview;
    }

    public String getOpen_time() {
        return open_time;
    }

    public void setOpen_time(String open_time) {
        this.open_time = open_time;
    }

    public String getOpen_type() {
        return open_type;
    }

    public void setOpen_type(String open_type) {
        this.open_type = open_type;
    }
}
