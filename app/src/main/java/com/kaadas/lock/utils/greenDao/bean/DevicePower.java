package com.kaadas.lock.utils.greenDao.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

//电量缓存
@Entity
public class DevicePower {

    @Id
    private String deviceIdUid;

    private String deviceSN;

    private int power;

    @Generated(hash = 1120809814)
    public DevicePower(String deviceIdUid, String deviceSN, int power) {
        this.deviceIdUid = deviceIdUid;
        this.deviceSN = deviceSN;
        this.power = power;
    }

    @Generated(hash = 606954897)
    public DevicePower() {
    }

    public String getDeviceIdUid() {
        return deviceIdUid;
    }

    public void setDeviceIdUid(String deviceIdUid) {
        this.deviceIdUid = deviceIdUid;
    }

    public String getDeviceSN() {
        return deviceSN;
    }

    public void setDeviceSN(String deviceSN) {
        this.deviceSN = deviceSN;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }
}
