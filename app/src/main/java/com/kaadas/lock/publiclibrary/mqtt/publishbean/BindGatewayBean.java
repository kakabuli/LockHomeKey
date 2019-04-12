package com.kaadas.lock.publiclibrary.mqtt.publishbean;

import java.io.Serializable;

public class BindGatewayBean extends BaseBean{


    /**
     * func : bindGatewayByUser
     * devuuid :
     * uid : 5902aca835736f21ae1e7a82
     */

    private String func;
    private String devuuid;
    private String uid;

    public BindGatewayBean(String func, String devuuid, String uid) {
        this.func = func;
        this.devuuid = devuuid;
        this.uid = uid;
    }

    public BindGatewayBean() {
    }

    public String getFunc() {
        return func;
    }

    public void setFunc(String func) {
        this.func = func;
    }

    public String getDevuuid() {
        return devuuid;
    }

    public void setDevuuid(String devuuid) {
        this.devuuid = devuuid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
