package com.kaadas.lock.bean.deviceAdd;

public class AddSelectDeviceAddItemBean {
    private int imageId;
    private String title;
    private int type; //区分是猫眼，网关，锁----1是网关，2是猫眼，3是锁
    private String deviceType;//区分相同的锁的类型---
    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }
}


