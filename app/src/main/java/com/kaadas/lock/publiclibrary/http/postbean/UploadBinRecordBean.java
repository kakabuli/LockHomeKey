package com.kaadas.lock.publiclibrary.http.postbean;

import java.util.List;

/**
 * Create By lxj  on 2019/3/3
 * Describe
 */
public class UploadBinRecordBean {

    /**
     * device_name : GI132231004
     * device_nickname : 别人的门锁
     * user_id : 5c70ac053c554639ea93cc85
     * openLockList : [{"open_time":"2019-02-19 19:21:23","open_type":"指纹","user_num":"002","nickName":"小明的指纹"}]
     */

    private String device_name;
    private String device_nickname;
    private String user_id;
    private List<OpenLockRecordBle> openLockList;

    public UploadBinRecordBean(String device_name, String device_nickname, String user_id, List<OpenLockRecordBle> openLockList) {
        this.device_name = device_name;
        this.device_nickname = device_nickname;
        this.user_id = user_id;
        this.openLockList = openLockList;
    }

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    public String getDevice_nickname() {
        return device_nickname;
    }

    public void setDevice_nickname(String device_nickname) {
        this.device_nickname = device_nickname;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public List<OpenLockRecordBle> getOpenLockList() {
        return openLockList;
    }

    public void setOpenLockList(List<OpenLockRecordBle> openLockList) {
        this.openLockList = openLockList;
    }

    public static class OpenLockRecordBle {
        /**
         * open_time : 2019-02-19 19:21:23
         * open_type : 指纹
         * user_num : 002
         * nickName : 小明的指纹
         */

        private String open_time;
        private String open_type;
        private String user_num;
        private String nickName;

        public OpenLockRecordBle(String open_time, String open_type, String user_num, String nickName) {
            this.open_time = open_time;
            this.open_type = open_type;
            this.user_num = user_num;
            this.nickName = nickName;
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

        public String getUser_num() {
            return user_num;
        }

        public void setUser_num(String user_num) {
            this.user_num = user_num;
        }

        public String getNickName() {
            return nickName;
        }

        @Override
        public String toString() {
            return "OpenLockRecordBle{" +
                    "open_time='" + open_time + '\'' +
                    ", open_type='" + open_type + '\'' +
                    ", user_num='" + user_num + '\'' +
                    ", nickName='" + nickName + '\'' +
                    '}';
        }

        public void setNickName(String nickName) {


            this.nickName = nickName;
        }
    }
}
