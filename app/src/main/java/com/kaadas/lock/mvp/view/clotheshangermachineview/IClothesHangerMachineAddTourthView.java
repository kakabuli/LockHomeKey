package com.kaadas.lock.mvp.view.clotheshangermachineview;

import android.bluetooth.BluetoothDevice;

import com.kaadas.lock.bean.BluetoothLockBroadcastBean;
import com.kaadas.lock.bean.BluetoothLockBroadcastListBean;
import com.kaadas.lock.mvp.mvpbase.IBaseView;

import java.util.List;

public interface IClothesHangerMachineAddTourthView extends IBaseView {


    void onDeviceStateChange(boolean isConnected);

}
