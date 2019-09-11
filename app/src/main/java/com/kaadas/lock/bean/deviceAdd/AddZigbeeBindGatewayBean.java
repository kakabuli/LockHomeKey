package com.kaadas.lock.bean.deviceAdd;

import java.io.Serializable;

public class AddZigbeeBindGatewayBean implements Serializable {
    private String nickName;
    private String adminId;
    private String gatewayId;
    private int isOnLine;
    private boolean isSelect;
    private int isAdmin;
    private  String model;

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getGatewayId() {
        return gatewayId;
    }

    public void setGatewayId(String gatewayId) {
        this.gatewayId = gatewayId;
    }

    public int getIsOnLine() {
        return isOnLine;
    }

    public void setIsOnLine(int isOnLine) {
        this.isOnLine = isOnLine;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public int getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(int isAdmin) {
        this.isAdmin = isAdmin;
    }

    public AddZigbeeBindGatewayBean(String nickName, String adminId, String gatewayId, int isOnLine, boolean isSelect, int isAdmin) {
        this.nickName = nickName;
        this.adminId = adminId;
        this.gatewayId = gatewayId;
        this.isOnLine = isOnLine;
        this.isSelect = isSelect;
        this.isAdmin = isAdmin;
    }

    public AddZigbeeBindGatewayBean() {
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public AddZigbeeBindGatewayBean(String nickName, String adminId, String gatewayId, int isOnLine, boolean isSelect, int isAdmin, String model) {
        this.nickName = nickName;
        this.adminId = adminId;
        this.gatewayId = gatewayId;
        this.isOnLine = isOnLine;
        this.isSelect = isSelect;
        this.isAdmin = isAdmin;
        this.model = model;
    }
}
