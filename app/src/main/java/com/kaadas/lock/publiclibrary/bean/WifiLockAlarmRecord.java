package com.kaadas.lock.publiclibrary.bean;

public class WifiLockAlarmRecord {
    /**
     * _id : 5df0abf54d27d6da12fb4c71
     * time : 1541468973342
     * type : 4
     * wifiSN : WF132231004
     * createTime : 1576054908866
     */

    public String _id;
    private long time;
    private int type;
    private String wifiSN;
    private long createTime;

    public WifiLockAlarmRecord(){}

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getWifiSN() {
        return wifiSN;
    }

    public void setWifiSN(String wifiSN) {
        this.wifiSN = wifiSN;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
}