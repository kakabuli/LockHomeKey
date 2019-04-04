package com.kaadas.lock.publiclibrary.http.temp.postbean;

public class ModifyDeviceNicknameBean {
    private String user_id;
    private String lockNickName;
    private String devname;

    public ModifyDeviceNicknameBean(String user_id, String lockNickName, String devname) {
        this.user_id = user_id;
        this.lockNickName = lockNickName;
        this.devname = devname;
    }
}
