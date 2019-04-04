package com.kaadas.lock.publiclibrary.http.temp.postbean;

/**
 * Create By lxj  on 2019/1/12
 * Describe
 */
public class GetDevicesList {

    public GetDevicesList(String user_id) {
        this.user_id = user_id;
    }

    public GetDevicesList() {

    }

    /**
     * user_id : string
     */



    private String user_id;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
