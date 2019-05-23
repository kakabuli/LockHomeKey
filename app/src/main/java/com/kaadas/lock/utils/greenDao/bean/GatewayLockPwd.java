package com.kaadas.lock.utils.greenDao.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;



@Entity
public class GatewayLockPwd  {

    @Id(autoincrement = true)
    private Long id;
    //用户id
    private String uid;
    //网关id
    private String gatewayId;

    //设备id
    private String deviceId;

    //编号
    private String num;

    //周期性 1代表永久性密码，2代表临时密码，3代表胁迫密码
    private int time;

    //昵称
    private String name;

    //状态
    private int status;


    @Generated(hash = 439063795)
    public GatewayLockPwd(Long id, String uid, String gatewayId, String deviceId,
            String num, int time, String name, int status) {
        this.id = id;
        this.uid = uid;
        this.gatewayId = gatewayId;
        this.deviceId = deviceId;
        this.num = num;
        this.time = time;
        this.name = name;
        this.status = status;
    }

    @Generated(hash = 776213925)
    public GatewayLockPwd() {
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Long getId() {
        return id;
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

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
