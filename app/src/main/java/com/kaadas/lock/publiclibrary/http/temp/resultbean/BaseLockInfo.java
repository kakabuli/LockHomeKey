package com.kaadas.lock.publiclibrary.http.temp.resultbean;

import java.io.Serializable;

/**
 * Create By lxj  on 2019/1/7
 * Describe    从服务器获取的设备信息
 */
public class BaseLockInfo implements Serializable {


    /**
     * _id : 5c394c5435736f6e8f9b9bfa
     * auto_lock : 0
     * center_latitude : 0
     * center_longitude : 0
     * circle_radius : 0
     * device_name : KDSA434F1CC1568
     * device_nickname : KDSA434F1CC1568
     * devmac : A4:34:F1:CC:15:68
     * is_admin : 1
     * model :
     * open_purview : 3
     * password1 : f564967d578934ab33bc9496
     * password2 : 5fc5b7ad
     */

    private String _id;
    private String auto_lock;
    private String center_latitude;
    private String center_longitude;
    private String circle_radius;
    private String device_name;
    private String device_nickname;
    private String devmac;
    private String is_admin;
    private String model;
    private String open_purview;
    private String password1;
    private String password2;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getAuto_lock() {
        return auto_lock;
    }

    public void setAuto_lock(String auto_lock) {
        this.auto_lock = auto_lock;
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

    public String getIs_admin() {
        return is_admin;
    }

    public void setIs_admin(String is_admin) {
        this.is_admin = is_admin;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getOpen_purview() {
        return open_purview;
    }

    public void setOpen_purview(String open_purview) {
        this.open_purview = open_purview;
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


    @Override
    public String toString() {
        return "BaseLockInfo{" +
                "_id='" + _id + '\'' +
                ", auto_lock='" + auto_lock + '\'' +
                ", center_latitude='" + center_latitude + '\'' +
                ", center_longitude='" + center_longitude + '\'' +
                ", circle_radius='" + circle_radius + '\'' +
                ", device_name='" + device_name + '\'' +
                ", device_nickname='" + device_nickname + '\'' +
                ", devmac='" + devmac + '\'' +
                ", is_admin='" + is_admin + '\'' +
                ", model='" + model + '\'' +
                ", open_purview='" + open_purview + '\'' +
                ", password1='" + password1 + '\'' +
                ", password2='" + password2 + '\'' +
                '}';
    }
}
