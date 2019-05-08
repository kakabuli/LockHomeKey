package com.kaadas.lock.publiclibrary.mqtt.publishresultbean;

/**
 * Create By denganzhi  on 2019/5/7
 * Describe
 */

public class FtpEnable {

    /**
     * deviceId : CH01191510002
     * func : setFtpEnable
     * gwId : GW01182510163
     * msgId : 1
     * msgtype : respone
     * params : {}
     * returnCode : 200
     * returnData : {"ftpCmdPort":59839,"ftpCmdIp":"198.18.107.136"}
     * timestamp : 1557210418820
     * userId : 5bff4cf6e57f02461d49e285
     */

    private String deviceId;
    private String func;
    private String gwId;
    private int msgId;
    private String msgtype;
    private ParamsBean params;
    private String returnCode;
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
         * ftpCmdPort : 59839
         * ftpCmdIp : 198.18.107.136
         */

        private int ftpCmdPort;
        private String ftpCmdIp;

        public int getFtpCmdPort() {
            return ftpCmdPort;
        }

        public void setFtpCmdPort(int ftpCmdPort) {
            this.ftpCmdPort = ftpCmdPort;
        }

        public String getFtpCmdIp() {
            return ftpCmdIp;
        }

        public void setFtpCmdIp(String ftpCmdIp) {
            this.ftpCmdIp = ftpCmdIp;
        }
    }
}
