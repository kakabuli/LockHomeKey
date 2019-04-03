package com.kaadas.lock.publiclibrary.http.postbean;

/**
 * Create By lxj  on 2019/3/3
 * Describe
 */
public class GetLockRecordBean {

    /**
     * device_name : GI132231004
     * user_id : 5c70ac053c554639ea93cc85
     * openType : 200
     * pagenum : 1
     */

    private String device_name;
    private String user_id;
    private String openType;
    private String pagenum;

    public GetLockRecordBean(String device_name, String user_id, String openType, String pagenum) {
        this.device_name = device_name;
        this.user_id = user_id;
        this.openType = openType;
        this.pagenum = pagenum;
    }

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getOpenType() {
        return openType;
    }

    public void setOpenType(String openType) {
        this.openType = openType;
    }

    public String getPagenum() {
        return pagenum;
    }

    public void setPagenum(String pagenum) {
        this.pagenum = pagenum;
    }
}
