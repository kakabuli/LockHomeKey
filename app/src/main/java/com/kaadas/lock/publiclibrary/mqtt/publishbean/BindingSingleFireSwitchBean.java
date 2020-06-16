package com.kaadas.lock.publiclibrary.mqtt.publishbean;

import com.google.gson.annotations.SerializedName;
import com.kaadas.lock.publiclibrary.bean.SingleFireSwitchInfo;

import java.io.Serializable;

public class BindingSingleFireSwitchBean implements Serializable {

    /**
     *  "wifiSN": "WF132231004",
     *   "uid": "5c4fe492dc93897aa7d8600b",
     *   "lockNickname": "wode",
     *   "switch": SingleFireSwitchInfo
     *
     *
     */

    private String wifiSN;
    private String uid;
    private String lockNickname;
    @SerializedName("switch")
    private SingleFireSwitchInfo params;


    public String getWifiSN() {
        return wifiSN;
    }

    public void setWifiSN(String wifiSN) {
        this.wifiSN = wifiSN;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getLockNickname() {
        return lockNickname;
    }

    public void setLockNickname(String lockNickname) {
        this.lockNickname = lockNickname;
    }


    public SingleFireSwitchInfo getParams() {
        return params;
    }

    public void setParams(SingleFireSwitchInfo params) {
        this.params = params;
    }


    public BindingSingleFireSwitchBean(String wifiSN, String uid, String lockNickname, SingleFireSwitchInfo params) {
        this.wifiSN = wifiSN;
        this.uid = uid;
        this.lockNickname = lockNickname;
        this.params = params;
    }

    public BindingSingleFireSwitchBean() {
    }
}
