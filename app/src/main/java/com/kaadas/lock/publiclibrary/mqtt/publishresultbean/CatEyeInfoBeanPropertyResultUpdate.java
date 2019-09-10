package com.kaadas.lock.publiclibrary.mqtt.publishresultbean;

import java.util.List;

/**
 * Created by denganzhi on 2019/9/10.
 */

public class CatEyeInfoBeanPropertyResultUpdate {

    /**
     * returnCode : 200
     * func : setPropertys
     * returnData : {}
     * msgId : 8
     * gwId : GW01182510141
     * params : {"propertys":["CamInfrared"],"values":["1,100,50"]}
     * deviceId : AC35EE882849
     * msgtype : respone
     * userId : 5bf6536935736f0af8467a4f
     * timestamp : 1568085670650
     */
    private String returnCode;
    private String func;
    private ReturnDataEntity returnData;
    private int msgId;
    private String gwId;
    private ParamsEntity params;
    private String deviceId;
    private String msgtype;
    private String userId;
    private String timestamp;

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }

    public void setFunc(String func) {
        this.func = func;
    }

    public void setReturnData(ReturnDataEntity returnData) {
        this.returnData = returnData;
    }

    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }

    public void setGwId(String gwId) {
        this.gwId = gwId;
    }

    public void setParams(ParamsEntity params) {
        this.params = params;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getReturnCode() {
        return returnCode;
    }

    public String getFunc() {
        return func;
    }

    public ReturnDataEntity getReturnData() {
        return returnData;
    }

    public int getMsgId() {
        return msgId;
    }

    public String getGwId() {
        return gwId;
    }

    public ParamsEntity getParams() {
        return params;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getMsgtype() {
        return msgtype;
    }

    public String getUserId() {
        return userId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public static class ReturnDataEntity {
    }

    public static class ParamsEntity {
        /**
         * propertys : ["CamInfrared"]
         * values : ["1,100,50"]
         */
        private List<String> propertys;
        private List<String> values;

        public void setPropertys(List<String> propertys) {
            this.propertys = propertys;
        }

        public void setValues(List<String> values) {
            this.values = values;
        }

        public List<String> getPropertys() {
            return propertys;
        }

        public List<String> getValues() {
            return values;
        }
    }

    public CatEyeInfoBeanPropertyResultUpdate(String returnCode, String func, ReturnDataEntity returnData, int msgId, String gwId, ParamsEntity params, String deviceId, String msgtype, String userId, String timestamp) {
        this.returnCode = returnCode;
        this.func = func;
        this.returnData = returnData;
        this.msgId = msgId;
        this.gwId = gwId;
        this.params = params;
        this.deviceId = deviceId;
        this.msgtype = msgtype;
        this.userId = userId;
        this.timestamp = timestamp;
    }

    public CatEyeInfoBeanPropertyResultUpdate() {
    }
}
