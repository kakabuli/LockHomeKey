package com.kaadas.lock.mvp.view;


import com.kaadas.lock.mvp.mvpbase.ICheckOtaView;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;

/**
 * Created by David on 2019/3/14
 */
public interface IDeviceInfoView extends ICheckOtaView {
    /**
     * 软件版本
     */
    void SoftwareRevDataSuccess(String data);

    void SoftwareRevDataError(Throwable throwable);

    /**
     * 硬件版本
     */
    void HardwareRevDataSuccess(String data);

    void HardwareRevDataError(Throwable throwable);

    /**
     * 锁型号
     */
    void FirmwareRevDataSuccess(String data);

    void FirmwareRevDataError(Throwable throwable);

    /**
     * 序列号 sn
     */
    void SerialNumberDataSuccess(String data);


    /**
     * 修改设备昵称成功
     */
    void modifyDeviceNicknameSuccess();

    /**
     * 修改昵称异常
     */
    void modifyDeviceNicknameError(Throwable throwable);

    /**
     * 修改昵称失败
     */
    void modifyDeviceNicknameFail(BaseResult baseResult);


    /**
     * 读取设备信息结束
     */
    void readDeviceInfoEnd();

    /**
     * 读取设备信息失败
     * @param throwable
     */
    void readDeviceInfoFailed(Throwable throwable);

    /**
     * 读取子模块版本信息
     */
    void onReadModuleVersion(int moduleNumber,String version,int otaType);



    /**
     * 请求OTA  失败
     */
    void onRequestOtaFailed(Throwable throwable);

    /**
     * 请求OTA  成功
     */
    void onRequestOtaSuccess(String ssid,String password,String version,int number,int otaType,String filePath);



}
