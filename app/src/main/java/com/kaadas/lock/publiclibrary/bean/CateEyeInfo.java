package com.kaadas.lock.publiclibrary.bean;

public class CateEyeInfo {

    //猫眼的绑定的UUId
    String gwID;
    //服务器返回的消息
    ServerGwDevice serverInfo;

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
}
