package com.kaadas.lock.publiclibrary.mqtt.publishbean;

import java.util.List;

/**
 * Created by denganzhi on 2019/9/10.
 */

public class CateEyeInfoBeanPropertyUpdate {


    /**
     * returnCode : 0
     * func : setPropertys
     * returnData : {}
     * msgId : 00001
     * gwId :
     * params : {"propertys":["pirWander"],"values":["1,4"]}
     * msgtype : request
     * userId :
     * deviceId : devuuid
     * timestamp : 13433333333
     */
    private int returnCode;
    private String func;
    private ReturnDataEntity returnData;
    private int msgId;
    private String gwId;
    private ParamsEntity params;
    private String msgtype;
    private String userId;
    private String deviceId;
    private String timestamp;

    public void setReturnCode(int returnCode) {
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

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getReturnCode() {
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

    public String getMsgtype() {
        return msgtype;
    }

    public String getUserId() {
        return userId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public static class ReturnDataEntity {
    }

    public static class ParamsEntity {
        /**
         * propertys : ["pirWander"]
         * values : ["1,4"]
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

    public CateEyeInfoBeanPropertyUpdate() {
    }

    public CateEyeInfoBeanPropertyUpdate(int returnCode, String func, ReturnDataEntity returnData, int msgId, String gwId, ParamsEntity params, String msgtype, String userId, String deviceId, String timestamp) {
        this.returnCode = returnCode;
        this.func = func;
        this.returnData = returnData;
        this.msgId = msgId;
        this.gwId = gwId;
        this.params = params;
        this.msgtype = msgtype;
        this.userId = userId;
        this.deviceId = deviceId;
        this.timestamp = timestamp;
    }
}
