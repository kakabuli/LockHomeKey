package com.kaadas.lock.publiclibrary.ble.responsebean;

import android.bluetooth.BluetoothDevice;

/**
 * Create By lxj  on 2019/1/22
 * Describe
 */
public class BleStateBean {
    private boolean connected  = false;
    private BluetoothDevice device;
    /**
     * 1 最老的模块   透传
     * 2 中间的模块    现在的指令
     * 3 最新的模块   支持全功能1
     * 4 最新的模块   BLE&WIFI 模块
     */
    private int bleVersion = -1;

    public BleStateBean(boolean connectState, BluetoothDevice device,int bleVersion ) {
        this.bleVersion = bleVersion;
        this.connected = connectState;
        this.device = device;
    }

    public BleStateBean() {
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    public int getBleVersion() {
        return bleVersion;
    }

    public void setBleVersion(int bleVersion) {
        this.bleVersion = bleVersion;
    }
}
