package com.kaadas.lock.publiclibrary.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class SingleFireSwitchInfo implements Serializable {

    /**
     * switchEn     ：开关使能
     * mac          : mac地址
     * switchArray  ：开关键位功能数组
     * switchBind   ：绑定时间
     * total        : 开关总数
     */
    private int switchEn;
    private int total;
    private String mac;
    @SerializedName("createTime")
    private long switchBind;

    @SerializedName("switchArray")
    private List<SwitchNumberBean> switchNumber;

    public SingleFireSwitchInfo(int switchEn,String mac,long switchBind,List<SwitchNumberBean> switchNumber,int total) {
        this.switchEn = switchEn;
        this.mac = mac;
        this.switchBind = switchBind;
        this.switchNumber = switchNumber;
        this.total = total;
    }


    public SingleFireSwitchInfo() {

    }

    public int getSwitchEn() {
        return switchEn;
    }

    public void setSwitchEn(int switchEn) {
        this.switchEn = switchEn;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String macaddr) {
        this.mac = mac;
    }

    public long getSwitchBind() {
        return switchBind;
    }

    public void setSwitchBind(long switchBind) {
        this.switchBind = switchBind;
    }


    @Override
    public String toString() {
        return "{" +
                "switchEn='" + switchEn + '\'' +
                ", mac='" + mac + '\'' +
                ", total='" + total + '\'' +
                ", createTime='" + switchBind + '\'' +
                ", switchArray=" + switchNumber +
                '}';
    }


    public List<SwitchNumberBean> getSwitchNumber() {
    return switchNumber;
    }

    public void setSwitchNumber(List<SwitchNumberBean> switchNumber) {
        this.switchNumber = switchNumber;
    }

}
