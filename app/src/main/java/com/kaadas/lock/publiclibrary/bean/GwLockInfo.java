package com.kaadas.lock.publiclibrary.bean;

public class GwLockInfo {

    //网关锁绑定的网关的UUID
    String gwID;
    //服务器返回的信息
    ServerGwDevice serverInfo;

    public GwLockInfo() {
    }

    public GwLockInfo(String gwID, ServerGwDevice serverInfo) {
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
