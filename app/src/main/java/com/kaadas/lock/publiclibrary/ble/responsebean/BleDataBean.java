package com.kaadas.lock.publiclibrary.ble.responsebean;

import android.bluetooth.BluetoothDevice;

/**
 * Create By lxj  on 2019/1/22
 * Describe  收到蓝牙数据的通用返回类
 */
public class BleDataBean {
    private byte cmd; //指令
    private byte tsn; //TSN码
    private byte[] originalData; //原始数据s
    private Object parseObject;   //解析的数据
    private BluetoothDevice device;

    public BleDataBean() {
    }

    public BleDataBean(byte cmd, byte tsn, byte[] originalData, Object parseObject,BluetoothDevice device) {
        this.cmd = cmd;
        this.tsn = tsn;
        this.originalData = originalData;
        this.parseObject = parseObject;
        this.device = device;
    }

    public BleDataBean(byte cmd, byte tsn, byte[] originalData) {
        this.cmd = cmd;
        this.tsn = tsn;
        this.originalData = originalData;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    public byte getCmd() {
        return cmd;
    }

    public void setCmd(byte cmd) {
        this.cmd = cmd;
    }

    public byte getTsn() {
        return tsn;
    }

    public void setTsn(byte tsn) {
        this.tsn = tsn;
    }

    public byte[] getOriginalData() {
        return originalData;
    }

    public void setOriginalData(byte[] originalData) {
        this.originalData = originalData;
    }

    public Object getParseObject() {
        return parseObject;
    }

    public void setParseObject(Object parseObject) {
        this.parseObject = parseObject;
    }

    public byte[] getPayload(){
        byte[] payload = new byte[16];
        System.arraycopy(originalData, 4,payload , 0, 16);
        return payload;
    }

    /**
     * 是不是确认帧
     * @return
     */
    public boolean isConfirm(){
        return originalData[0] == 0;  //
    }
}
