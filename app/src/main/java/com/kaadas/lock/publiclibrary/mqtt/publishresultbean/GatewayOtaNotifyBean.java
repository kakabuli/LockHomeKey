package com.kaadas.lock.publiclibrary.mqtt.publishresultbean;

import com.kaadas.lock.publiclibrary.http.result.ServerDevice;

import java.io.Serializable;
import java.util.List;

public class GatewayOtaNotifyBean implements Serializable {


    /**
     * deviceId : GW01182510033
     * func : otaApprovate
     * gwId : GW01182510033
     * msgId : 1
     * params : {"SW":"orangeiot-2.3.1","childCode":"01","deviceList":["GW01182510033"],"fileLen":6711263,"fileMd5":"85fb621703823f60e758e22e112658cb","fileUrl":"47.106.94.189/otaUpgradeFile/058882d64c134dd0890c12950ba6028a.tar.gz","modelCode":"GW","otaType":1}
     * timestamp : 1560821421381
     * userId : 5cdd31e84574114c0ace1a7b
     */

    private String deviceId;
    private String func;
    private String gwId;
    private int msgId;
    private ParamsBean params;
    private long timestamp;
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

    public ParamsBean getParams() {
        return params;
    }

    public void setParams(ParamsBean params) {
        this.params = params;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public static class ParamsBean implements Serializable{
        /**
         * SW : orangeiot-2.3.1
         * childCode : 01
         * deviceList : ["GW01182510033"]
         * fileLen : 6711263
         * fileMd5 : 85fb621703823f60e758e22e112658cb
         * fileUrl : 47.106.94.189/otaUpgradeFile/058882d64c134dd0890c12950ba6028a.tar.gz
         * modelCode : GW
         * otaType : 1
         */

        private String SW;
        private String childCode;
        private int fileLen;
        private String fileMd5;
        private String fileUrl;
        private String modelCode;
        private int otaType;
        private List<String> deviceList;

        public String getSW() {
            return SW;
        }

        public void setSW(String SW) {
            this.SW = SW;
        }

        public String getChildCode() {
            return childCode;
        }

        public void setChildCode(String childCode) {
            this.childCode = childCode;
        }

        public int getFileLen() {
            return fileLen;
        }

        public void setFileLen(int fileLen) {
            this.fileLen = fileLen;
        }

        public String getFileMd5() {
            return fileMd5;
        }

        public void setFileMd5(String fileMd5) {
            this.fileMd5 = fileMd5;
        }

        public String getFileUrl() {
            return fileUrl;
        }

        public void setFileUrl(String fileUrl) {
            this.fileUrl = fileUrl;
        }

        public String getModelCode() {
            return modelCode;
        }

        public void setModelCode(String modelCode) {
            this.modelCode = modelCode;
        }

        public int getOtaType() {
            return otaType;
        }

        public void setOtaType(int otaType) {
            this.otaType = otaType;
        }

        public List<String> getDeviceList() {
            return deviceList;
        }

        public void setDeviceList(List<String> deviceList) {
            this.deviceList = deviceList;
        }
    }
}
