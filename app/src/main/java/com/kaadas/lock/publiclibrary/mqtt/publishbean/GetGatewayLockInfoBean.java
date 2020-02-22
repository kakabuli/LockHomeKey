package com.kaadas.lock.publiclibrary.mqtt.publishbean;

import java.io.Serializable;

public class GetGatewayLockInfoBean implements Serializable {
    /**
     * msgtype : respone
     * userId :
     * msgId : 00001
     * gwId :
     * deviceId : devuuid
     * func : BasicInfo
     * params : {}
     * returnCode : 200
     * returnData : {"macaddr":"xxxxxx","model":"xxxxxx","firmware":"xxxxxx","hwversion":"xxxxxx","swversion":"xxxxxx","manufact":"xxxxxx","linkquality":"xxxxxx"}
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
         * macaddr : xxxxxx
         * model : xxxxxx
         * firmware : xxxxxx
         * hwversion : xxxxxx
         * swversion : xxxxxx
         * manufact : xxxxxx
         * linkquality : xxxxxx
         */

        private String macaddr;
        private String model;
        private String firmware;
        private String hwversion;
        private String swversion;
        private String manufact;
        private String linkquality;
        private String lockversion;


        public String getLockversion() {
            return lockversion;
        }

        public void setLockversion(String lockversion) {
            this.lockversion = lockversion;
        }

        public String getMacaddr() {
            return macaddr;
        }

        public void setMacaddr(String macaddr) {
            this.macaddr = macaddr;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public String getFirmware() {
            return firmware;
        }

        public void setFirmware(String firmware) {
            this.firmware = firmware;
        }

        public String getHwversion() {
            return hwversion;
        }

        public void setHwversion(String hwversion) {
            this.hwversion = hwversion;
        }

        public String getSwversion() {
            return swversion;
        }

        public void setSwversion(String swversion) {
            this.swversion = swversion;
        }

        public String getManufact() {
            return manufact;
        }

        public void setManufact(String manufact) {
            this.manufact = manufact;
        }

        public String getLinkquality() {
            return linkquality;
        }

        public void setLinkquality(String linkquality) {
            this.linkquality = linkquality;
        }
    }

    public GetGatewayLockInfoBean(String msgtype, String userId, int msgId, String gwId, String deviceId, String func, ParamsBean params, String returnCode, ReturnDataBean returnData, String timestamp) {
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

    public GetGatewayLockInfoBean() {
    }
}
