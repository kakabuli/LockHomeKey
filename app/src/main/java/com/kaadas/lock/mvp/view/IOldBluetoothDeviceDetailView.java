package com.kaadas.lock.mvp.view;


/**
 * Create By lxj  on 2019/2/27
 * Describe
 */
public interface IOldBluetoothDeviceDetailView extends IOldBleLockDetailView {


    void onElectricUpdata(Integer electric);

    void onElectricUpdataFailed(Throwable throwable);


//    void onStateUpdate(int type);


}
