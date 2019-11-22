package com.kaadas.lock.publiclibrary.mqtt.publishbean;

public class SetUserTypeBean {


    /**
     * deviceId : devuuid
     * func : setUserType
     * gwId :
     * msgId : 1
     * msgtype : request
     * params : {"userID":1,"userType":1}
     * returnCode : 0
     * returnData : {}
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


    public SetUserTypeBean(String deviceId, String func, String gwId, int msgId, String msgtype, ParamsBean params, int returnCode, ReturnDataBean returnData, String timestamp, String userId) {
        this.deviceId = deviceId;
        this.func = func;
        this.gwId = gwId;
        this.msgId = msgId;
        this.msgtype = msgtype;
        this.params = params;
        this.returnCode = returnCode;
        this.returnData = returnData;
        this.timestamp = timestamp;
        this.userId = userId;
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
        public ParamsBean(int userID, int userType) {
            this.userID = userID;
            this.userType = userType;
        }

        /**
         * userID : 1
         * userType : 1
         */



        private int userID;
        private int userType;

        public int getUserID() {
            return userID;
        }

        public void setUserID(int userID) {
            this.userID = userID;
        }

        public int getUserType() {
            return userType;
        }

        public void setUserType(int userType) {
            this.userType = userType;
        }
    }

    public static class ReturnDataBean {
    }
}
