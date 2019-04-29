package com.kaadas.lock.publiclibrary.bean;

import java.io.Serializable;

public class GatewayInfo implements Serializable {

    /**
     * 服务器返回的网关信息
     */
    ServerGatewayInfo serverInfo;

    public GatewayInfo() {
    }


    int power;

    public GatewayInfo(ServerGatewayInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    public ServerGatewayInfo getServerInfo() {
        return serverInfo;
    }

    public void setServerInfo(ServerGatewayInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }
}
