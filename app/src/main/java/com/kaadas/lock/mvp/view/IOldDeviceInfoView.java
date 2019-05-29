package com.kaadas.lock.mvp.view;


import com.kaadas.lock.mvp.mvpbase.IBleView;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.OTAResult;

/**
 * Created by David on 2019/3/14
 */
public interface IOldDeviceInfoView extends IBleView {
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

    void SerialNumberDataError(Throwable throwable);

    /**
     * 模块代号
     */
    void ModelNumberDataSuccess(String data);

    void ModelNumberDataError(Throwable throwable);

    /**
     * 无升级配置
     */
    void noUpdateConfig();

    /**
     * 需要升级
     */
    void needUpdate(OTAResult.UpdateFileInfo updateFileInfo);

    /**
     * 需要升级
     */
    void checkInfoFailed(String errorCode);

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
}
