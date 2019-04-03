package com.kaadas.lock.publiclibrary.http.postbean;

/**
 * Create By lxj  on 2019/3/3
 * Describe
 */
public class GetUserProtocolContentBean {

    /**
     * protocolId : 5c6df80da3965c0b5c2d4019
     */

    private String protocolId;


    public GetUserProtocolContentBean(String protocolId) {
        this.protocolId = protocolId;
    }

    public String getProtocolId() {
        return protocolId;
    }

    public void setProtocolId(String protocolId) {
        this.protocolId = protocolId;
    }
}
