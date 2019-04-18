package com.kaadas.lock.publiclibrary.ble.responsebean;

import android.bluetooth.BluetoothDevice;

/**
 * Create By lxj  on 2019/1/22
 * Describe
 */
public class BleStateBean {
    private boolean connected  = false;
    private BluetoothDevice device;
    private boolean isNew;

    public BleStateBean(boolean connectState, BluetoothDevice device, boolean isNew) {
        this.connected = connectState;
        this.device = device;
        this.isNew = isNew;
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

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }
}
