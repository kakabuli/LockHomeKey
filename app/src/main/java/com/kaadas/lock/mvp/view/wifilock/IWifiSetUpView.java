package com.kaadas.lock.mvp.view.wifilock;

import com.kaadas.lock.mvp.mvpbase.IBaseView;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;

public interface IWifiSetUpView extends IBaseView {
    void connectFailed(Throwable throwable);

    void startConnect();

    void readSuccess(String sn,byte[] password);
    /**
     *  读取失败
     * @param errorCode  -1 长度不够  -2 校验和错误   -3 求Sha256出错  -4读取数据失败
     */
    void readFailed( int errorCode);

    /**
     * 绑定成功
     */

    void onBindSuccess(String wifiSn);

    /**
     * 绑定失败
     */
    void onBindFailed(BaseResult baseResult);

    /**
     * 绑定异常
     */
    void onBindThrowable(Throwable throwable);



    /**
     * 绑定成功
     */

    void onUpdateSuccess(String wifiSn);

    /**
     * 绑定失败
     */
    void onUpdateFailed(BaseResult baseResult);

    /**
     * 绑定异常
     */
    void onUpdateThrowable(Throwable throwable);


    void onCheckError(byte[] data);

}
