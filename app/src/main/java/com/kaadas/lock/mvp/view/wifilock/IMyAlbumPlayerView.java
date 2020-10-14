package com.kaadas.lock.mvp.view.wifilock;

import com.kaadas.lock.mvp.mvpbase.IBaseView;

public interface IMyAlbumPlayerView extends IBaseView {


    //连接失败，失败code，关闭资源
    void onConnectFailed(int paramInt);
    //连接成功
    void onConnectSuccess();
    //连接状态信息
    void onStartConnect(String paramString);
    //连接失败，错误信息
    void onErrorMessage(String message);

}
