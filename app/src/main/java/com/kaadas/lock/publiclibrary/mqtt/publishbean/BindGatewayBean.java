package com.kaadas.lock.publiclibrary.mqtt.publishbean;

public class BindGatewayBean extends GetBindGatewayListBean {


    /**
     * func : bindGatewayByUser
     * devuuid :
     * uid : 5902aca835736f21ae1e7a82
     */
    private int msgId;
    private String devuuid;
    public BindGatewayBean(int msgId,String uid, String func, String devuuid) {
        super(uid, func);
        this.devuuid = devuuid;
        this.msgId=msgId;
    }

    public String getDevuuid() {
        return devuuid;
    }

    public void setDevuuid(String devuuid) {
        this.devuuid = devuuid;
    }

    public int getMsgId() {
        return msgId;
    }

    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }
}
