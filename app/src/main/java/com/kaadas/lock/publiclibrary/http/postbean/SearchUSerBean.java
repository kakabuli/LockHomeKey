package com.kaadas.lock.publiclibrary.http.postbean;

/**
 * Create By lxj  on 2019/3/3
 * Describe
 */
public class SearchUSerBean {

    /**
     * devname : GI132231004
     * user_id : 5c70ac053c554639ea93cc85
     */

    private String devname;
    private String user_id;

    public SearchUSerBean(String devname, String user_id) {
        this.devname = devname;
        this.user_id = user_id;
    }

    public String getDevname() {
        return devname;
    }

    public void setDevname(String devname) {
        this.devname = devname;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
