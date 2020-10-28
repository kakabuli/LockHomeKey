package com.kaadas.lock.mvp.view.wifilock;

import com.kaadas.lock.mvp.mvpbase.IBaseView;
import com.xm.sdk.struct.stream.AVStreamHeader;
import com.xmitech.sdk.H264Frame;
import com.xmitech.sdk.MP4Info;

public interface IMyAlbumPlayerView extends IBaseView {


    //连接失败，失败code，关闭资源
    void onConnectFailed(int paramInt);
    //连接成功
    void onConnectSuccess();
    //连接状态信息
    void onStartConnect(String paramString);
    //连接失败，错误信息
    void onErrorMessage(String message);

    void onVideoDataAVStreamHeader(AVStreamHeader paramAVStreamHeader);

    void onVideoFrameUsed(H264Frame h264Frame);

    void onStopRecordMP4CallBack(MP4Info mp4Info, String name);

    void onstartRecordMP4CallBack();

    void onSuccessRecord(boolean b);
}
