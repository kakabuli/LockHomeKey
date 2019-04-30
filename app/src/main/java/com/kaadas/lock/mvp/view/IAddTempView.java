package com.kaadas.lock.mvp.view;


import com.kaadas.lock.mvp.mvpbase.IBleView;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;

/**
 * Create By lxj  on 2019/2/27
 * Describe
 */
public interface IAddTempView extends IBleView {


    /**
     * 开始设置密码
     */
    void onStartSetPwd();

    /**
     * 设置密码结束
     */
    void onEndSetPwd();

    /**
     * 设置密码失败
     */
    void onSetPwdFailed(Throwable throwable);


    /**
     * 设置密码失败
     */
    void onSetPwdFailedServer(BaseResult result);

    /**
     * 设置密码成功
     */
    void onSetPwdSuccess();

    /**
     * 正在保存到云端   跟onSetPwdSuccess同时执行
     */
    void onUploadToServer();

    /**
     * 上传服务器成功
     */
    void onUpLoadSuccess(String password, String number, String nickName);


    /**
     * 上传服务器失败
     */
    void onUploadFailed(Throwable throwable);

    /**
     * 密码库已满
     */

    void onPwdFull();

    /**
     * 同步密码失败
     */

    void onSyncPasswordFailed(Throwable throwable);

}
