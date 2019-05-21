package com.kaadas.lock.publiclibrary.bean;

import java.io.Serializable;

public class CateEyeInfo  implements Serializable {

    //猫眼的绑定的UUId
    private String gwID;
    //服务器返回的消息
    private ServerGwDevice serverInfo;

    //电量
    private int power;

    //请求电量的时间戳
    private String powerTimeStamp;

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

    public String getPowerTimeStamp() {
        return powerTimeStamp;
    }

    public void setPowerTimeStamp(String powerTimeStamp) {
        this.powerTimeStamp = powerTimeStamp;
    }


    @Override
    public String toString() {
        return "CateEyeInfo{" +
                "gwID='" + gwID + '\'' +
                ", serverInfo=" + serverInfo +
                ", power=" + power +
                ", powerTimeStamp='" + powerTimeStamp + '\'' +
                ", gatewayInfo=" + gatewayInfo +
                '}';
    }
}
