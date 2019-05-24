package com.kaadas.lock.mvp.view;


import com.kaadas.lock.mvp.mvpbase.IBleView;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.OTAResult;

/**
 * Create By lxj  on 2019/3/4
 * Describe
 */
public interface IDeviceMoreView extends IBleView {

    void onDeleteDeviceSuccess();

    void onDeleteDeviceFailed(Throwable throwable);

    void onDeleteDeviceFailedServer(BaseResult result);

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
     * 获取是否开启声音
     */

    void getVoice(int voice);


    void setVoiceSuccess(int voice);

    void setVoiceFailed(Throwable throwable, int voice);

    /**
     * 获取是否开启自动关门
     */

    void getAutoLock(boolean isOpen);


    void setAutoLockSuccess(boolean isOpen);

    void setAutoLockFailed( byte b);

    void setAutoLockError(Throwable throwable);

    /**
     * 读取SN成功
     */
    void readSnSuccess(String sn);

    /**
     * 读取蓝牙模块信息失败
     */
    void readInfoFailed(Throwable throwable);
    /**
     * 读取版本成功
     */
    void readVersionSuccess(String version);

    /**
     * 查询有OTA升级
     */
    void needUpdate(OTAResult.UpdateFileInfo updateFileInfo, String SN, String version);

    /**
     * 请求OTA错误
     */
    void notNeedUpdate(String errorCode );





    void onStateUpdate(int type);

}
