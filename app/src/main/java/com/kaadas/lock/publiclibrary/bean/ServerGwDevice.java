package com.kaadas.lock.publiclibrary.bean;

public class ServerGwDevice {
    /**
     * SW : orangecat-1.3.7
     * deviceId : 0C9A42B78CF5
     * device_type : kdscateye
     * event_str : offline
     * ipaddr : 192.168.168.235
     * macaddr : 0C:9A:42:B7:8C:F5
     * nickName : 0C9A42B78CF5
     * time : 2019-04-25 11:24:43.548
     */

    private String SW;
    private String deviceId;
    private String device_type;
    private String event_str;
    private String ipaddr;
    private String macaddr;
    private String nickName;
    private String time;

    public String getSW() {
        return SW;
    }

    public void setSW(String SW) {
        this.SW = SW;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDevice_type() {
        return device_type;
    }

    public void setDevice_type(String device_type) {
        this.device_type = device_type;
    }

    public String getEvent_str() {
        return event_str;
    }

    public void setEvent_str(String event_str) {
        this.event_str = event_str;
    }

    public String getIpaddr() {
        return ipaddr;
    }

    public void setIpaddr(String ipaddr) {
        this.ipaddr = ipaddr;
    }

    public String getMacaddr() {
        return macaddr;
    }

    public void setMacaddr(String macaddr) {
        this.macaddr = macaddr;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
