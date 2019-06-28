package com.kaadas.lock.publiclibrary.mqtt.publishresultbean;

import java.io.Serializable;

public class DeviceShareResultBean implements Serializable {


    /**
     * msgId : 456321
     * msgtype : response
     * userId :
     * gwId :
     * deviceId :
     * func : shareDevice
     * code : 200
     * msg : 成功
     * timestamp : 1557556270390
     * data : {"type":1,"gwId":"GW04191410002","deviceId":"","adminUid":"5c4fe492dc93897aa7d8600b","userAccount":"8618954359822","shareFlag":1}
     */

    private int msgId;
    private String msgtype;
    private String userId;
    private String gwId;
    private String deviceId;
    private String func;
    private String code;
    private String msg;
    private String timestamp;
    private DataBean data;

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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * type : 1
         * gwId : GW04191410002
         * deviceId :
         * adminUid : 5c4fe492dc93897aa7d8600b
         * userAccount : 8618954359822
         * shareFlag : 1
         */

        private int type;
        private String gwId;
        private String deviceId;
        private String adminUid;
        private String userAccount;
        private int shareFlag;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
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

        public String getAdminUid() {
            return adminUid;
        }

        public void setAdminUid(String adminUid) {
            this.adminUid = adminUid;
        }

        public String getUserAccount() {
            return userAccount;
        }

        public void setUserAccount(String userAccount) {
            this.userAccount = userAccount;
        }

        public int getShareFlag() {
            return shareFlag;
        }

        public void setShareFlag(int shareFlag) {
            this.shareFlag = shareFlag;
        }
    }

    public DeviceShareResultBean(int msgId, String msgtype, String userId, String gwId, String deviceId, String func, String code, String msg, String timestamp, DataBean data) {
        this.msgId = msgId;
        this.msgtype = msgtype;
        this.userId = userId;
        this.gwId = gwId;
        this.deviceId = deviceId;
        this.func = func;
        this.code = code;
        this.msg = msg;
        this.timestamp = timestamp;
        this.data = data;
    }

    public DeviceShareResultBean() {
    }
}
