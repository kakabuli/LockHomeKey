package com.kaadas.lock.mvp.view.wifilock.x9;

import com.kaadas.lock.mvp.mvpbase.IBaseView;

public interface IWifiLockOpenDirectionView extends IBaseView  {

    void settingThrowable(Throwable throwable);

    void settingFailed();

    void settingSuccess(int openDirection);


}
