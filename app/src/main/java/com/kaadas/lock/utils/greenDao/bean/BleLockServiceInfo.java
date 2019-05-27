package com.kaadas.lock.utils.greenDao.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class BleLockServiceInfo {

    /**
     * _id : 5c8f5563dc938989e2f5429d
     * lockName : BT123456
     * lockNickName : BT123456
     * macLock : 123456
     * open_purview : 3
     * is_admin : 1
     * center_latitude : 0
     * center_longitude : 0
     * circle_radius : 0
     * auto_lock : 0
     * password1 : 123456
     * password2 : 654321
     * model :
     */
    @Id(autoincrement = true)
    private Long id;
    private String lockName;
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
    private String uid;



    @Generated(hash = 566262832)
    public BleLockServiceInfo(Long id, String lockName, String lockNickName,
            String macLock, String open_purview, String is_admin,
            String center_latitude, String center_longitude, String circle_radius,
            String auto_lock, String password1, String password2, String model,
            String uid) {
        this.id = id;
        this.lockName = lockName;
        this.lockNickName = lockNickName;
        this.macLock = macLock;
        this.open_purview = open_purview;
        this.is_admin = is_admin;
        this.center_latitude = center_latitude;
        this.center_longitude = center_longitude;
        this.circle_radius = circle_radius;
        this.auto_lock = auto_lock;
        this.password1 = password1;
        this.password2 = password2;
        this.model = model;
        this.uid = uid;
    }

    @Generated(hash = 2041412345)
    public BleLockServiceInfo() {
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
