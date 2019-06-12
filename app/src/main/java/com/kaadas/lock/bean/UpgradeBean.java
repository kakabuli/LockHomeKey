package com.kaadas.lock.bean;

import java.io.Serializable;

/**
 * Create By denganzhi  on 2019/6/10
 * Describe
 */

public class UpgradeBean implements Serializable {


    /**
     * appName : Kaadas
     * lastVersionName : V2.2.11.218
     * versionName : V2.2.11.226
     * versionCode : 201901121
     * updateURL : http://www.kaadas.com/app/Kaadas/Kaadas.apk
     * isForced : false
     * isPrompt : true
     * upgradeInfoEn : V2.2.11.226 version update, fix Bluetooth bug, optimize user experience
     * upgradeInfoZh : V2.2.11.226版本更新,修复蓝牙bug,优化用户体验
     * upgradeInfoTw : V2.2.11.226版本更新，修復藍牙bug，優化用戶體驗
     * upgradeInfoTai : บลูทูธ V2.2.11.226 รุ่นที่ปรับปรุงและแก้ไขข้อผิดพลาดและเพิ่มประสบการณ์ของผู้ใช้
     */

    private String appName;
    private String lastVersionName;
    private String versionName;
    private String versionCode;
    private String updateURL;
    private String isForced;
    private String isPrompt;
    private String upgradeInfoEn;
    private String upgradeInfoZh;
    private String upgradeInfoTw;
    private String upgradeInfoTai;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getLastVersionName() {
        return lastVersionName;
    }

    public void setLastVersionName(String lastVersionName) {
        this.lastVersionName = lastVersionName;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getUpdateURL() {
        return updateURL;
    }

    public void setUpdateURL(String updateURL) {
        this.updateURL = updateURL;
    }

    public String getIsForced() {
        return isForced;
    }

    public void setIsForced(String isForced) {
        this.isForced = isForced;
    }

    public String getIsPrompt() {
        return isPrompt;
    }

    public void setIsPrompt(String isPrompt) {
        this.isPrompt = isPrompt;
    }

    public String getUpgradeInfoEn() {
        return upgradeInfoEn;
    }

    public void setUpgradeInfoEn(String upgradeInfoEn) {
        this.upgradeInfoEn = upgradeInfoEn;
    }

    public String getUpgradeInfoZh() {
        return upgradeInfoZh;
    }

    public void setUpgradeInfoZh(String upgradeInfoZh) {
        this.upgradeInfoZh = upgradeInfoZh;
    }

    public String getUpgradeInfoTw() {
        return upgradeInfoTw;
    }

    public void setUpgradeInfoTw(String upgradeInfoTw) {
        this.upgradeInfoTw = upgradeInfoTw;
    }

    public String getUpgradeInfoTai() {
        return upgradeInfoTai;
    }

    public void setUpgradeInfoTai(String upgradeInfoTai) {
        this.upgradeInfoTai = upgradeInfoTai;
    }

    @Override
    public String toString() {
        return "UpgradeBean{" +
                "appName='" + appName + '\'' +
                ", lastVersionName='" + lastVersionName + '\'' +
                ", versionName='" + versionName + '\'' +
                ", versionCode='" + versionCode + '\'' +
                ", updateURL='" + updateURL + '\'' +
                ", isForced='" + isForced + '\'' +
                ", isPrompt='" + isPrompt + '\'' +
                ", upgradeInfoEn='" + upgradeInfoEn + '\'' +
                ", upgradeInfoZh='" + upgradeInfoZh + '\'' +
                ", upgradeInfoTw='" + upgradeInfoTw + '\'' +
                ", upgradeInfoTai='" + upgradeInfoTai + '\'' +
                '}';
    }
}
