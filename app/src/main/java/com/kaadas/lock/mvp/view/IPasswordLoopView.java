package com.kaadas.lock.mvp.view;


import com.kaadas.lock.mvp.mvpbase.IBleView;
import com.kaadas.lock.publiclibrary.http.postbean.AddPasswordBean;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;

/**
 * Create By lxj  on 2019/3/12
 * Describe
 */
public interface IPasswordLoopView extends IBleView {

    /**
     * 密码库满了
     */
    void onPwdFull();

    /**
     * 开始设置密码
     */
    void startSetPwd();

    /**
     * 结束设置密码
     */
    void endSetPwd();

    /**
     * 设置密码成功
     *
     * @param password
     */
    void onSetPasswordSuccess(AddPasswordBean.Password password);

    /**
     * -
     * 设置密码失败
     *
     * @param throwable
     */
    void onSetPasswordFailed(Throwable throwable);

    /**
     * 设置周计划成功
     */
    void setWeekPlanSuccess();

    /**
     * 设置周计划失败
     *
     * @param throwable
     */
    void setWeekPlanFailed(Throwable throwable);

    /**
     * 设置用户类型成功
     */
    void setUserTypeSuccess();

    /**
     * 设置用户类型失败
     *
     * @param throwable
     */
    void setUserTypeFailed(Throwable throwable);

    /**
     * 上传密码成功
     */
    void onUploadPwdSuccess(String password, String number, String nickName);

    /**
     * 上传密码失败
     *
     * @param throwable
     */
    void onUploadPwdFailed(Throwable throwable);

    /**
     * 上传密码服务器返回错误码
     *
     * @param result
     */
    void onUploadPwdFailedServer(BaseResult result);

    /**
     * 同步密码错误
     */
    void onSyncPasswordFailed(Throwable throwable);
    /**
     *  时效密码满了
     */
    void onTimePwdFull();

}
