package com.kaadas.lock.publiclibrary.bean;

public class CateEyeInfo {

    //猫眼的绑定的UUId
    private String gwID;
    //服务器返回的消息
    private ServerGwDevice serverInfo;

    //是否在线
    private boolean isOnLine;

    //电量
    private int power;

    //网关信息
    private GatewayInfo gatewayInfo;



    public CateEyeInfo() {
    }

    public CateEyeInfo(String gwID, ServerGwDevice serverInfo) {
        this.gwID = gwID;
        this.serverInfo = serverInfo;
    }

    public String getGwID() {
        return gwID;
    }

    public void setGwID(String gwID) {
        this.gwID = gwID;
    }

    public ServerGwDevice getServerInfo() {
        return serverInfo;
    }

    public void setServerInfo(ServerGwDevice serverInfo) {
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

    public GatewayInfo getGatewayInfo() {
        return gatewayInfo;
    }

    public void setGatewayInfo(GatewayInfo gatewayInfo) {
        this.gatewayInfo = gatewayInfo;
    }
}
