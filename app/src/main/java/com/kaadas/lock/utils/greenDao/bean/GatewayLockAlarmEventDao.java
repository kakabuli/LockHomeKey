package com.kaadas.lock.utils.greenDao.bean;

import com.kaadas.lock.publiclibrary.mqtt.PowerResultBean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class GatewayLockAlarmEventDao {
    @Id(autoincrement = true)
    private Long id;
    private String gatewayId; //网关id
    private String deviceId; //设备id
    private String timeStamp; //上报的时间
    private String devtype; //设备类型
    private int alarmCode; //报警代码
    private int clusterID; //  257 代表锁的信息;1 代表电量信息
    private int eventcode;



    @Generated(hash = 1149457319)
    public GatewayLockAlarmEventDao(Long id, String gatewayId, String deviceId,
            String timeStamp, String devtype, int alarmCode, int clusterID,
            int eventcode) {
        this.id = id;
        this.gatewayId = gatewayId;
        this.deviceId = deviceId;
        this.timeStamp = timeStamp;
        this.devtype = devtype;
        this.alarmCode = alarmCode;
        this.clusterID = clusterID;
        this.eventcode = eventcode;
    }

    @Generated(hash = 590752578)
    public GatewayLockAlarmEventDao() {
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGatewayId() {
        return gatewayId;
    }

    public void setGatewayId(String gatewayId) {
        this.gatewayId = gatewayId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getAlarmCode() {
        return alarmCode;
    }

    public void setAlarmCode(int alarmCode) {
        this.alarmCode = alarmCode;
    }

    public int getClusterID() {
        return clusterID;
    }

    public void setClusterID(int clusterID) {
        this.clusterID = clusterID;
    }

    public int getEventcode() {
        return eventcode;
    }

    public void setEventcode(int eventcode) {
        this.eventcode = eventcode;
    }

    public String getDevtype() {
        return devtype;
    }

    public void setDevtype(String devtype) {
        this.devtype = devtype;
    }
}
