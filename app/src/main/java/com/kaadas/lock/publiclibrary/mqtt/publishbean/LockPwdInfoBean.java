package com.kaadas.lock.publiclibrary.mqtt.publishbean;

import java.io.Serializable;

public class LockPwdInfoBean implements Serializable {


    /**
     * msgtype : respone
     * userId :
     * msgId : 00001
     * gwId :
     * deviceId : devuuid
     * func :  lockPwdInfo
     * params : {}
     * returnCode : 200
     * returnData : {"maxpwdusernum":100,"maxrfidusernum":100,"maxusernum":100,"maxpwdsize":8,"minpwdsize":8,"maxrfidsize":8,"minrfidsize":8}
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
         * maxpwdusernum : 100
         * maxrfidusernum : 100
         * maxusernum : 100
         * maxpwdsize : 8
         * minpwdsize : 8
         * maxrfidsize : 8
         * minrfidsize : 8
         */

        private int maxpwdusernum;
        private int maxrfidusernum;
        private int maxusernum;
        private int maxpwdsize;
        private int minpwdsize;
        private int maxrfidsize;
        private int minrfidsize;

        public int getMaxpwdusernum() {
            return maxpwdusernum;
        }

        public void setMaxpwdusernum(int maxpwdusernum) {
            this.maxpwdusernum = maxpwdusernum;
        }

        public int getMaxrfidusernum() {
            return maxrfidusernum;
        }

        public void setMaxrfidusernum(int maxrfidusernum) {
            this.maxrfidusernum = maxrfidusernum;
        }

        public int getMaxusernum() {
            return maxusernum;
        }

        public void setMaxusernum(int maxusernum) {
            this.maxusernum = maxusernum;
        }

        public int getMaxpwdsize() {
            return maxpwdsize;
        }

        public void setMaxpwdsize(int maxpwdsize) {
            this.maxpwdsize = maxpwdsize;
        }

        public int getMinpwdsize() {
            return minpwdsize;
        }

        public void setMinpwdsize(int minpwdsize) {
            this.minpwdsize = minpwdsize;
        }

        public int getMaxrfidsize() {
            return maxrfidsize;
        }

        public void setMaxrfidsize(int maxrfidsize) {
            this.maxrfidsize = maxrfidsize;
        }

        public int getMinrfidsize() {
            return minrfidsize;
        }

        public void setMinrfidsize(int minrfidsize) {
            this.minrfidsize = minrfidsize;
        }
    }

    public LockPwdInfoBean(String msgtype, String userId, int msgId, String gwId, String deviceId, String func, ParamsBean params, String returnCode, ReturnDataBean returnData, String timestamp) {
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

    public LockPwdInfoBean() {
    }
}
