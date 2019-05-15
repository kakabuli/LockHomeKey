package com.kaadas.lock.publiclibrary.mqtt.publishbean;

import java.io.Serializable;

public class SetPirSlientBean implements Serializable {


    /**
     * msgtype : request
     * userId :
     * msgId : 1
     * gwId :
     * deviceId : devuuid
     * func : setPirSilent
     * params : {"periodtime":15,"threshold":3,"protecttime":10,"ust":15,"maxprohibition":8,"enable":1}
     * returnCode : 0
     * returnData : {}
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
        /**
         * periodtime : 15
         * threshold : 3
         * protecttime : 10
         * ust : 15
         * maxprohibition : 8
         * enable : 1
         */

        private int periodtime;
        private int threshold;
        private int protecttime;
        private int ust;
        private int maxprohibition;
        private int enable;

        public int getPeriodtime() {
            return periodtime;
        }

        public void setPeriodtime(int periodtime) {
            this.periodtime = periodtime;
        }

        public int getThreshold() {
            return threshold;
        }

        public void setThreshold(int threshold) {
            this.threshold = threshold;
        }

        public int getProtecttime() {
            return protecttime;
        }

        public void setProtecttime(int protecttime) {
            this.protecttime = protecttime;
        }

        public int getUst() {
            return ust;
        }

        public void setUst(int ust) {
            this.ust = ust;
        }

        public int getMaxprohibition() {
            return maxprohibition;
        }

        public void setMaxprohibition(int maxprohibition) {
            this.maxprohibition = maxprohibition;
        }

        public int getEnable() {
            return enable;
        }

        public void setEnable(int enable) {
            this.enable = enable;
        }
    }

    public static class ReturnDataBean {
    }

    public SetPirSlientBean(String msgtype, String userId, int msgId, String gwId, String deviceId, String func, ParamsBean params, String returnCode, ReturnDataBean returnData, String timestamp) {
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

    public SetPirSlientBean() {
    }
}
