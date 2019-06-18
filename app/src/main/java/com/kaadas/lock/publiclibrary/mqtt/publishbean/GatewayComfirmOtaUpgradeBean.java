package com.kaadas.lock.publiclibrary.mqtt.publishbean;

import java.io.Serializable;
import java.util.List;

public class GatewayComfirmOtaUpgradeBean implements Serializable {


    /**
     * func : otaApprovateResult
     * gwId : UI01181911117
     * deviceId : UI01181911117
     * timestamp : 1539845863886
     * msgId : 1
     * uid : 5c4fe492dc93897aa7d8600b
     * userId : 5902aca835736f21ae1e7a82
     * params : {"modelCode":"UI","childCode":"01","fileUrl":"HTTPS://12312312","SW":"1.3","deviceList":["UI01181911117"],"fileMd5":"QWEQ","fileLen":0,"otaType":1,"type":1}
     */

    private String func;
    private String gwId;
    private String deviceId;
    private long timestamp;
    private int msgId;
    private String uid;
    private String userId;
    private ParamsBean params;

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

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getMsgId() {
        return msgId;
    }

    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public ParamsBean getParams() {
        return params;
    }

    public void setParams(ParamsBean params) {
        this.params = params;
    }

    public static class ParamsBean {
        /**
         * modelCode : UI
         * childCode : 01
         * fileUrl : HTTPS://12312312
         * SW : 1.3
         * deviceList : ["UI01181911117"]
         * fileMd5 : QWEQ
         * fileLen : 0
         * otaType : 1
         * type : 1
         */

        private String modelCode;
        private String childCode;
        private String fileUrl;
        private String SW;
        private String fileMd5;
        private int fileLen;
        private int otaType;
        private int type;
        private List<String> deviceList;

        public String getModelCode() {
            return modelCode;
        }

        public void setModelCode(String modelCode) {
            this.modelCode = modelCode;
        }

        public String getChildCode() {
            return childCode;
        }

        public void setChildCode(String childCode) {
            this.childCode = childCode;
        }

        public String getFileUrl() {
            return fileUrl;
        }

        public void setFileUrl(String fileUrl) {
            this.fileUrl = fileUrl;
        }

        public String getSW() {
            return SW;
        }

        public void setSW(String SW) {
            this.SW = SW;
        }

        public String getFileMd5() {
            return fileMd5;
        }

        public void setFileMd5(String fileMd5) {
            this.fileMd5 = fileMd5;
        }

        public int getFileLen() {
            return fileLen;
        }

        public void setFileLen(int fileLen) {
            this.fileLen = fileLen;
        }

        public int getOtaType() {
            return otaType;
        }

        public void setOtaType(int otaType) {
            this.otaType = otaType;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public List<String> getDeviceList() {
            return deviceList;
        }

        public void setDeviceList(List<String> deviceList) {
            this.deviceList = deviceList;
        }

        public ParamsBean(String modelCode, String childCode, String fileUrl, String SW, String fileMd5, int fileLen, int otaType, int type, List<String> deviceList) {
            this.modelCode = modelCode;
            this.childCode = childCode;
            this.fileUrl = fileUrl;
            this.SW = SW;
            this.fileMd5 = fileMd5;
            this.fileLen = fileLen;
            this.otaType = otaType;
            this.type = type;
            this.deviceList = deviceList;
        }
    }

    public GatewayComfirmOtaUpgradeBean(String func, String gwId, String deviceId, long timestamp, int msgId, String uid, String userId, ParamsBean params) {
        this.func = func;
        this.gwId = gwId;
        this.deviceId = deviceId;
        this.timestamp = timestamp;
        this.msgId = msgId;
        this.uid = uid;
        this.userId = userId;
        this.params = params;
    }
}
