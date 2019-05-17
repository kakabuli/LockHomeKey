package com.kaadas.lock.publiclibrary.mqtt.publishresultbean;

import java.io.Serializable;
import java.util.List;

public class BindGatewayResultBean implements Serializable {


    /**
     * msgId : 456321
     * msgtype : response
     * userId :
     * gwId :
     * deviceId :
     * func : bindGatewayByUser
     * code : 200
     * msg : 成功
     * timestamp : 1557556270390
     * data : {"_id":"5cd66bb5d98d1d2f0cd096dc","model":"6010","deviceList":[{"devetype":"lockop","event_str":"online","devecode":2,"eventsource":2,"userID":103,"pin":255,"zigBeeLocalTime":-1719285923,"Data":"","deviceId":"ZGCCXXYY58266","device_type":"kdszblock","time":"2019-05-06 16:26:51.110","macaddr":null,"SW":null,"ipaddr":""}],"uid":"5c4fe492dc93897aa7d8600b","adminuid":"5c4fe492dc93897aa7d8600b","adminName":"8618954359822","adminNickname":"8618954359822"}
     */

    private int msgId;
    private String msgtype;
    private String userId;
    private String gwId;
    private String deviceId;
    private String func;
    private String code;
    private String msg;
    private String timestamp;
    private DataBean data;

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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGwId() {
        return gwId;
    }

    public void setGwId(String gwId) {
        this.gwId = gwId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getFunc() {
        return func;
    }

    public void setFunc(String func) {
        this.func = func;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * _id : 5cd66bb5d98d1d2f0cd096dc
         * model : 6010
         * deviceList : [{"devetype":"lockop","event_str":"online","devecode":2,"eventsource":2,"userID":103,"pin":255,"zigBeeLocalTime":-1719285923,"Data":"","deviceId":"ZGCCXXYY58266","device_type":"kdszblock","time":"2019-05-06 16:26:51.110","macaddr":null,"SW":null,"ipaddr":""}]
         * uid : 5c4fe492dc93897aa7d8600b
         * adminuid : 5c4fe492dc93897aa7d8600b
         * adminName : 8618954359822
         * adminNickname : 8618954359822
         */

        private String _id;
        private String model;
        private String uid;
        private String adminuid;
        private String adminName;
        private String adminNickname;
        private int  meBindState;
        private List<DeviceListBean> deviceList;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
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

        public int getMeBindState() {
            return meBindState;
        }

        public void setMeBindState(int meBindState) {
            this.meBindState = meBindState;
        }

        public String getAdminuid() {
            return adminuid;
        }

        public void setAdminuid(String adminuid) {
            this.adminuid = adminuid;
        }

        public String getAdminName() {
            return adminName;
        }

        public void setAdminName(String adminName) {
            this.adminName = adminName;
        }

        public String getAdminNickname() {
            return adminNickname;
        }

        public void setAdminNickname(String adminNickname) {
            this.adminNickname = adminNickname;
        }

        public List<DeviceListBean> getDeviceList() {
            return deviceList;
        }

        public void setDeviceList(List<DeviceListBean> deviceList) {
            this.deviceList = deviceList;
        }

        public static class DeviceListBean {
            /**
             * devetype : lockop
             * event_str : online
             * devecode : 2
             * eventsource : 2
             * userID : 103
             * pin : 255
             * zigBeeLocalTime : -1719285923
             * Data :
             * deviceId : ZGCCXXYY58266
             * device_type : kdszblock
             * time : 2019-05-06 16:26:51.110
             * macaddr : null
             * SW : null
             * ipaddr :
             */

            private String devetype;
            private String event_str;
            private int devecode;
            private int eventsource;
            private int userID;
            private int pin;
            private int zigBeeLocalTime;
            private String Data;
            private String deviceId;
            private String device_type;
            private String time;
            private Object macaddr;
            private Object SW;
            private String ipaddr;

            public String getDevetype() {
                return devetype;
            }

            public void setDevetype(String devetype) {
                this.devetype = devetype;
            }

            public String getEvent_str() {
                return event_str;
            }

            public void setEvent_str(String event_str) {
                this.event_str = event_str;
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

            public String getTime() {
                return time;
            }

            public void setTime(String time) {
                this.time = time;
            }

            public Object getMacaddr() {
                return macaddr;
            }

            public void setMacaddr(Object macaddr) {
                this.macaddr = macaddr;
            }

            public Object getSW() {
                return SW;
            }

            public void setSW(Object SW) {
                this.SW = SW;
            }

            public String getIpaddr() {
                return ipaddr;
            }

            public void setIpaddr(String ipaddr) {
                this.ipaddr = ipaddr;
            }
        }
    }
}
