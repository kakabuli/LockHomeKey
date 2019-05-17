package com.kaadas.lock.utils.greenDao.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Create By denganzhi  on 2019/5/17
 * Describe
 */
@Entity
public class PirDefault {

    @Id(autoincrement = true)
    private Long id;
    private String deviceId;
    private int periodtime;
    private int threshold;
    private int protecttime;
    private int ust;
    private int maxprohibition;
    private int enable;

@Generated(hash = 1307158762)
public PirDefault(Long id, String deviceId, int periodtime, int threshold, int protecttime, int ust, int maxprohibition, int enable) {
    this.id = id;
    this.deviceId = deviceId;
    this.periodtime = periodtime;
    this.threshold = threshold;
    this.protecttime = protecttime;
    this.ust = ust;
    this.maxprohibition = maxprohibition;
    this.enable = enable;
}

    public PirDefault() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public int getPeriodtime() {
        return periodtime;
    }

    public void setPeriodtime(int periodtime) {
        this.periodtime = periodtime;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public int getProtecttime() {
        return protecttime;
    }

    public void setProtecttime(int protecttime) {
        this.protecttime = protecttime;
    }

    public int getUst() {
        return ust;
    }

    public void setUst(int ust) {
        this.ust = ust;
    }

    public int getMaxprohibition() {
        return maxprohibition;
    }

    public void setMaxprohibition(int maxprohibition) {
        this.maxprohibition = maxprohibition;
    }

    public int getEnable() {
        return enable;
    }

    public void setEnable(int enable) {
        this.enable = enable;
    }

    @Override
    public String toString() {
        return "PirDefault{" +
                "id=" + id +
                ", deviceId='" + deviceId + '\'' +
                ", periodtime=" + periodtime +
                ", threshold=" + threshold +
                ", protecttime=" + protecttime +
                ", ust=" + ust +
                ", maxprohibition=" + maxprohibition +
                ", enable=" + enable +
                '}';
    }
}
