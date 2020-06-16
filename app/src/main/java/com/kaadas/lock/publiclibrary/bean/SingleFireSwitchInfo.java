package com.kaadas.lock.publiclibrary.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class SingleFireSwitchInfo implements Serializable {

    /**
     * switchEn     ：开关使能
     * macaddr      : mac地址
     * switchArray   ：开关键位功能数组
     * switchBind   ：绑定时间
     */
    private int switchEn;
    private String macaddr;
    private long switchBind;

    @SerializedName("switchArray")
    private List<SwitchNumberBean> switchNumber;

    public SingleFireSwitchInfo(int switchEn,String macaddr,long switchBind,List<SwitchNumberBean> switchNumber) {
        this.switchEn = switchEn;
        this.macaddr = macaddr;
        this.switchBind = switchBind;
        this.switchNumber = switchNumber;
    }

    public SingleFireSwitchInfo() {

    }

    public int getSwitchEn() {
        return switchEn;
    }

    public void setSwitchEn(int switchEn) {
        this.switchEn = switchEn;
    }

    public String getMacaddr() {
        return macaddr;
    }

    public void setMacaddr(String macaddr) {
        this.macaddr = macaddr;
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
                ", macaddr='" + macaddr + '\'' +
                ", switchBind='" + switchBind + '\'' +
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
