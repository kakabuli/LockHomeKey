package com.kaadas.lock.mvp.view.wifilock;

import com.kaadas.lock.mvp.mvpbase.IBaseView;
import com.xm.sdk.struct.stream.AVStreamHeader;
import com.xmitech.sdk.MP4Info;

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

    //录屏回调
    void onStopRecordMP4CallBack(MP4Info mp4Info);

    //录屏开始回调
    void onstartRecordMP4CallBack();

    //开始视频回调
    void onVideoDataAVStreamHeader(AVStreamHeader paramAVStreamHeader);

    //麦克风权限未打开Manifest.permission.RECORD_AUDIO
    void recordAudidFailed();
}
