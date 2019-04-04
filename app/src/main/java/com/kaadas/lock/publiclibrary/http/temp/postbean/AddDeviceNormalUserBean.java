package com.kaadas.lock.publiclibrary.http.temp.postbean;

public class AddDeviceNormalUserBean {
    public AddDeviceNormalUserBean(String admin_id, String device_username, String devicemac, String devmac, String devname, String end_time, String[] items, String lockNickName, String open_purview,
                           String start_time) {
        this.admin_id = admin_id;
        this.device_username = device_username;
        this.devicemac = devicemac;
        this.devmac = devmac;
        this.devname = devname;
        this.end_time = end_time;
        this.items = items;
        this.lockNickName = lockNickName;
        this.open_purview = open_purview;
        this.start_time = start_time;
        this.tokens = tokens;
    }

    public String admin_id;
    public String device_username;
    public String devicemac;
    public String devmac;
    public String devname;
    public String end_time;
    public String[] items;
    public String lockNickName;
    public String open_purview;
    public String start_time;
    public String tokens;

    public static String[] itemTime;
    public static String startTime;
    public static String endTime;

}
