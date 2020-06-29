package com.kaadas.lock.mvp.view.deviceaddview;

import com.kaadas.lock.mvp.mvpbase.IBaseView;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.BindGatewayResultBean;

import java.util.List;

public interface WiFiLockChooseToAddView extends IBaseView {

    //检索门锁型号成功：WiFi
    void searchLockProductSuccessForWiFi(String pairMode);

    //检索门锁型号成功：WiFi&BLE
    void searchLockProductSuccessForWiFiAndBLE(String pairMode);

    //检索门锁型号异常
    void searchLockProductThrowable();




}
