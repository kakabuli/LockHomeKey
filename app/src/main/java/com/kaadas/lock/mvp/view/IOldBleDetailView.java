package com.kaadas.lock.mvp.view;


import com.kaadas.lock.mvp.mvpbase.IBleView;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;

/**
 * Create By lxj  on 2019/2/27
 * Describe
 */
public interface IOldBleDetailView extends IBleView {


    void onElectricUpdata(Integer electric);

    void onElectricUpdataFailed(Throwable throwable);

    /**
     * 获取到版本号
     * @param version
     */
    void onBleVersionUpdate(int version);

//    void onStateUpdate(int type);

    /**
     * 删除设备成功
     */
    void onDeleteDeviceSuccess();

    /**
     * 删除设备失败
     * @param throwable
     */
    void onDeleteDeviceFailed(Throwable throwable);
    /**
     * 删除设备失败
     * @param result
     */
    void onDeleteDeviceFailedServer(BaseResult result);


    /**
     * 加载到设备信息
     */
    void onDeviceInfoLoaded();


}
