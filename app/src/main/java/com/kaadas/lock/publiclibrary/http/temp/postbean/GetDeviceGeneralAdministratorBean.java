package com.kaadas.lock.publiclibrary.http.temp.postbean;

public class GetDeviceGeneralAdministratorBean {
    private String user_id;
    private String devname;

    public GetDeviceGeneralAdministratorBean(String user_id, String devname) {
        this.user_id = user_id;
        this.devname = devname;
    }
}
