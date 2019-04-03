package com.kaadas.lock.bean;

import java.io.Serializable;

public class VersionBean implements Serializable {
    /**
     * appName : Philips
     * lastVersionName : 1.0.1
     * versionName : 1.0.2
     * versionCode : 111
     * updateURL : http://www.kaadas.com/app/Kaadas/Kaadas.apk
     * isForced : true
     * isPrompt : true
     * upgradeInfoEn : V1.0.2版本更新，你想不想要试一下哈！！！
     * upgradeInfoZh : V1.0.2版本更新，你想不想要试一下哈！！！
     * upgradeInfoTw : V1.0.2版本更新，你想不想要试一下哈！！！
     * upgradeInfoTai : V1.0.2版本更新，你想不想要试一下哈！！！
     */

    private String appName;
    private String lastVersionName;
    private String versionName;
    private int versionCode;
    private String updateURL;
    private boolean isForced;
    private boolean isPrompt;
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

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getUpdateURL() {
        return updateURL;
    }

    public void setUpdateURL(String updateURL) {
        this.updateURL = updateURL;
    }

    public boolean getIsForced() {
        return isForced;
    }

    public void setIsForced(boolean isForced) {
        this.isForced = isForced;
    }

    public boolean getIsPrompt() {
        return isPrompt;
    }

    public void setIsPrompt(boolean isPrompt) {
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
        return "VersionBean{" +
                "appName='" + appName + '\'' +
                ", lastVersionName='" + lastVersionName + '\'' +
                ", versionName='" + versionName + '\'' +
                ", versionCode=" + versionCode +
                ", updateURL='" + updateURL + '\'' +
                ", isForced=" + isForced +
                ", isPrompt=" + isPrompt +
                ", upgradeInfoEn='" + upgradeInfoEn + '\'' +
                ", upgradeInfoZh='" + upgradeInfoZh + '\'' +
                ", upgradeInfoTw='" + upgradeInfoTw + '\'' +
                ", upgradeInfoTai='" + upgradeInfoTai + '\'' +
                '}';
    }
}
