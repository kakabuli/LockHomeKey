package com.kaadas.lock.publiclibrary.bean;

public class WifiLockOperationRecord {
    /**
     * _id : 5dde33754d27d6da12f51637
     * time : 1541468973342
     * type : 1
     * wifiSN : WF132231004
     * bleSN : BT01192910010
     * pwdType : 4
     * pwdNum : 2
     * createTime : 1576058537426
     * appId : 1
     * uid : 5c4fe492dc93897aa7d8600b
     * uname : 8618954359822
     * userNickname : ahaha
     */

    private String _id;
    private long time;
    private int type;
    private String wifiSN;
    private String bleSN;
    private int pwdType;
    private int pwdNum;
    private long createTime;
    private int appId;
    private String uid;
    private String uname;
    private String userNickname;

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

    public String getBleSN() {
        return bleSN;
    }

    public void setBleSN(String bleSN) {
        this.bleSN = bleSN;
    }

    public int getPwdType() {
        return pwdType;
    }

    public void setPwdType(int pwdType) {
        this.pwdType = pwdType;
    }

    public int getPwdNum() {
        return pwdNum;
    }

    public void setPwdNum(int pwdNum) {
        this.pwdNum = pwdNum;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }
}