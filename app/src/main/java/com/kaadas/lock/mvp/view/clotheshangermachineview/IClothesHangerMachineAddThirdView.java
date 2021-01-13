package com.kaadas.lock.mvp.view.clotheshangermachineview;

import android.bluetooth.BluetoothDevice;

import com.kaadas.lock.bean.BluetoothLockBroadcastBean;
import com.kaadas.lock.bean.BluetoothLockBroadcastListBean;
import com.kaadas.lock.mvp.mvpbase.IBaseView;

import java.util.List;

public interface IClothesHangerMachineAddThirdView extends IBaseView {

    /**
     * 搜索到设备，带更多的信息
     * @param devices
     * @param broadcastList
     */
    void loadBLEWiFiModelDevices(List<BluetoothDevice> devices, List<BluetoothLockBroadcastListBean> broadcastList);

    /**
     * 停止搜索
     */
    void onStopScan();

    /**
     * 搜索失败
     */
    void onScanDevicesFailed(Throwable throwable);

    /**
     * 连接失败
     */
    void onConnectFailed();

    /**
     * 设备已被绑定
     * @param device
     */
    void onAlreadyBind(BluetoothDevice device,String uName);

    /**
     * 设备没被绑定
     * @param device
     */
    void onNoBind(BluetoothDevice device);

    /**
     * 检查绑定状态失败
     */
    void onCheckBindFailed(Throwable throwable);

    /**
     * 检查绑定失败   网络异常
     */
    void checkBindFailed( );


    void onConnectBLEWIFISuccess(BluetoothLockBroadcastBean broadcastBean, int bleVersion);
}
