package com.kaadas.lock.publiclibrary.mqtt.publishresultbean;

import java.io.Serializable;
import java.util.List;

public class SelectOpenLockResultBean implements Serializable {


    /**
     * msgId : 123654
     * msgtype : response
     * userId :
     * gwId :
     * deviceId : ZG01184112588
     * func : selectOpenLockRecord
     * code : 200
     * msg : 成功
     * timestamp : 1556607092707
     * data : [{"lockName":"ZG01184112588","versionType":"kaadas","lockNickName":"XKA434F1A9F041","nickName":"8613152657145","uname":"8613152657145","open_purview":"3","open_time":"2019-04-18 11:05:17","open_type":"7"}]
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
    private List<DataBean> data;

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

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * lockName : ZG01184112588
         * versionType : kaadas
         * lockNickName : XKA434F1A9F041
         * nickName : 8613152657145
         * uname : 8613152657145
         * open_purview : 3
         * open_time : 2019-04-18 11:05:17
         * open_type : 7
         */

        private String lockName;
        private String versionType;
        private String lockNickName;
        private String nickName;
        private String uname;
        private String open_purview;
        private String open_time;
        private String open_type;

        public String getLockName() {
            return lockName;
        }

        public void setLockName(String lockName) {
            this.lockName = lockName;
        }

        public String getVersionType() {
            return versionType;
        }

        public void setVersionType(String versionType) {
            this.versionType = versionType;
        }

        public String getLockNickName() {
            return lockNickName;
        }

        public void setLockNickName(String lockNickName) {
            this.lockNickName = lockNickName;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getUname() {
            return uname;
        }

        public void setUname(String uname) {
            this.uname = uname;
        }

        public String getOpen_purview() {
            return open_purview;
        }

        public void setOpen_purview(String open_purview) {
            this.open_purview = open_purview;
        }

        public String getOpen_time() {
            return open_time;
        }

        public void setOpen_time(String open_time) {
            this.open_time = open_time;
        }

        public String getOpen_type() {
            return open_type;
        }

        public void setOpen_type(String open_type) {
            this.open_type = open_type;
        }
    }
}
