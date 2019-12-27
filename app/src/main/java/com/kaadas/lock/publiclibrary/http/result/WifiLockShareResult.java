package com.kaadas.lock.publiclibrary.http.result;

import java.io.Serializable;
import java.util.List;

public class WifiLockShareResult extends BaseResult {

    /**
     * code : 200
     * msg : 成功
     * nowTime : 1576142450
     * data : [{"_id":"5df206704d3ee14df09d6d50","adminname":"8618954359822","uname":"8618954359823","unickname":"ahaha","open_purview":"3","datestart":"2019-12-12 14:00:00","dateend":"2019-12-12 15:00:00","items":["1"],"createTime":1576142448}]
     */

    private int nowTime;
    private List<WifiLockShareUser> data;

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

    public int getNowTime() {
        return nowTime;
    }

    public void setNowTime(int nowTime) {
        this.nowTime = nowTime;
    }

    public List<WifiLockShareUser> getData() {
        return data;
    }

    public void setData(List<WifiLockShareUser> data) {
        this.data = data;
    }

    public static class WifiLockShareUser implements Serializable {

        /**
         * _id : 5def586f4d3ee1156842868c
         * uid : 5d0c9aa322916bfd695cbae3
         * uname : 8618954359823
         * userNickname : 萝卜头
         */

        private String _id;
        private String uid;
        private String uname;
        private String userNickname;
        private long createTime;

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

        public String getUname() {
            return uname;
        }

        public void setUname(String uname) {
            this.uname = uname;
        }

        public String getUserNickname() {
            return userNickname;
        }

        public void setUserNickname(String userNickname) {
            this.userNickname = userNickname;
        }

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }
    }
}
