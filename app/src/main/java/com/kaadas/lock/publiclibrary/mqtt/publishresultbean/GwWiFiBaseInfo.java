package com.kaadas.lock.publiclibrary.mqtt.publishresultbean;

import java.io.Serializable;

public class GwWiFiBaseInfo implements Serializable {


    /**
     * deviceId : devuuid       网关ID
     * func :  getWiFiBasic   func
     * gwId :   网关Id
     * msgId : 1
     * msgtype : respone
     * params : {}
     * returnCode : 200
     * returnData : {"encryption":"wpa/wpa2/wep/none","pwd":"12345678","ssid":"abc"}
     * timestamp : 13433333333
     * userId :
     */

    private String deviceId;
    private String func;
    private String gwId;
    private int msgId;
    private String msgtype;
    private ParamsBean params;
    private int returnCode;
    private ReturnDataBean returnData;
    private String timestamp;
    private String userId;

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

    public ParamsBean getParams() {
        return params;
    }

    public void setParams(ParamsBean params) {
        this.params = params;
    }

    public int getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(int returnCode) {
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public static class ParamsBean {
    }

    public static class ReturnDataBean {
        /**
         * encryption : wpa/wpa2/wep/none
         * pwd : 12345678
         * ssid : abc
         */

        private String encryption;
        private String pwd;
        private String ssid;

        public String getEncryption() {
            return encryption;
        }

        public void setEncryption(String encryption) {
            this.encryption = encryption;
        }

        public String getPwd() {
            return pwd;
        }

        public void setPwd(String pwd) {
            this.pwd = pwd;
        }

        public String getSsid() {
            return ssid;
        }

        public void setSsid(String ssid) {
            this.ssid = ssid;
        }
    }
}
