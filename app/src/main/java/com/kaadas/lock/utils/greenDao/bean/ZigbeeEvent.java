package com.kaadas.lock.utils.greenDao.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Transient;

@Entity
public class ZigbeeEvent {
    @Id(autoincrement = true)
    private Long id;

    private int deviceType;  //设备类型

    private String deviceId;  //设备Id

    private int eventType; //时间类型

    private long eventTime; //事件触发的时间
    @Transient
    public final static int DEVICE_CAT_EYE = 1; //猫眼
    @Transient
    public final static int EVENT_PIR = 101;  //pir触发
    @Transient
    public final static int EVENT_HEAD_LOST = 102; //猫头被拔
    @Transient
    public final static int EVENT_DOOR_BELL = 103; //响铃
    @Transient
    public final static int EVENT_LOW_POWER = 104; //低电量
    @Transient
    public final static int EVENT_HOST_LOST = 105; //机身被拔
    @Transient
    public final static int DEVICE_GATEWAY = 2;  //网关


    public final static int DEVICE_GW_LOCK = 3;   //网关锁

    public final static int DEVICE_BLE_LOCK = 4;   //蓝牙锁


    @Generated(hash = 1783476231)
    public ZigbeeEvent(Long id, int deviceType, String deviceId, int eventType,
            long eventTime) {
        this.id = id;
        this.deviceType = deviceType;
        this.deviceId = deviceId;
        this.eventType = eventType;
        this.eventTime = eventTime;
    }

    @Generated(hash = 1051241011)
    public ZigbeeEvent() {
    }

  
    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    public long getEventTime() {
        return eventTime;
    }

    public void setEventTime(long eventTime) {
        this.eventTime = eventTime;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getDeviceType() {
        return this.deviceType;
    }
}
