package com.kaadas.lock.adapter.inf;

import android.bluetooth.BluetoothDevice;
import android.view.View;

public interface OnBindClickListener {
    void onItemClickListener(View view, int position, BluetoothDevice device);
}
