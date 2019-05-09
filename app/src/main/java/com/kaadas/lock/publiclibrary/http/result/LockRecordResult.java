package com.kaadas.lock.publiclibrary.http.result;

import java.util.List;

/**
 * Create By lxj  on 2019/3/3
 * Describe
 */
public class LockRecordResult extends BaseResult {
    @Override
    public String toString() {
        return "LockRecordResult{" +
                "code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }

    /**
     * code : 200
     * msg : 成功
     * data : [{"_id":"5c70e6a93c554639ea93cc94","lockName":"GI132231004","versionType":"kaadas","lockNickName":"GI132231004","nickName":"bbbb","uname":"8618954359824","open_purview":"3","open_time":"2019-02-23 14:22:33","open_type":"200"}]
     *
     * code	String	状态码
     * msg	String	提示语
     * data	JsonObject
     * _id	String	开门记录ID
     * lockName	String	门锁SN
     * lockNickName	String	门锁昵称
     * nickName	String	用户昵称
     * uname	String	用户账号
     * open_time	String	开锁时间
     * open_type	String	开锁类型
     */

    private String code;
    private String msg;
    private List<LockRecordServer> data;

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

    public List<LockRecordServer> getData() {
        return data;
    }

    public void setData(List<LockRecordServer> data) {
        this.data = data;
    }

    public static class LockRecordServer {
        @Override
        public String toString() {
            return "LockRecordServer{" +
                    "_id='" + _id + '\'' +
                    ", lockName='" + lockName + '\'' +
                    ", lockNickName='" + lockNickName + '\'' +
                    ", nickName='" + nickName + '\'' +
                    ", open_purview='" + open_purview + '\'' +
                    ", open_time='" + open_time + '\'' +
                    ", open_type='" + open_type + '\'' +
                    ", status=" + status +
                    ", uname='" + uname + '\'' +
                    ", user_num='" + user_num + '\'' +
                    ", versionType='" + versionType + '\'' +
                    '}';
        }

        /**
         * _id : 5c7e0b5663301ce694eab6d6
         * lockName : XKA434F1A9F118
         * lockNickName : XKA434F1A9F118
         * nickName : 0
         * open_purview : 0
         * open_time : 2017-06-04 12:22:31
         * open_type : 2
         * status : 1
         * uname : 8615338786472
         * user_num : 0
         * versionType : kaadas
         */

        private String _id;
        private String lockName;
        private String lockNickName;
        private String nickName;
        private String open_purview;
        private String open_time;
        private String open_type;
        private int status;
        private String uname;
        private String user_num;
        private String versionType;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getLockName() {
            return lockName;
        }

        public void setLockName(String lockName) {
            this.lockName = lockName;
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

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getUname() {
            return uname;
        }

        public void setUname(String uname) {
            this.uname = uname;
        }

        public String getUser_num() {
            return user_num;
        }

        public void setUser_num(String user_num) {
            this.user_num = user_num;
        }

        public String getVersionType() {
            return versionType;
        }

        public void setVersionType(String versionType) {
            this.versionType = versionType;
        }
    }
}
