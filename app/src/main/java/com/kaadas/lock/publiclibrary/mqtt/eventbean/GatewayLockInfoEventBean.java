package com.kaadas.lock.publiclibrary.mqtt.eventbean;

import java.io.Serializable;

/**
 * 锁信息上报
 */
public class GatewayLockInfoEventBean implements Serializable {


    /**
     * msgtype : event
     * func : gwevent
     * msgId : 67
     * devtype : kdszblock
     * deviceId : ZG01185110280
     * eventtype : info
     * eventparams : {"devetype":"lockprom","devecode":2,"eventsource":0,"userID":3,"pin":255,"userType":0,"userStatus":1,"zigBeeLocalTime":611767639,"Data":""}
     * eventcode : 2
     * gwId : GW01182510033
     * timestamp : 1558507293865
     */

    private String msgtype;
    private String func;
    private int msgId;
    private String devtype;
    private String deviceId;
    private String eventtype;
    private EventparamsBean eventparams;
    private int eventcode;
    private String gwId;
    private String timestamp;

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public String getFunc() {
        return func;
    }

    public void setFunc(String func) {
        this.func = func;
    }

    public int getMsgId() {
        return msgId;
    }

    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }

    public String getDevtype() {
        return devtype;
    }

    public void setDevtype(String devtype) {
        this.devtype = devtype;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getEventtype() {
        return eventtype;
    }

    public void setEventtype(String eventtype) {
        this.eventtype = eventtype;
    }

    public EventparamsBean getEventparams() {
        return eventparams;
    }

    public void setEventparams(EventparamsBean eventparams) {
        this.eventparams = eventparams;
    }

    public int getEventcode() {
        return eventcode;
    }

    public void setEventcode(int eventcode) {
        this.eventcode = eventcode;
    }

    public String getGwId() {
        return gwId;
    }

    public void setGwId(String gwId) {
        this.gwId = gwId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public static class EventparamsBean {
        /**
         * devetype : lockprom
         * devecode : 2
         * eventsource : 0
         * userID : 3
         * pin : 255
         * userType : 0
         * userStatus : 1
         * zigBeeLocalTime : 611767639
         * Data :
         */

        private String devetype;
        private int devecode;
        private int eventsource;
        private int userID;
        private int pin;
        private int userType;
        private int userStatus;
        private int zigBeeLocalTime;
        private String Data;

        public String getDevetype() {
            return devetype;
        }

        public void setDevetype(String devetype) {
            this.devetype = devetype;
        }

        public int getDevecode() {
            return devecode;
        }

        public void setDevecode(int devecode) {
            this.devecode = devecode;
        }

        public int getEventsource() {
            return eventsource;
        }

        public void setEventsource(int eventsource) {
            this.eventsource = eventsource;
        }

        public int getUserID() {
            return userID;
        }

        public void setUserID(int userID) {
            this.userID = userID;
        }

        public int getPin() {
            return pin;
        }

        public void setPin(int pin) {
            this.pin = pin;
        }

        public int getUserType() {
            return userType;
        }

        public void setUserType(int userType) {
            this.userType = userType;
        }

        public int getUserStatus() {
            return userStatus;
        }

        public void setUserStatus(int userStatus) {
            this.userStatus = userStatus;
        }

        public int getZigBeeLocalTime() {
            return zigBeeLocalTime;
        }

        public void setZigBeeLocalTime(int zigBeeLocalTime) {
            this.zigBeeLocalTime = zigBeeLocalTime;
        }

        public String getData() {
            return Data;
        }

        public void setData(String Data) {
            this.Data = Data;
        }
    }
}
