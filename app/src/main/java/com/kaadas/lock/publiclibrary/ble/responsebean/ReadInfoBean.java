package com.kaadas.lock.publiclibrary.ble.responsebean;

/**
 * Create By lxj  on 2019/1/22
 * Describe
 */
public class ReadInfoBean {
    public static final int TYPE_SN = 1;  //SN
    public static final int TYPE_MODE = 2;  ////模块版本信息
    public static final int TYPE_BATTERY =3;   //电量信息
    public static final int TYPE_SYSTEMID = 4;   //systemId
    public static final int TYPE_BLEINFO = 5;   //蓝牙版本
    public static final int TYPE_SERIAL_NUMBER = 6;   //序列号
    public static final int TYPE_FIRMWARE_REV = 7;   //锁型号
    public static final int TYPE_HARDWARE_REV = 8;   //硬件版本
    public static final int TYPE_SOFTWARE_REV = 9;   //软件版本
    public static final int TYPE_LOCK_TYPE = 10;
    public static final int TYPE_LOCK_FUN = 11;
    public static final int TYPE_LOCK_STATUS = 12;
    public int type;
    public Object data;

    public ReadInfoBean(int type, Object data) {
        this.type = type;
        this.data = data;
    }

    public ReadInfoBean() {

    }
}
