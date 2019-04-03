package com.kaadas.lock.publiclibrary.http.temp.postbean;

public class GetDeviceLatitudeAndLogitudeBean {
    private String user_id;

    public GetDeviceLatitudeAndLogitudeBean(String user_id, String devname) {
        this.user_id = user_id;
        this.devname = devname;
    }

    private String devname;
}
