package com.kaadas.lock.utils.greenDao.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;

/**
 * Create By denganzhi  on 2019/5/16
 * Describe
 */
@Entity
public class CateEyeInfoBase{

    @Id(autoincrement = true)
    private Long id;


    int curBellNum;  // 铃声
    int bellVolume;  // 铃声音量
    int bellCount;   // 响铃次数
    String resolution; // 分辨率
    String deviceId;  // 序列号
    String SW;    // 软件版本
    String HW;   // 硬件版本
    String MCU;    // MCU版本
    String T200;  // T200版本
    String macaddr;
    String ipaddr;
    int wifiStrength;
    int pirEnable;   // 智能监测开关
    String pirWander;  // pir徘徊
    int sdStatus;  // sd卡状态,是否有sd卡
    String gwid;

    @Generated(hash = 1930281601)
    public CateEyeInfoBase(Long id, int curBellNum, int bellVolume, int bellCount, String resolution, String deviceId, String SW, String HW, String MCU, String T200, String macaddr, String ipaddr, int wifiStrength, int pirEnable, String pirWander, int sdStatus, String gwid) {
        this.id = id;
        this.curBellNum = curBellNum;
        this.bellVolume = bellVolume;
        this.bellCount = bellCount;
        this.resolution = resolution;
        this.deviceId = deviceId;
        this.SW = SW;
        this.HW = HW;
        this.MCU = MCU;
        this.T200 = T200;
        this.macaddr = macaddr;
        this.ipaddr = ipaddr;
        this.wifiStrength = wifiStrength;
        this.pirEnable = pirEnable;
        this.pirWander = pirWander;
        this.sdStatus = sdStatus;
        this.gwid = gwid;
    }

    public CateEyeInfoBase() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getCurBellNum() {
        return curBellNum;
    }

    public void setCurBellNum(int curBellNum) {
        this.curBellNum = curBellNum;
    }

    public int getBellVolume() {
        return bellVolume;
    }

    public void setBellVolume(int bellVolume) {
        this.bellVolume = bellVolume;
    }

    public int getBellCount() {
        return bellCount;
    }

    public void setBellCount(int bellCount) {
        this.bellCount = bellCount;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getSW() {
        return SW;
    }

    public void setSW(String SW) {
        this.SW = SW;
    }

    public String getHW() {
        return HW;
    }

    public void setHW(String HW) {
        this.HW = HW;
    }

    public String getMCU() {
        return MCU;
    }

    public void setMCU(String MCU) {
        this.MCU = MCU;
    }

    public String getT200() {
        return T200;
    }

    public void setT200(String t200) {
        T200 = t200;
    }

    public String getMacaddr() {
        return macaddr;
    }

    public void setMacaddr(String macaddr) {
        this.macaddr = macaddr;
    }

    public String getIpaddr() {
        return ipaddr;
    }

    public void setIpaddr(String ipaddr) {
        this.ipaddr = ipaddr;
    }

    public int getWifiStrength() {
        return wifiStrength;
    }

    public void setWifiStrength(int wifiStrength) {
        this.wifiStrength = wifiStrength;
    }

    public int getPirEnable() {
        return pirEnable;
    }

    public void setPirEnable(int pirEnable) {
        this.pirEnable = pirEnable;
    }

    public String getPirWander() {
        return pirWander;
    }

    public void setPirWander(String pirWander) {
        this.pirWander = pirWander;
    }

    public int getSdStatus() {
        return sdStatus;
    }

    public void setSdStatus(int sdStatus) {
        this.sdStatus = sdStatus;
    }

    public String getGwid() {
        return gwid;
    }

    public void setGwid(String gwid) {
        this.gwid = gwid;
    }

    @Override
    public String toString() {
        return "CateEyeInfoBase{" +
                "id=" + id +
                ", curBellNum=" + curBellNum +
                ", bellVolume=" + bellVolume +
                ", bellCount=" + bellCount +
                ", resolution='" + resolution + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", SW='" + SW + '\'' +
                ", HW='" + HW + '\'' +
                ", MCU='" + MCU + '\'' +
                ", T200='" + T200 + '\'' +
                ", macaddr='" + macaddr + '\'' +
                ", ipaddr='" + ipaddr + '\'' +
                ", wifiStrength=" + wifiStrength +
                ", pirEnable=" + pirEnable +
                ", pirWander='" + pirWander + '\'' +
                ", sdStatus=" + sdStatus +
                '}';
    }
}
