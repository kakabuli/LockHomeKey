package com.kaadas.lock.publiclibrary.http.result;

import java.io.Serializable;


/**
 * Create By lxj  on 2019/3/3
 * Describe
 */
public class ServerBleDevice implements Serializable {
    private String _id;
    private String device_name;//设备唯一编号
    private String device_nickname;
    private String devmac;
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
    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    public String getDevice_nickname() {
        return device_nickname;
    }

    public void setDevice_nickname(String device_nickname) {
        this.device_nickname = device_nickname;
    }

    public String getDevmac() {
        return devmac;
    }

    public void setDevmac(String devmac) {
        this.devmac = devmac;
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
        if (model.startsWith("X5")){
            model="X5";
        }else{
            model="T5";
        }
        this.model = model;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "ServerDevices{" +
                "_id='" + _id + '\'' +
                ", device_name='" + device_name + '\'' +
                ", device_nickname='" + device_nickname + '\'' +
                ", devmac='" + devmac + '\'' +
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
                '}';
    }
}
