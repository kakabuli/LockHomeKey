package com.kaadas.lock.publiclibrary.http.temp.postbean;

public class DeleteDeviceNormalUserBean {
//	jsonObject.put("adminid", user_id);
//				jsonObject.put("dev_username", userInfo.getUname());
//				jsonObject.put("devname", deviceInfo.getDevice_name());
    private String adminid;
    private String dev_username;
    private String devname;

    public DeleteDeviceNormalUserBean(String adminid, String dev_username, String devname) {
        this.adminid = adminid;
        this.dev_username = dev_username;
        this.devname = devname;
    }
}
