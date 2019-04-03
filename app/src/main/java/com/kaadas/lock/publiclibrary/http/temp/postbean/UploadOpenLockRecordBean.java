package com.kaadas.lock.publiclibrary.http.temp.postbean;

import java.util.List;

public class UploadOpenLockRecordBean {
    private String device_name;
    private String device_nickname;
    private List<OpenLockRecordToServer> openLockList;
    private String user_id;

    public UploadOpenLockRecordBean(String device_name, String device_nickname,
                                    List<OpenLockRecordToServer> openLockList, String user_id) {
        this.device_name = device_name;
        this.device_nickname = device_nickname;
        this.openLockList = openLockList;
        this.user_id = user_id;
    }

    public static class OpenLockRecordToServer {
        private String open_time;
        private String open_type;
        private String user_num;
        private String nickName;


        public OpenLockRecordToServer(String open_time, String open_type, String user_num, String nickName) {
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

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }
    }


}
