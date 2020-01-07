package com.kaadas.lock.mvp.view.wifilock;

import com.kaadas.lock.mvp.mvpbase.IBaseView;

public interface IWifiLockApView extends IBaseView {

    void onConnectingWifi(String ssid);

}
