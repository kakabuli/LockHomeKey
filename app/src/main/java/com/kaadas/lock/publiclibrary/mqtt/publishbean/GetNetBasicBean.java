package com.kaadas.lock.publiclibrary.mqtt.publishbean;

import java.io.Serializable;

public class GetNetBasicBean implements Serializable {

    /**
     * msgtype : respone
     * userId :
     * msgId : 00001
     * gwId :
     * deviceId : devuuid
     * func :  getNetBasic
     * params : {}
     * returnCode : 200
     * returnData : {"SW":"1.1.0","znpVersion":"","lanIp":"192.168.1.1","lanNetmask":"255.255.255.0","wanIp":"10.10.10.1","wanNetmask":"255.255.0.0","wanType":"dhcp/pppoe/sta"}
     * timestamp : 13433333333
     */

    private String msgtype;
    private String userId;
    private int msgId;
    private String gwId;
    private String deviceId;
    private String func;
    private ParamsBean params;
    private String returnCode;
    private ReturnDataBean returnData;
    private String timestamp;

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

    public int getMsgId() {
        return msgId;
    }

    public void setMsgId(int msgId) {
        this.msgId = msgId;
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

    public ParamsBean getParams() {
        return params;
    }

    public void setParams(ParamsBean params) {
        this.params = params;
    }

    public String getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }

    public ReturnDataBean getReturnData() {
        return returnData;
    }

    public void setReturnData(ReturnDataBean returnData) {
        this.returnData = returnData;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public static class ParamsBean {
    }

    public static class ReturnDataBean {
        /**
         * SW : 1.1.0
         * znpVersion :
         * lanIp : 192.168.1.1
         * lanNetmask : 255.255.255.0
         * wanIp : 10.10.10.1
         * wanNetmask : 255.255.0.0
         * wanType : dhcp/pppoe/sta
         */

        private String SW;
        private String znpVersion;
        private String lanIp;
        private String lanNetmask;
        private String wanIp;
        private String wanNetmask;
        private String wanType;

        public String getSW() {
            return SW;
        }

        public void setSW(String SW) {
            this.SW = SW;
        }

        public String getZnpVersion() {
            return znpVersion;
        }

        public void setZnpVersion(String znpVersion) {
            this.znpVersion = znpVersion;
        }

        public String getLanIp() {
            return lanIp;
        }

        public void setLanIp(String lanIp) {
            this.lanIp = lanIp;
        }

        public String getLanNetmask() {
            return lanNetmask;
        }

        public void setLanNetmask(String lanNetmask) {
            this.lanNetmask = lanNetmask;
        }

        public String getWanIp() {
            return wanIp;
        }

        public void setWanIp(String wanIp) {
            this.wanIp = wanIp;
        }

        public String getWanNetmask() {
            return wanNetmask;
        }

        public void setWanNetmask(String wanNetmask) {
            this.wanNetmask = wanNetmask;
        }

        public String getWanType() {
            return wanType;
        }

        public void setWanType(String wanType) {
            this.wanType = wanType;
        }
    }

    public GetNetBasicBean(String msgtype, String userId, int msgId, String gwId, String deviceId, String func, ParamsBean params, String returnCode, ReturnDataBean returnData, String timestamp) {
        this.msgtype = msgtype;
        this.userId = userId;
        this.msgId = msgId;
        this.gwId = gwId;
        this.deviceId = deviceId;
        this.func = func;
        this.params = params;
        this.returnCode = returnCode;
        this.returnData = returnData;
        this.timestamp = timestamp;
    }

    public GetNetBasicBean() {
    }
}
