package com.kaadas.lock.publiclibrary.mqtt.publishresultbean;

import java.io.Serializable;

public class WifiLockVideoBindBean implements Serializable {
    /**
     * 	"msgtype": "event",
     * 	"func": "wfevent",
     * 	"msgId": 1,
     * 	"devtype": "xmkdswflock",
     * 	"eventtype": "bindInfoNotify",
     * 	"wfId": "KV51203710173",
     * 	"timestamp": "1600686106",
     * 	"eventparams": {
     * 		"uid": "5f5a0002c36a8525eb648432",
     * 		"device_sn": "010000000020701289",
     * 		"mac": "b0:b3:53:77:a0:36",
     * 		"device_did": "AYIOT-000000-XJXXE",
     * 		"p2p_password": "rGxk7j5J",
     * 		"randomCode": "1234"
     *        },
     * 	"userId": "5f5a0002c36a8525eb648432"
     */

   private String  msgtype;
   private String func;
   private int msgId;
   private String devtype;
   private String eventtype;
   private String wfId;
   private String timestamp;
   private String userId;
   private VideoLockBind  eventparams;

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

    public String getEventtype() {
        return eventtype;
    }

    public void setEventtype(String eventtype) {
        this.eventtype = eventtype;
    }

    public String getWfId() {
        return wfId;
    }

    public void setWfId(String wfId) {
        this.wfId = wfId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public VideoLockBind getEventparams() {
        return eventparams;
    }

    public void setEventparams(VideoLockBind eventparams) {
        this.eventparams = eventparams;
    }

    public class VideoLockBind implements Serializable{
        /**
         * "eventparams":{
         * "uid":"5cad4509dc938989e2f542c8",
         * "randomCode":"50",
         * "device_sn":"010000000020500020",
         * "device_type":"1",
         * "device_model":"model",
         * "mac":"bfjakaua898f9",
         * "device_did":"did",
         * "p2p_password":"p2p_password"
         * },
         */

        private String uid;
        private String randomCode;
        private String device_sn;
//        private String device_type;
//        private String device_model;
        private String mac;
        private String device_did;
        private String p2p_password;

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getRandomCode() {
            return randomCode;
        }

        public void setRandomCode(String randomCode) {
            this.randomCode = randomCode;
        }

        public String getDevice_sn() {
            return device_sn;
        }

        public void setDevice_sn(String device_sn) {
            this.device_sn = device_sn;
        }

        public String getMac() {
            return mac;
        }

        public void setMac(String mac) {
            this.mac = mac;
        }

        public String getDevice_did() {
            return device_did;
        }

        public void setDevice_did(String device_did) {
            this.device_did = device_did;
        }

        public String getP2p_password() {
            return p2p_password;
        }

        public void setP2p_password(String p2p_password) {
            this.p2p_password = p2p_password;
        }

        @Override
        public String toString() {
            return "WifiLockVideoBindBean{" +
                    "uid='" + uid + '\'' +
                    ", randomCode='" + randomCode + '\'' +
                    ", device_sn='" + device_sn + '\'' +
                    ", mac='" + mac + '\'' +
                    ", device_did='" + device_did + '\'' +
                    ", p2p_password='" + p2p_password + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "WifiLockVideoBindBean{" +
                "msgtype='" + msgtype + '\'' +
                ", func='" + func + '\'' +
                ", msgId=" + msgId +
                ", devtype='" + devtype + '\'' +
                ", eventtype='" + eventtype + '\'' +
                ", wfId='" + wfId + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", userId='" + userId + '\'' +
                ", eventparams=" + eventparams +
                '}';
    }
}
