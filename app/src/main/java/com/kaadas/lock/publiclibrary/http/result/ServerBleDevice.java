package com.kaadas.lock.publiclibrary.http.result;

import android.text.TextUtils;

import com.kaadas.lock.utils.LogUtils;

import java.io.Serializable;


/**
 * Create By lxj  on 2019/3/3
 * Describe
 */
public class ServerBleDevice implements Serializable {
    private String _id;
    private String lockName;//设备唯一编号
    private String lockNickName;
    private String macLock;
    private String open_purview;
    private String is_admin;
    private String center_latitude;
    private String center_longitude;
    private String circle_radius;
    private String auto_lock;
    private String password1;
    private String password2;
    private String model;
    private long createTime;
    private String bleVersion;
    private String softwareVersion;
    private String deviceSN;
    /**
     * peripheralId : 5c70ac053c554639ea931111
     * functionSet : 01
     */

    private String peripheralId;
    private String functionSet;

    public String getBleVersion() {
        return bleVersion;
    }

    public void setBleVersion(String bleVersion) {
        this.bleVersion = bleVersion;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getLockName() {
        return lockName;
    }

    public void setLockName(String lockName) {
        this.lockName = lockName;
    }

    public String getLockNickName() {
        return lockNickName;
    }

    public void setLockNickName(String lockNickName) {
        this.lockNickName = lockNickName;
    }

    public String getMacLock() {
        return macLock;
    }

    public void setMacLock(String macLock) {
        this.macLock = macLock;
    }

    public String getOpen_purview() {
        return open_purview;
    }

    public void setOpen_purview(String open_purview) {
        this.open_purview = open_purview;
    }

    public String getIs_admin() {
        return is_admin;
    }

    public void setIs_admin(String is_admin) {
        this.is_admin = is_admin;
    }

    public String getCenter_latitude() {
        return center_latitude;
    }

    public void setCenter_latitude(String center_latitude) {
        this.center_latitude = center_latitude;
    }

    public String getCenter_longitude() {
        return center_longitude;
    }

    public void setCenter_longitude(String center_longitude) {
        this.center_longitude = center_longitude;
    }

    public String getCircle_radius() {
        return circle_radius;
    }

    public void setCircle_radius(String circle_radius) {
        this.circle_radius = circle_radius;
    }

    public String getAuto_lock() {
        return auto_lock;
    }

    public void setAuto_lock(String auto_lock) {
        this.auto_lock = auto_lock;
    }

    public String getPassword1() {
        return password1;
    }

    public void setPassword1(String password1) {
        this.password1 = password1;
    }

    public String getPassword2() {
        return password2;
    }

    public void setPassword2(String password2) {
        this.password2 = password2;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSoftwareVersion() {
        return softwareVersion;
    }

    public void setSoftwareVersion(String softwareVersion) {
        this.softwareVersion = softwareVersion;
    }

    public String getDeviceSN() {
        return deviceSN;
    }

    public void setDeviceSN(String deviceSN) {
        this.deviceSN = deviceSN;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "ServerBleDevice{" +
                "_id='" + _id + '\'' +
                ", lockName='" + lockName + '\'' +
                ", lockNickName='" + lockNickName + '\'' +
                ", macLock='" + macLock + '\'' +
                ", open_purview='" + open_purview + '\'' +
                ", is_admin='" + is_admin + '\'' +
                ", center_latitude='" + center_latitude + '\'' +
                ", center_longitude='" + center_longitude + '\'' +
                ", circle_radius='" + circle_radius + '\'' +
                ", auto_lock='" + auto_lock + '\'' +
                ", password1='" + password1 + '\'' +
                ", password2='" + password2 + '\'' +
                ", model='" + model + '\'' +
                ", createTime=" + createTime +
                ", bleVersion='" + bleVersion + '\'' +
                ", softwareVersion='" + softwareVersion + '\'' +
                ", deviceSN='" + deviceSN + '\'' +
                '}';
    }

    public String getPeripheralId() {
        return peripheralId;
    }

    public void setPeripheralId(String peripheralId) {
        this.peripheralId = peripheralId;
    }
//
//     if (lockType.startsWith("V6") ||
//             lockType.startsWith("V7") ||
//             lockType.startsWith("S100") ||
//             lockType.startsWith("K9")||
//             lockType.startsWith("S6")) {



    public boolean functionIsEmpty(){
        return TextUtils.isEmpty(functionSet);
    }
    public String getFunctionSet() {
        LogUtils.e("");

        if (TextUtils.isEmpty(functionSet)) {
            if (!"3".equals(bleVersion)){ //如果蓝牙版本号不是3
                return ""+0;
            }
            if (TextUtils.isEmpty(model)) {
                return "" + 0x31;
            } else {
                if (model.startsWith("V6") || model.startsWith("S100")) {
                    return "" + 0x20;
                } else if (model.startsWith("S8")) {
                    return "" + 0x32;
                } else if (model.startsWith("V7")) {
                    return "" + 0x20;
                } else if (model.startsWith("K9")) {
                    return "" + 0x01;
                }else if (model.startsWith("S6")) {
                    return "" + 0x20;
                }else {
                    return "" + 0x31;
                }
            }
        }
        return functionSet;
    }

    public void setFunctionSet(String functionSet) {
        this.functionSet = functionSet;
    }
}
