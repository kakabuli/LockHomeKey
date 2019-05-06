package com.kaadas.lock.publiclibrary.mqtt.eventbean;

import java.io.Serializable;

public class DeleteDeviceLockBean implements Serializable {


    /**
     * deviceId : devuuid
     * devtype : devtype
     * eventcode : 1
     * eventparams : {" macaddr":"XX:XX:XX:XX:XX:XX","SW":"V1.03.111","device_type":"zigbee","event_str":"delete"}
     * eventtype : alarm
     * func : gwevent
     * gwId :
     * msgid : msgid
     * msgtype : event
     * timestamp : 134211111111
     */

    private String deviceId;
    private String devtype;
    private int eventcode;
    private EventparamsBean eventparams;
    private String eventtype;
    private String func;
    private String gwId;
    private String msgid;
    private String msgtype;
    private String timestamp;





    public static class EventparamsBean {
        /**
         *  macaddr : XX:XX:XX:XX:XX:XX
         * SW : V1.03.111
         * device_type : zigbee
         * event_str : delete
         */

        private String macaddr;
        private String SW;
        private String device_type;
        private String event_str;


        public String getMacaddr() {
            return macaddr;
        }

        public void setMacaddr(String macaddr) {
            this.macaddr = macaddr;
        }

        public String getSW() {
            return SW;
        }

        public void setSW(String SW) {
            this.SW = SW;
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
    }

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

    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
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

    public DeleteDeviceLockBean(String deviceId, String devtype, int eventcode, EventparamsBean eventparams, String eventtype, String func, String gwId, String msgid, String msgtype, String timestamp) {
        this.deviceId = deviceId;
        this.devtype = devtype;
        this.eventcode = eventcode;
        this.eventparams = eventparams;
        this.eventtype = eventtype;
        this.func = func;
        this.gwId = gwId;
        this.msgid = msgid;
        this.msgtype = msgtype;
        this.timestamp = timestamp;
    }

    public DeleteDeviceLockBean() {
    }
}
