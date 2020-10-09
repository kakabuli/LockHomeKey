package com.kaadas.lock.mvp.view.wifilock;

import com.kaadas.lock.mvp.mvpbase.IBaseView;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.WifiLockVideoBindBean;

public interface IWifiLockVideoFifthView extends IBaseView {

    void onDeviceBinding(WifiLockVideoBindBean mWifiLockVideoBindBean);

}
