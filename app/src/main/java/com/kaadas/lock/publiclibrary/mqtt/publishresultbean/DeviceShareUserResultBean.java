package com.kaadas.lock.publiclibrary.mqtt.publishresultbean;

import java.io.Serializable;
import java.util.List;

public class DeviceShareUserResultBean implements Serializable {


    /**
     * msgId : 666
     * msgtype : response
     * userId :
     * gwId : GW05191910010
     * deviceId : ZGCCXXYY58267
     * func : shareUserList
     * code : 200
     * msg : 成功
     * timestamp : 1561686432357
     * data : [{"_id":"5cd66cc2d98d1d2f0cd09786","uid":"5d0c9aa322916bfd695cbae3","username":"8618954359823","adminuid":"5c4fe492dc93897aa7d8600b","userNickname":"小王⑧"}]
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

    public static class DataBean implements Serializable{
        /**
         * _id : 5cd66cc2d98d1d2f0cd09786
         * uid : 5d0c9aa322916bfd695cbae3
         * username : 8618954359823
         * adminuid : 5c4fe492dc93897aa7d8600b
         * userNickname : 小王⑧
         */

        private String _id;
        private String uid;
        private String username;
        private String adminuid;
        private String userNickname;
        private String time;
        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getAdminuid() {
            return adminuid;
        }

        public void setAdminuid(String adminuid) {
            this.adminuid = adminuid;
        }

        public String getUserNickname() {
            return userNickname;
        }

        public void setUserNickname(String userNickname) {
            this.userNickname = userNickname;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }
    }
}
