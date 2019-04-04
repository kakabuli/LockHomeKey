package com.kaadas.lock.publiclibrary.http.temp.postbean;

public class GetOpenLockRecordBean {
    private String user_id;
    private String device_name;
    private String pagenum;

    public GetOpenLockRecordBean(String user_id, String device_name, String pagenum) {
        this.user_id = user_id;
        this.device_name = device_name;
        this.pagenum = pagenum;
    }
}
