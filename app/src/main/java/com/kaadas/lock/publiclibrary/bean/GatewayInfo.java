package com.kaadas.lock.publiclibrary.bean;

import java.io.Serializable;

public class GatewayInfo implements Serializable {

    /**
     * 服务器返回的网关信息
     */
    ServerGatewayInfo serverInfo;

    /**
     * 上线和离线状态
     * online  在线
     * offline 离线
     */
    String event_str;

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

    public String getEvent_str() {
        return event_str;
    }

    public void setEvent_str(String event_str) {
        this.event_str = event_str;
    }
}
