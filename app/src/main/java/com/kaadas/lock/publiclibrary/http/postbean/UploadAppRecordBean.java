package com.kaadas.lock.publiclibrary.http.postbean;

/**
 * Create By lxj  on 2019/3/3
 * Describe
 */
public class UploadAppRecordBean {

    /**
     * devname : GI132231004
     * is_admin : 1
     * open_type : 200
     * user_id : 5c70ac053c554639ea93cc85
     * nickName : bbbb
     */

    private String devname;
    private String is_admin;
    private String open_type;
    private String user_id;
    private String nickName;

    public UploadAppRecordBean(String devname, String is_admin, String open_type, String user_id, String nickName) {
        this.devname = devname;
        this.is_admin = is_admin;
        this.open_type = open_type;
        this.user_id = user_id;
        this.nickName = nickName;
    }

    public String getDevname() {
        return devname;
    }

    public void setDevname(String devname) {
        this.devname = devname;
    }

    public String getIs_admin() {
        return is_admin;
    }

    public void setIs_admin(String is_admin) {
        this.is_admin = is_admin;
    }

    public String getOpen_type() {
        return open_type;
    }

    public void setOpen_type(String open_type) {
        this.open_type = open_type;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
