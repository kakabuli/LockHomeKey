package com.kaadas.lock.publiclibrary.mqtt.publishbean;

import java.util.List;

/**
 * Created by denganzhi on 2019/9/6.
 */

public class CatEyeInfoBeanProperty {


    /**
     * returnCode : 0
     * func : getPropertys
     * returnData : {}
     * msgId : 00001
     * gwId :
     * params : {"propertys":["timezone"]}
     * msgtype : request
     * userId :
     * deviceId : devuuid
     * timestamp : 13433333333
     */
    private String msgtype;
    private String userId;
    private String msgId;
    private String gwId;
    private String deviceId;
    private String func;
    private ParamsEntity params;
    private int returnCode;
    private ReturnDataEntity returnData;
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

    public void setMsgId(String msgId) {
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

    public String getMsgId() {
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
         * propertys : ["timezone"]
         */
        private List<String> propertys;

        public void setPropertys(List<String> propertys) {
            this.propertys = propertys;
        }

        public List<String> getPropertys() {
            return propertys;
        }
    }

    public CatEyeInfoBeanProperty(String msgtype, String userId, String msgId, String gwId, String deviceId, String func, ParamsEntity params, int returnCode, ReturnDataEntity returnData, String timestamp) {
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

    public CatEyeInfoBeanProperty() {
    }
}
