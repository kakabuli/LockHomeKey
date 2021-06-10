package com.kaadas.lock.adapter.inf;

import android.bluetooth.BluetoothDevice;
import android.view.View;

import com.kaadas.lock.bean.BluetoothLockBroadcastBean;

public interface OnHangerBindClickListener {
    void onItemClickListener(View view, int position, BluetoothLockBroadcastBean device);
}
