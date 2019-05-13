package com.kaadas.lock.publiclibrary.mqtt.eventbean;

import java.io.Serializable;

/**
 * 网关锁报警事件
 */
public class GatewayLockAlarmEventBean implements Serializable {


    /**
     * deviceId :  devuuid
     * devtype : kdszblock
     * eventcode : 2
     * eventparams : {"alarmCode":4,"clusterID":257}
     * eventtype : alarm
     * func : gwevent
     * gwId :  gwuuid
     * msgId : 4
     * msgtype : event
     * timestamp : 1541468973342
     */

    private String deviceId;
    private String devtype;
    private int eventcode;
    private EventparamsBean eventparams;
    private String eventtype;
    private String func;
    private String gwId;
    private int msgId;
    private String msgtype;
    private String timestamp;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDevtype() {
        return devtype;
    }

    public void setDevtype(String devtype) {
        this.devtype = devtype;
    }

    public int getEventcode() {
        return eventcode;
    }

    public void setEventcode(int eventcode) {
        this.eventcode = eventcode;
    }

    public EventparamsBean getEventparams() {
        return eventparams;
    }

    public void setEventparams(EventparamsBean eventparams) {
        this.eventparams = eventparams;
    }

    public String getEventtype() {
        return eventtype;
    }

    public void setEventtype(String eventtype) {
        this.eventtype = eventtype;
    }

    public String getFunc() {
        return func;
    }

    public void setFunc(String func) {
        this.func = func;
    }

    public String getGwId() {
        return gwId;
    }

    public void setGwId(String gwId) {
        this.gwId = gwId;
    }

    public int getMsgId() {
        return msgId;
    }

    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public static class EventparamsBean {
        /**
         * alarmCode : 4
         * clusterID : 257
         */

        private int alarmCode;
        private int clusterID;

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
    }
}
