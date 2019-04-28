package com.kaadas.lock.publiclibrary.http.result;

import java.util.List;

/**
 * Create By lxj  on 2019/3/3
 * Describe
 */
public class GetDeviceResult extends BaseResult {

    /**
     * code : 200
     * msg : 成功
     * data : [{"_id":"5c70bd443c554639ea93cc89","device_name":"GI132231004","device_nickname":"GI132231004","devmac":"macadr2","open_purview":"3","is_admin":"1","center_latitude":"0","center_longitude":"0","circle_radius":"0","auto_lock":"0","password1":"123123","password2":"258258","model":"","createTime":1551426018}]
     */

    private List<ServerBleDevice> data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<ServerBleDevice> getData() {
        return data;
    }

    public void setData(List<ServerBleDevice> data) {
        this.data = data;
    }



    @Override
    public String toString() {
        return "GetDeviceResult{" +
                "code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
