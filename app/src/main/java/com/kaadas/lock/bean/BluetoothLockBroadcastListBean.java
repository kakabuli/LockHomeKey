package com.kaadas.lock.bean;

import android.bluetooth.BluetoothDevice;

import java.util.List;

public class BluetoothLockBroadcastListBean {

    List<BluetoothLockBroadcastBean> list;
    List<BluetoothDevice> devices ;

    public BluetoothLockBroadcastListBean(List<BluetoothLockBroadcastBean> list, List<BluetoothDevice> devices) {

        this.list = list;
        this.devices = devices;

    }
    public List<BluetoothLockBroadcastBean> getList() {
        return list;
    }

    public void setList(List<BluetoothLockBroadcastBean> list) {
        this.list = list;
    }

    public List<BluetoothDevice> getDevices() {
        return devices;
    }

    public void setDevices(List<BluetoothDevice> devices) {
        this.devices = devices;
    }

}
