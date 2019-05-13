package com.kaadas.lock.mvp.mvpbase;

import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.GetPasswordResult;

/**
 * Create By lxj  on 2019/3/2
 * Describe
 */
public interface IBleView extends IBaseView {


    /**
     * 蓝牙没有打开
     * 提示用户没有打开蓝牙
     * 打开蓝牙将连接设备
     */
    void onBleOpenStateChange(boolean isOpen);

    /**
     * 如果连接成功
     */
    void onDeviceStateChange(boolean isConnected);
    

    /**
     * 查找设备失败   如果异常类型为NotFondDeviceException
     * 表示没有找到该设备，通常情况为不在蓝牙设备旁边
     */
    void onStartSearchDevice();

    /**
     * 查找设备失败   如果异常类型为NotFondDeviceException
     * 表示没有找到该设备，通常情况为不在蓝牙设备旁边
     */
    void onSearchDeviceFailed(Throwable throwable);


    /**
     * 查找设备失败   如果异常类型为NotFondDeviceException
     * 表示没有找到该设备，通常情况为不在蓝牙设备旁边
     */
    void onSearchDeviceSuccess();

    /**
     * 锁需要重新绑定的情况
     * * 情况2、  BLE模块返鉴权确认帧不是0
     * *0x00	成功
     * * 0x01	失败（5s后模块主动断开连接）
     * * 0x7E	未绑定（Password2为空）    需要重绑
     * * 0x91	鉴权内容不匹配    需要重绑
     * * 0x9A	命令正在执行（TSN重复）
     * * 0xC0	硬件错误（BLEModel解密功能出错）    需要重绑
     * * 0xC2	校验错误（一般是Password2被修改）    需要重绑
     */
    void onNeedRebind(int errorCode);

    /**
     * 鉴权成功
     */
    void authResult(boolean isSuccess);

    /**
     * 开始连接设备
     */
    void onStartConnectDevice();


    /**
     * 结束连接设备
     */
    void onEndConnectDevice(boolean isSuccess);


    /**
     * 请求密码列表成功
     * @param result
     */
    void onGetPasswordSuccess(GetPasswordResult result);

    /**
     * 请求密码列表失败 服务器返回失败
     * @param result
     */
    void onGetPasswordFailedServer(BaseResult result);

    /**
     * 请求密码列表失败
     */
    void onGetPasswordFailed(Throwable throwable);


    /**
     * 没有权限
     */
    void noPermissions();


}
