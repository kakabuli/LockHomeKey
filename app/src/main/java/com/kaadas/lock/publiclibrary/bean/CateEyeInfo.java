package com.kaadas.lock.publiclibrary.bean;

import java.io.Serializable;

public class CateEyeInfo  implements Serializable {

    //猫眼的绑定的UUId
    String gwID;
    //服务器返回的消息
    ServerGwDevice serverInfo;

    //是否在线
    boolean isOnLine;

    //电量
    int power;


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
}
