package com.kaadas.lock.publiclibrary.mqtt.publishbean;

import java.io.Serializable;

public class BindGatewayBean extends BaseBean{


    /**
     * func : bindGatewayByUser
     * devuuid :
     * uid : 5902aca835736f21ae1e7a82
     */

    private String devuuid;

    public BindGatewayBean(String uid, String func, String devuuid) {
        super(uid, func);
        this.devuuid = devuuid;
    }

    public String getDevuuid() {
        return devuuid;
    }

    public void setDevuuid(String devuuid) {
        this.devuuid = devuuid;
    }


}
