package com.kaadas.lock.mvp.view;

import com.kaadas.lock.mvp.mvpbase.IBleView;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;


/**
 * Create By lxj  on 2019/3/7
 * Describe
 */
public interface IAddCardEndView extends IBleView {

    /**
     * 密码列表已满
     */
    void onPwdFull();

    /**
     * 设置指纹失败
     */
    void onSetCardFailed(Throwable throwable);

    /**
     * 设置指纹成功  添加成功的用户编号
     */
    void onSetCardSuccess(int userNumber);


    /**
     * void onUploadPasswordNick  成功
     */
    void onUploadPasswordNickSuccess(String nickName, String number, String Password);

    /**
     * void onUploadPasswordNick  http失败
     */
    void onUploadPasswordNickFailed(Throwable throwable);

    /**
     * void onUploadPasswordNick 服务器失败
     */
    void onUploadPasswordNickFailedServer(BaseResult result);

    /**
     * 同步锁上编号失败
     */
    void syncNumberFailed(Throwable throwable);

}
