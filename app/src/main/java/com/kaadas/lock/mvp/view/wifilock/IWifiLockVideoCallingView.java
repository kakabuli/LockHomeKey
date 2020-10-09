package com.kaadas.lock.mvp.view.wifilock;

import com.kaadas.lock.mvp.mvpbase.IBaseView;

public interface IWifiLockVideoCallingView extends IBaseView {

    //连接失败，失败code，关闭资源
    void onConnectFailed(int paramInt);
    //连接成功
    void onConnectSuccess();
    //连接状态信息
    void onStartConnect(String paramString);
    //连接失败，错误信息
    void onErrorMessage(String message);
    //通知设备OTA更新
   // void onNotifyGateWayNewVersion(String paramString);
    //重启设备
    //void onRebootDevice(String paramString);

    //截图
    void onLastFrameRgbData(int[] ints, int height, int width, boolean b);
}
