package com.kaadas.lock.publiclibrary.bean;


import com.kaadas.lock.publiclibrary.http.result.ServerDevice;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Create By lxj  on 2019/1/12
 * Describe
 */
public class BleLockInfo implements Serializable {
    private boolean isConnected = false;
    private int battery = -1; //-1为吗，没有获取到电量信息
    private boolean isAuth = false; //是否鉴权
    private byte[] authKey;  //是否鉴权
    private boolean isNew = false;  //是不是新模块
    private boolean notNeedPwd = false; //是不是需要密码  新模块才有可能不需要密码
    private String modeNumber;   // 这是蓝牙模块的型号
    private String firmware;//锁型号
    private String hardware; //硬件型号
    private String software; //软件型号
    private String SerialNumber;
    private int voice = -1;  // 音量   默认值为-1  表示没有获取到   0表示静音，非0表示不是静音
    private int safeMode = -1; //安全模式  0不启用或不支持   1已启动
    private int armMode = -1;  //布防模式 0不启用或不支持   1已启动
    private String lang;   //语言设置   zh 中文   en  英语
    private int autoMode = -1; // 手动自动模式  0手动  1自动
    private int doorState = -1; //门状态   0 lock  1 unlock
    private int backLock = -1; //反锁（独立锁舌）状态 0 lock 1 unlock
    private long readDeviceInfoTime = -1;  //读取锁信息的时间

    public String getSerialNumber() {
        return SerialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        SerialNumber = serialNumber;
    }

    /**
     * 重置设备信息
     */
    public void rease() {
        isConnected = false;
        battery = -1; //-1为吗，没有获取到电量信息
        isAuth = false; //是否鉴权
        authKey = null;  //是否鉴权
        isNew = false;  //是不是新模块
        notNeedPwd = false; //是不是需要密码  新模块才有可能不需要密码
    }

    private ServerDevice serverLockInfo;

    public BleLockInfo(ServerDevice serverLockInfo) {
        this.serverLockInfo = serverLockInfo;
    }

    public ServerDevice getServerLockInfo() {
        return serverLockInfo;
    }

    public void setServerLockInfo(ServerDevice serverLockInfo) {
        this.serverLockInfo = serverLockInfo;
    }


    public boolean isConnected() {
        return isConnected;
    }


    public boolean isNewMode() {
        if (serverLockInfo.getDevice_name().startsWith("KDS") || serverLockInfo.getDevice_name().startsWith("kdslock")) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 断开连接   初始化数据
     *
     * @param connected
     */
    public void setConnected(boolean connected) {
        isConnected = connected;
        if (!isConnected) {
            battery = -1; //-1为吗，没有获取到电量信息
            isAuth = false; //是否鉴权
        }

    }


    public byte[] getAuthKey() {
        return authKey;
    }

    public void setAuthKey(byte[] authKey) {
        this.authKey = authKey;
    }

    public int getBattery() {
        return battery;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }

    public boolean isAuth() {
        return isAuth;
    }

    public void setAuth(boolean auth) {
        isAuth = auth;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }


    public boolean isNotNeedPwd() {
        return notNeedPwd;
    }

    public void setNotNeedPwd(boolean notNeedPwd) {
        this.notNeedPwd = notNeedPwd;
    }


    public String getModeNumber() {
        return modeNumber;
    }

    public void setModeNumber(String modeNumber) {
        this.modeNumber = modeNumber;
    }

    public String getFirmware() {
        return firmware;
    }

    public void setFirmware(String firmware) {
        this.firmware = firmware;
    }

    public String getHardware() {
        return hardware;
    }

    public void setHardware(String hardware) {
        this.hardware = hardware;
    }

    public String getSoftware() {
        return software;
    }

    public void setSoftware(String software) {
        this.software = software;
    }


    public int getVoice() {
        return voice;
    }

    public void setVoice(int voice) {
        this.voice = voice;
    }

    public int getSafeMode() {
        return safeMode;
    }

    public void setSafeMode(int safeMode) {
        this.safeMode = safeMode;
    }

    public int getArmMode() {
        return armMode;
    }

    public void setArmMode(int armMode) {
        this.armMode = armMode;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public int getAutoMode() {
        return autoMode;
    }

    public void setAutoMode(int autoMode) {
        this.autoMode = autoMode;
    }

    public int getDoorState() {
        return doorState;
    }

    public void setDoorState(int doorState) {
        this.doorState = doorState;
    }

    public int getBackLock() {
        return backLock;
    }

    public void setBackLock(int backLock) {
        this.backLock = backLock;
    }

    public long getReadDeviceInfoTime() {
        return readDeviceInfoTime;
    }

    public void setReadDeviceInfoTime(long readDeviceInfoTime) {
        this.readDeviceInfoTime = readDeviceInfoTime;
    }


    @Override
    public String toString() {
        return "BleLockInfo{" +
                "isConnected=" + isConnected +
                ", battery=" + battery +
                ", isAuth=" + isAuth +
                ", authKey=" + Arrays.toString(authKey) +
                ", isNew=" + isNew +
                ", notNeedPwd=" + notNeedPwd +
                ", modeNumber='" + modeNumber + '\'' +
                ", firmware='" + firmware + '\'' +
                ", hardware='" + hardware + '\'' +
                ", software='" + software + '\'' +
                ", SerialNumber='" + SerialNumber + '\'' +
                ", voice=" + voice +
                ", safeMode=" + safeMode +
                ", armMode=" + armMode +
                ", lang='" + lang + '\'' +
                ", autoMode=" + autoMode +
                ", doorState=" + doorState +
                ", backLock=" + backLock +
                ", readDeviceInfoTime=" + readDeviceInfoTime +
                ", serverLockInfo=" + serverLockInfo +
                '}';
    }
}
