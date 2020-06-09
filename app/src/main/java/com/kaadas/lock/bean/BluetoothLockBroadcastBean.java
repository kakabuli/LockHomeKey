package com.kaadas.lock.bean;

import android.bluetooth.BluetoothDevice;

public class BluetoothLockBroadcastBean {

    private BluetoothDevice device;

    private String originalData;

    private String deviceName;

    private String deviceSN;

    private String deviceMAC;

    private String deviceModel;

    private int bindingType;

    private int productType;

//    public BluetoothLockBroadcastBean(BluetoothDevice device, String originalData, String deviceName, String deviceSN, String deviceMAC, String deviceModel, int bindingType) {
//
//        this.device = device;
//        this.originalData = originalData;
//        this.deviceName = deviceName;
//        this.deviceSN = deviceSN;
//        this.deviceMAC = deviceMAC;
//        this.deviceModel = deviceModel;
//        this.bindingType = bindingType;
//
//    }


    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    public String getOriginalData() {
        return originalData;
    }

    public void setOriginalData(String originalData) {
        this.originalData = originalData;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceSN() {
        return deviceSN;
    }

    public void setDeviceSN(String deviceSN) {
        this.deviceSN = deviceSN;
    }

    public String getDeviceMAC() {
        return deviceMAC;
    }

    public void setDeviceMAC(String deviceMAC) {
        this.deviceMAC = deviceMAC;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public int getBindingType() {
        return bindingType;
    }

    public void setBindingType(int bindingType) {
        this.bindingType = bindingType;
    }

    public int getProductType() {
        return productType;
    }

    public void setProductType(int productType) {
        this.productType = productType;
    }

}
