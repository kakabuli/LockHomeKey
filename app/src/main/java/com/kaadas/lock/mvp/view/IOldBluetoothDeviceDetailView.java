package com.kaadas.lock.mvp.view;


import com.kaadas.lock.publiclibrary.http.result.BaseResult;

/**
 * Create By lxj  on 2019/2/27
 * Describe
 */
public interface IOldBluetoothDeviceDetailView extends IOldBleLockDetailView {


    void onElectricUpdata(Integer electric);

    void onElectricUpdataFailed(Throwable throwable);

    /**
     * 获取到版本号
     * @param version
     */
    void onBleVersionUpdate(int version);

//    void onStateUpdate(int type);

    void onDeleteDeviceSuccess();

    void onDeleteDeviceFailed(Throwable throwable);

    void onDeleteDeviceFailedServer(BaseResult result);
}
