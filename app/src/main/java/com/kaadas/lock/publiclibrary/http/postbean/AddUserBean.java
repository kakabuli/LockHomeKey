package com.kaadas.lock.publiclibrary.http.postbean;

import java.util.List;

/**
 * Create By lxj  on 2019/3/3
 * Describe
 */
public class AddUserBean {

    /**
     * admin_id : 5c70ac053c554639ea93cc85
     * device_username : 8618954359825
     * devicemac : string
     * devname : GI132231004
     * end_time : string
     * lockNickName : 别人的门锁
     * open_purview : 3
     * start_time : string
     * items : ["string"]
     */

    private String admin_id;
    private String device_username;
    private String devicemac;
    private String devname;
    private String end_time;
    private String lockNickName;
    private String open_purview;
    private String start_time;
    private List<String> items;


    public AddUserBean(String admin_id, String device_username, String devicemac, String devname, String end_time, String lockNickName, String open_purview, String start_time, List<String> items) {
        this.admin_id = admin_id;
        this.device_username = device_username;
        this.devicemac = devicemac;
        this.devname = devname;
        this.end_time = end_time;
        this.lockNickName = lockNickName;
        this.open_purview = open_purview;
        this.start_time = start_time;
        this.items = items;
    }

    public String getAdmin_id() {
        return admin_id;
    }

    public void setAdmin_id(String admin_id) {
        this.admin_id = admin_id;
    }

    public String getDevice_username() {
        return device_username;
    }

    public void setDevice_username(String device_username) {
        this.device_username = device_username;
    }

    public String getDevicemac() {
        return devicemac;
    }

    public void setDevicemac(String devicemac) {
        this.devicemac = devicemac;
    }

    public String getDevname() {
        return devname;
    }

    public void setDevname(String devname) {
        this.devname = devname;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
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

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }
}
