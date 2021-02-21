package com.kaadas.lock.publiclibrary.bean;

import com.google.gson.annotations.SerializedName;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.greenDao.convert.SingleFireSwitchInfoConvert;
import com.kaadas.lock.utils.greenDao.convert.WifiVideoAliveTimeBeanConvert;
import com.kaadas.lock.utils.greenDao.convert.WifiVideoLockSetPirConvert;
import com.yun.software.kaadas.UI.wxchat.PayResult;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;
import java.util.List;

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
     * faceVersion : 66666
     * lockFirmwareVersion : 11111
     * randomCode : randomCode666
     * distributionNetwork : 1
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
     * switch  单火开关数据
     */
    @Id(autoincrement = true)
    private Long id;

    @SerializedName("_id")
    private String deviceID;
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
    private String faceVersion;
    private String lockFirmwareVersion;
    private String randomCode;
    private int distributionNetwork;

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

    /**
     * 		"device_did": "AYIOTCN-000343-PMHXH",
     * 			"device_sn": "020020900003",
     * 			"p2p_password": "12345678",
     */
    private String device_did;
    private String device_sn;
    private String p2p_password;

    @Convert(converter = SingleFireSwitchInfoConvert.class , columnType = String.class)
    @SerializedName("switch")
    private SingleFireSwitchInfo singleFireSwitchInfo;

    @Convert(converter = WifiVideoLockSetPirConvert.class , columnType = String.class)
    private WifiVideoLockSetPirBean setPir;
    @Convert(converter = WifiVideoAliveTimeBeanConvert.class , columnType = String.class)
    private WifiVideoLockAliveTimeBean alive_time;
    private int stay_status;
    private String camera_version;
    private String mcu_version;
    private String device_model;

    private int keep_alive_status;

    private String mac;
    private String lockMac;
    private String RSSI;
    private int wifiStrength;

    @Generated(hash = 1135326471)
    public WifiLockInfo(Long id, String deviceID, String wifiSN, int isAdmin, String adminUid,
            String adminName, String productSN, String productModel, int appId,
            String lockNickname, String lockSoftwareVersion, String functionSet, String uid,
            String uname, int pushSwitch, int amMode, int safeMode, int powerSave,
            int faceStatus, int defences, String language, int operatingMode, int volume,
            String bleVersion, String wifiVersion, String mqttVersion, String faceVersion,
            String lockFirmwareVersion, String randomCode, int distributionNetwork,
            long createTime, String wifiName, int power, long updateTime, int openStatus,
            long openStatusTime, String device_did, String device_sn, String p2p_password,
            SingleFireSwitchInfo singleFireSwitchInfo, WifiVideoLockSetPirBean setPir,
            WifiVideoLockAliveTimeBean alive_time, int stay_status, String camera_version,
            String mcu_version, String device_model, int keep_alive_status, String mac,
            String lockMac, String RSSI, int wifiStrength) {
        this.id = id;
        this.deviceID = deviceID;
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
        this.faceVersion = faceVersion;
        this.lockFirmwareVersion = lockFirmwareVersion;
        this.randomCode = randomCode;
        this.distributionNetwork = distributionNetwork;
        this.createTime = createTime;
        this.wifiName = wifiName;
        this.power = power;
        this.updateTime = updateTime;
        this.openStatus = openStatus;
        this.openStatusTime = openStatusTime;
        this.device_did = device_did;
        this.device_sn = device_sn;
        this.p2p_password = p2p_password;
        this.singleFireSwitchInfo = singleFireSwitchInfo;
        this.setPir = setPir;
        this.alive_time = alive_time;
        this.stay_status = stay_status;
        this.camera_version = camera_version;
        this.mcu_version = mcu_version;
        this.device_model = device_model;
        this.keep_alive_status = keep_alive_status;
        this.mac = mac;
        this.lockMac = lockMac;
        this.RSSI = RSSI;
        this.wifiStrength = wifiStrength;
    }

    @Generated(hash = 666757199)
    public WifiLockInfo() {
    }

    public String getLockMac() {
        return lockMac;
    }

    public void setLockMac(String lockMac) {
        this.lockMac = lockMac;
    }

    public int getKeep_alive_status() {
        return keep_alive_status;
    }

    public void setKeep_alive_status(int keep_alive_status) {
        this.keep_alive_status = keep_alive_status;
    }

    public WifiVideoLockSetPirBean getSetPir() {
        return setPir;
    }

    public void setSetPir(WifiVideoLockSetPirBean setPir) {
        this.setPir = setPir;
    }

    public WifiVideoLockAliveTimeBean getAlive_time() {
        return alive_time;
    }

    public void setAlive_time(WifiVideoLockAliveTimeBean alive_time) {
        this.alive_time = alive_time;
    }

    public int getStay_status() {
        return stay_status;
    }

    public void setStay_status(int stay_status) {
        this.stay_status = stay_status;
    }

    public String getCamera_version() {
        return camera_version;
    }

    public void setCamera_version(String camera_version) {
        this.camera_version = camera_version;
    }

    public String getMcu_version() {
        return mcu_version;
    }

    public void setMcu_version(String mcu_version) {
        this.mcu_version = mcu_version;
    }

    public String getDevice_model() {
        return device_model;
    }

    public void setDevice_model(String device_model) {
        this.device_model = device_model;
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

    public String getFaceVersion() {
        return faceVersion;
    }

    public void setFaceVersion(String faceVersion) {
        this.faceVersion = faceVersion;
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

    public int getDistributionNetwork() {
        return distributionNetwork;
    }

    public void setDistributionNetwork(int distributionNetwork) {
        this.distributionNetwork = distributionNetwork;
    }

    @Override
    public String toString() {
        return "WifiLockInfo{" +
                "id=" + id +
                ", deviceID='" + deviceID + '\'' +
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
                ", powerSave=" + powerSave +
                ", faceStatus=" + faceStatus +
                ", defences=" + defences +
                ", language='" + language + '\'' +
                ", operatingMode=" + operatingMode +
                ", volume=" + volume +
                ", bleVersion='" + bleVersion + '\'' +
                ", wifiVersion='" + wifiVersion + '\'' +
                ", mqttVersion='" + mqttVersion + '\'' +
                ", faceVersion='" + faceVersion + '\'' +
                ", lockFirmwareVersion='" + lockFirmwareVersion + '\'' +
                ", randomCode='" + randomCode + '\'' +
                ", distributionNetwork=" + distributionNetwork +
                ", createTime=" + createTime +
                ", wifiName='" + wifiName + '\'' +
                ", power=" + power +
                ", updateTime=" + updateTime +
                ", openStatus=" + openStatus +
                ", openStatusTime=" + openStatusTime +
                ", device_did='" + device_did + '\'' +
                ", device_sn='" + device_sn + '\'' +
                ", p2p_password='" + p2p_password + '\'' +
                ", mac='" + mac + '\'' +
                ", singleFireSwitchInfo=" + singleFireSwitchInfo +
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

    public SingleFireSwitchInfo getSingleFireSwitchInfo() {

        return this.singleFireSwitchInfo;
    }

    public void setSingleFireSwitchInfo(SingleFireSwitchInfo singleFireSwitchInfo) {
        this.singleFireSwitchInfo = singleFireSwitchInfo;
    }



    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeviceID() {
        return this.deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }


    public String getDevice_did() {
        return device_did;
    }

    public void setDevice_did(String device_did) {
        this.device_did = device_did;
    }

    public String getDevice_sn() {
        return device_sn;
    }

    public void setDevice_sn(String device_sn) {
        this.device_sn = device_sn;
    }

    public String getP2p_password() {
        return p2p_password;
    }

    public void setP2p_password(String p2p_password) {
        this.p2p_password = p2p_password;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getRSSI() {
        return this.RSSI;
    }

    public void setRSSI(String RSSI) {
        this.RSSI = RSSI;
    }

    public int getWifiStrength() {
        return this.wifiStrength;
    }

    public void setWifiStrength(int wifiStrength) {
        this.wifiStrength = wifiStrength;
    }
}
