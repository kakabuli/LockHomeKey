package com.kaadas.lock.publiclibrary.ble.responsebean;

import android.bluetooth.BluetoothDevice;

/**
 * Create By lxj  on 2019/1/22
 * Describe
 */
public class BleStateBean {
    private boolean connected  = false;
    private BluetoothDevice device;

    public BleStateBean(boolean connectState, BluetoothDevice device ) {
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


}
