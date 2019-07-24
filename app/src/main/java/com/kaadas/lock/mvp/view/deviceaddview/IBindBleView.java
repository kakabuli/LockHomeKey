package com.kaadas.lock.mvp.view.deviceaddview;


import com.kaadas.lock.mvp.mvpbase.IBaseView;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;

/**
 * Create By lxj  on 2019/1/8
 * Describe
 */
public interface IBindBleView extends IBaseView {
    /**
     * 绑定设备成功
     */
    void onBindSuccess(String deviceName);


    /**
     * 绑定设备失败
     */
    void onBindFailed(Throwable throwable);

    /**
     * 绑定设备失败
     */
    void onBindFailedServer(BaseResult result);

    /**
     * 收到入网信息  s收到pwd2
     */
    void onReceiveInNetInfo();


    /**
     * 收到退网信息
     */
    void onReceiveUnbind();

    /**
     *  解绑设备成功
     */
    void onUnbindSuccess();


    /**
     * 解绑设备失败
     */
    void onUnbindFailed(Throwable throwable);
    /**
     * 解绑设备失败  服务器返回错误码
     */
    void onUnbindFailedServer(BaseResult result);
    /**
     * 读取锁型号失败
     */
    void readLockTypeFailed(Throwable throwable);

    /**
     * 读取锁型号成功
     */
    void readLockTypeSucces();

    void  onDeviceStateChange(boolean isConnected);
    /**
     * 未知锁型号
     */
    void unknownFunctionSet(int functionSet);

    /**
     *  读取功能集成功
     */
    void readFunctionSetSuccess(int functionSet);

    /**
     *  读取功能集失败
     */
    void readFunctionSetFailed(Throwable throwable);
}
