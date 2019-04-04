package com.kaadas.lock.publiclibrary.http.temp.resultbean;

import java.util.List;

/**
 * Create By lxj  on 2019/1/12
 * Describe
 */
public class LocksResult {

    /**
     * code : 200
     * data : [{"_id":"5c394c5435736f6e8f9b9bfa","auto_lock":"0","center_latitude":"0","center_longitude":"0","circle_radius":"0","device_name":"KDSA434F1CC1568","device_nickname":"KDSA434F1CC1568","devmac":"A4:34:F1:CC:15:68","is_admin":"1","model":"","open_purview":"3","password1":"f564967d578934ab33bc9496","password2":"5fc5b7ad"}]
     * msg : 成功
     */
    List<BaseLockInfo> data;

    public List<BaseLockInfo> getData() {
        return data;
    }

    public void setData(List<BaseLockInfo> data) {
        this.data = data;
    }
}
