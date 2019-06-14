package com.kaadas.lock.mvp.view.deviceaddview;

import android.bluetooth.BluetoothDevice;

import com.kaadas.lock.mvp.mvpbase.IBaseView;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;

import java.util.List;

/**
 * Create By lxj  on 2019/1/7
 * Describe
 */
public interface ISearchDeviceView extends IBaseView {


    /**
     * 搜索到设备
     * @param devices
     */
    void loadDevices(List<BluetoothDevice> devices);


    /**
     * 设备已被绑定
     * @param device
     */
    void onAlreadyBind(BluetoothDevice device);

    /**
     * 设备没被绑定
     * @param device
     */
    void onNoBind(BluetoothDevice device);

    /**
     * 检查绑定状态失败
     */
    void onCheckBindFailed(Throwable throwable);

    /**
     * 停止搜索
     */
    void onStopScan();

    /**
     * 搜索失败
     */
    void onScanFailed(Throwable throwable);


    /**
     * 连接成功
     */
    void onConnectSuccess();

    /**
     * 连接成功   而且是最老的模块
     */
    void onConnectedAndIsOldMode(int version,boolean isBind);

    /**
     * 连接失败
     */
    void onConnectFailed();

    /**
     * 读取SN失败
     */

    void readSNFailed();


    /**
     * 正在连接
     */
    void onConnecting();


    /**
     * 获取pwd1
     */
    void getPwd1();

    /**
     * 获取pwd1成功
     */
    void getPwd1Success(String pwd1, boolean isBind,int version);


    /**
     * 获取pwd1失败
     */
    void getPwd1Failed(Throwable throwable);
    /**
     * 获取pwd1失败
     */
    void getPwd1FailedServer(BaseResult result);

    /**
     * 419提示
     */
    void notice419();


    /**
     * 获取是否绑定  服务器返回失败
     */
    void onCheckBindFailedServer(String code);
    /**
     * 检查绑定失败   网络异常
     */
    void checkBindFailed( );


}
