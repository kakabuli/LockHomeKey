package com.kaadas.lock.mvp.view.wifilock;

import com.kaadas.lock.mvp.mvpbase.IBaseView;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;

public interface IWifiLockAddSuccessView extends IBaseView {
    void onSetNameSuccess(); //设置名称成功
    void onSetNameFailedNet(Throwable throwable); //设置名称网络异常
    void onSetNameFailedServer(BaseResult baseResult);  //设置名称  服务器返回错误


}
