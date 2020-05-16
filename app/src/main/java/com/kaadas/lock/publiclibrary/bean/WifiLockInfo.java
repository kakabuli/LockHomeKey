package com.kaadas.lock.publiclibrary.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;

@Entity
public class WifiLockInfo implements Serializable {

    private static final long serialVersionUID = 3554656442545454L;

    /**
     * _id : 5de4c32a33cc1949441265ca
     * wifiSN : WF132231004
     * isAdmin : 1
     * adminUid : 5c4fe492dc93897aa7d8600b
     * adminName : 8618954359822
     * productSN : s10001192910010
     * productModel : k8
     * appId : 1
     * lockNickname : wode
     * lockSoftwareVersion : 22222
     * functionSet : 00
     * uid : 5c4fe492dc93897aa7d8600b
     * uname : 8618954359822
     * pushSwitch : 1
     * amMode : 1
     * safeMode : 1
     * powerSave : 1 节能模式开启 0 节能模式关闭
     * faceStatus :  0 面容识别开启 1 面容识别关闭
     * defences : 0
     * language : zh
     * operatingMode : 0
     * volume : 1
     * bleVersion : 33333
     * wifiVersion : 44444
     * mqttVersion : 55555
     * lockFirmwareVersion : 11111
     * randomCode : randomCode666
     * 设备类型	串	设备类型：gateway或者为空：网关wifi：WIFI锁
     * wifiPwdNum	串	临时密码编号
     * wifiPwd昵称	串	临时密码昵称
     * wifi密码	串	临时密码因子
     * pushSwtich	整型	按下开关：1关闭按下2打开推（兼容老数据替换为空，即空为开启推动）
     * devList	清单	蓝牙锁列表
     * 设备编号	串	设备SN
     * 外围设备编号	串	ios蓝牙UUID
     * 软件版本	串	固件版本号
     * bleVersion	串	蓝牙型号：1 2 3
     * functionSet	串	功能集
     * productInfoList	清单	产品型号信息列表
     * wifi列表	清单	wifi锁列表
     *
     */

    @Id(autoincrement = true)
    private Long id;

    private String wifiSN;
    private int isAdmin;
    private String adminUid;
    private String adminName;
    private String productSN;
    private String productModel;
    private int appId;
    private String lockNickname;
    private String lockSoftwareVersion;
    private String functionSet;
    private String uid;
    private String uname;
    private int pushSwitch;
    private int amMode;
    private int safeMode;
    private int powerSave;
    private int faceStatus;
    private int defences;
    private String language;
    private int operatingMode;
    private int volume;
    private String bleVersion;
    private String wifiVersion;
    private String mqttVersion;
    private String lockFirmwareVersion;
    private String randomCode;


    /**
     * _id : 5de4c32a33cc1949441265ca
     * wifiName : wodewifi
     */

    /**
     * createTime  创建时间
     */
    private long createTime;

    private String wifiName;
    /**
     * _id : 5de4c32a33cc1949441265ca
     * power : 55
     * updateTime : 1577176575
     */

    private int power;
    private long updateTime;
    /**
     * _id : 5de4c32a33cc1949441265ca
     * updateTime : 1577176575
     * createTime : 1577176575
     * openStatus : 2
     * openStatusTime : 1541468973
     */

    private int openStatus;
    private long openStatusTime;


@Generated(hash = 390651843)
public WifiLockInfo(Long id, String wifiSN, int isAdmin, String adminUid,
        String adminName, String productSN, String productModel, int appId,
        String lockNickname, String lockSoftwareVersion, String functionSet, String uid,
        String uname, int pushSwitch, int amMode, int safeMode, int powerSave,
        int faceStatus, int defences, String language, int operatingMode, int volume,
        String bleVersion, String wifiVersion, String mqttVersion,
        String lockFirmwareVersion, String randomCode, long createTime, String wifiName,
        int power, long updateTime, int openStatus, long openStatusTime) {
    this.id = id;
    this.wifiSN = wifiSN;
    this.isAdmin = isAdmin;
    this.adminUid = adminUid;
    this.adminName = adminName;
    this.productSN = productSN;
    this.productModel = productModel;
    this.appId = appId;
    this.lockNickname = lockNickname;
    this.lockSoftwareVersion = lockSoftwareVersion;
    this.functionSet = functionSet;
    this.uid = uid;
    this.uname = uname;
    this.pushSwitch = pushSwitch;
    this.amMode = amMode;
    this.safeMode = safeMode;
    this.powerSave = powerSave;
    this.faceStatus = faceStatus;
    this.defences = defences;
    this.language = language;
    this.operatingMode = operatingMode;
    this.volume = volume;
    this.bleVersion = bleVersion;
    this.wifiVersion = wifiVersion;
    this.mqttVersion = mqttVersion;
    this.lockFirmwareVersion = lockFirmwareVersion;
    this.randomCode = randomCode;
    this.createTime = createTime;
    this.wifiName = wifiName;
    this.power = power;
    this.updateTime = updateTime;
    this.openStatus = openStatus;
    this.openStatusTime = openStatusTime;
}

    @Generated(hash = 666757199)
    public WifiLockInfo() {
    }









    public String getWifiSN() {
        return wifiSN;
    }

    public void setWifiSN(String wifiSN) {
        this.wifiSN = wifiSN;
    }

    public int getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(int isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getAdminUid() {
        return adminUid;
    }

    public void setAdminUid(String adminUid) {
        this.adminUid = adminUid;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getProductSN() {
        return productSN;
    }

    public void setProductSN(String productSN) {
        this.productSN = productSN;
    }

    public String getProductModel() {
        return productModel;
    }

    public void setProductModel(String productModel) {
        this.productModel = productModel;
    }

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public String getLockNickname() {
        return lockNickname;
    }

    public void setLockNickname(String lockNickname) {
        this.lockNickname = lockNickname;
    }

    public String getLockSoftwareVersion() {
        return lockSoftwareVersion;
    }

    public void setLockSoftwareVersion(String lockSoftwareVersion) {
        this.lockSoftwareVersion = lockSoftwareVersion;
    }

    public String getFunctionSet() {
        return functionSet;
    }

    public void setFunctionSet(String functionSet) {
        this.functionSet = functionSet;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public int getPushSwitch() {
        return pushSwitch;
    }

    public void setPushSwitch(int pushSwitch) {
        this.pushSwitch = pushSwitch;
    }

    public int getAmMode() {
        return amMode;
    }

    public void setAmMode(int amMode) {
        this.amMode = amMode;
    }

    public int getSafeMode() {
        return safeMode;
    }

    public void setSafeMode(int safeMode) {
        this.safeMode = safeMode;
    }

    public int getPowerSave() {
        return powerSave;
    }

    public void setPowerSave(int powerSave) {
        this.powerSave = powerSave;
    }
    public int getFaceStatus() {
        return faceStatus;
    }

    public void setFaceStatus(int faceStatus) {
        this.faceStatus = faceStatus;
    }

    public int getDefences() {
        return defences;
    }

    public void setDefences(int defences) {
        this.defences = defences;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getOperatingMode() {
        return operatingMode;
    }

    public void setOperatingMode(int operatingMode) {
        this.operatingMode = operatingMode;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public String getBleVersion() {
        return bleVersion;
    }

    public void setBleVersion(String bleVersion) {
        this.bleVersion = bleVersion;
    }

    public String getWifiVersion() {
        return wifiVersion;
    }

    public void setWifiVersion(String wifiVersion) {
        this.wifiVersion = wifiVersion;
    }

    public String getMqttVersion() {
        return mqttVersion;
    }

    public void setMqttVersion(String mqttVersion) {
        this.mqttVersion = mqttVersion;
    }

    public String getLockFirmwareVersion() {
        return lockFirmwareVersion;
    }

    public void setLockFirmwareVersion(String lockFirmwareVersion) {
        this.lockFirmwareVersion = lockFirmwareVersion;
    }

    public String getRandomCode() {
        return randomCode;
    }

    public void setRandomCode(String randomCode) {
        this.randomCode = randomCode;
    }


    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    @Override
    public String toString() {
        return "WifiLockInfo{" +
                "id=" + id +
                ", wifiSN='" + wifiSN + '\'' +
                ", isAdmin=" + isAdmin +
                ", adminUid='" + adminUid + '\'' +
                ", adminName='" + adminName + '\'' +
                ", productSN='" + productSN + '\'' +
                ", productModel='" + productModel + '\'' +
                ", appId=" + appId +
                ", lockNickname='" + lockNickname + '\'' +
                ", lockSoftwareVersion='" + lockSoftwareVersion + '\'' +
                ", functionSet='" + functionSet + '\'' +
                ", uid='" + uid + '\'' +
                ", uname='" + uname + '\'' +
                ", pushSwitch=" + pushSwitch +
                ", amMode=" + amMode +
                ", safeMode=" + safeMode +
                ", powerSave='" + powerSave + '\'' +
                ", faceStatus='" + faceStatus + '\'' +
                ", defences=" + defences +
                ", language='" + language + '\'' +
                ", operatingMode=" + operatingMode +
                ", volume=" + volume +
                ", bleVersion='" + bleVersion + '\'' +
                ", wifiVersion='" + wifiVersion + '\'' +
                ", mqttVersion='" + mqttVersion + '\'' +
                ", lockFirmwareVersion='" + lockFirmwareVersion + '\'' +
                ", randomCode='" + randomCode + '\'' +
                '}';
    }

    public String getWifiName() {
        return wifiName;
    }

    public void setWifiName(String wifiName) {
        this.wifiName = wifiName;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }


    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getOpenStatus() {
        return openStatus;
    }


    public void setOpenStatus(int openStatus) {
        this.openStatus = openStatus;
    }

    public long getOpenStatusTime() {
        return openStatusTime;
    }

    public void setOpenStatusTime(long openStatusTime) {
        this.openStatusTime = openStatusTime;
    }

}
