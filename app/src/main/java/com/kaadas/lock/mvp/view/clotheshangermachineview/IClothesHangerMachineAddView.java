package com.kaadas.lock.mvp.view.clotheshangermachineview;

import com.kaadas.lock.mvp.mvpbase.IBaseView;

public interface IClothesHangerMachineAddView extends IBaseView  {

    //检索门锁型号成功：WiFi
    void searchClothesMachineSuccessForWiFi(String pairMode);

    //检索门锁型号成功：WiFi&BLE
    void searchClothesMachineSuccessForWiFiAndBLE(String pairMode);

    //检索型号异常
    void searchClothesMachineThrowable();
}
