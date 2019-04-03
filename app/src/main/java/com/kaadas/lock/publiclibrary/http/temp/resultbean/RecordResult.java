package com.kaadas.lock.publiclibrary.http.temp.resultbean;

import java.util.List;

public class RecordResult {

    List<Record> data;

    public List<Record> getData() {
        return data;
    }

    public void setData(List<Record> data) {
        this.data = data;
    }

    public class Record {

        /**
         * _id : 5c76988163301ce694e96b06
         * lockName : XKA434F1A9F118
         * open_time : 2019-02-26 10:42:20
         * open_type : 103
         * nickName : 103
         * user_num : 103
         * uname : 8615338786472
         * versionType : kaadas
         * lockNickName : XKA434F1A9F118
         * open_purview : 0
         * status : 1
         */

        private String _id;
        private String lockName;
        private String open_time;
        private String open_type;
        private String nickName;
        private String user_num;
        private String uname;
        private String versionType;
        private String lockNickName;
        private String open_purview;
        private int status;

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

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getUser_num() {
            return user_num;
        }

        public void setUser_num(String user_num) {
            this.user_num = user_num;
        }

        public String getUname() {
            return uname;
        }

        public void setUname(String uname) {
            this.uname = uname;
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

        public String getOpen_purview() {
            return open_purview;
        }

        public void setOpen_purview(String open_purview) {
            this.open_purview = open_purview;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        @Override
        public String toString() {
            return "Record{" +
                    "_id='" + _id + '\'' +
                    ", lockName='" + lockName + '\'' +
                    ", open_time='" + open_time + '\'' +
                    ", open_type='" + open_type + '\'' +
                    ", nickName='" + nickName + '\'' +
                    ", user_num='" + user_num + '\'' +
                    ", uname='" + uname + '\'' +
                    ", versionType='" + versionType + '\'' +
                    ", lockNickName='" + lockNickName + '\'' +
                    ", open_purview='" + open_purview + '\'' +
                    ", status=" + status +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "RecordResult{" +
                "data=" + data +
                '}';
    }
}
