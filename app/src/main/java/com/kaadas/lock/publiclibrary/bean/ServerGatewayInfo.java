package com.kaadas.lock.publiclibrary.bean;

import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.AllBindDevices;

import java.io.Serializable;

public class ServerGatewayInfo implements Serializable {
    private String deviceSN;
    private String deviceNickName;
    private String adminuid;
    private String adminName;
    private String adminNickname;
    private int isAdmin;
    private String meUsername;
    private String mePwd;
    private int meBindState;
    private String model;   // 6030 小网关、 6010大网关
    public ServerGatewayInfo() {
    }

    public ServerGatewayInfo(AllBindDevices.ReturnDataBean.GwListBean gwListBean) {
        deviceSN = gwListBean.getDeviceSN();
        deviceNickName = gwListBean.getDeviceNickName();
        adminuid = gwListBean.getAdminuid();
        adminName = gwListBean.getAdminName();
        adminNickname = gwListBean.getAdminNickname();
        isAdmin = gwListBean.getIsAdmin();
        meUsername = gwListBean.getMeUsername();
        mePwd = gwListBean.getMePwd();
        meBindState = gwListBean.getMeBindState();
        model= gwListBean.getModel();
    }


    public String getDeviceSN() {
        return deviceSN;
    }

    public void setDeviceSN(String deviceSN) {
        this.deviceSN = deviceSN;
    }

    public String getDeviceNickName() {
        return deviceNickName;
    }

    public void setDeviceNickName(String deviceNickName) {
        this.deviceNickName = deviceNickName;
    }

    public String getAdminuid() {
        return adminuid;
    }

    public void setAdminuid(String adminuid) {
        this.adminuid = adminuid;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getAdminNickname() {
        return adminNickname;
    }

    public void setAdminNickname(String adminNickname) {
        this.adminNickname = adminNickname;
    }

    public int getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(int isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getMeUsername() {
        return meUsername;
    }

    public void setMeUsername(String meUsername) {
        this.meUsername = meUsername;
    }

    public String getMePwd() {
        return mePwd;
    }

    public void setMePwd(String mePwd) {
        this.mePwd = mePwd;
    }

    public int getMeBindState() {
        return meBindState;
    }

    public void setMeBindState(int meBindState) {
        this.meBindState = meBindState;
    }

    public ServerGatewayInfo(String deviceSN, String deviceNickName, String adminuid, String adminName, String adminNickname, int isAdmin, String meUsername, String mePwd, int meBindState) {
        this.deviceSN = deviceSN;
        this.deviceNickName = deviceNickName;
        this.adminuid = adminuid;
        this.adminName = adminName;
        this.adminNickname = adminNickname;
        this.isAdmin = isAdmin;
        this.meUsername = meUsername;
        this.mePwd = mePwd;
        this.meBindState = meBindState;
    }

    public ServerGatewayInfo(String deviceSN, String deviceNickName, String adminuid, String adminName, String adminNickname, int isAdmin, String meUsername, String mePwd, int meBindState, String model) {
        this.deviceSN = deviceSN;
        this.deviceNickName = deviceNickName;
        this.adminuid = adminuid;
        this.adminName = adminName;
        this.adminNickname = adminNickname;
        this.isAdmin = isAdmin;
        this.meUsername = meUsername;
        this.mePwd = mePwd;
        this.meBindState = meBindState;
        this.model = model;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
