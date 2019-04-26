package com.kaadas.lock.publiclibrary.bean;

public class GatewayInfo {

    /**
     * 服务器返回的网关信息
     */
    ServerGatewayInfo serverInfo;

    public GatewayInfo() {
    }

    public GatewayInfo(ServerGatewayInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    public ServerGatewayInfo getServerInfo() {
        return serverInfo;
    }

    public void setServerInfo(ServerGatewayInfo serverInfo) {
        this.serverInfo = serverInfo;
    }
}
