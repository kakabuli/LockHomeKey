package com.kaadas.lock.publiclibrary.bean;

public class GatewayInfo {

    /**
     * 服务器返回的网关信息
     */
    ServerGatewayInfo serverInfo;

    public GatewayInfo() {
    }

    boolean isOnLine;

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

    public boolean isOnLine() {
        return isOnLine;
    }

    public void setOnLine(boolean onLine) {
        isOnLine = onLine;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }
}
