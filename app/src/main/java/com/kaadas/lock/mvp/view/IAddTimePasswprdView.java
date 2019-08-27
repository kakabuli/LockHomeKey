package com.kaadas.lock.mvp.view;


import com.kaadas.lock.mvp.mvpbase.IBleView;
import com.kaadas.lock.publiclibrary.http.postbean.AddPasswordBean;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;

/**
 * Create By lxj  on 2019/3/9
 * Describe
 */
public interface IAddTimePasswprdView extends IBleView {

    /**
     * 设置用户类型成功
     */
    void onSetUserTypeSuccess();

    /**
     * 设置用户类型失败
     *
     * @param throwable
     */
    void onSetUserTypeFailed(Throwable throwable);


    /**
     * 设置时间计划成功
     */
    void onSetTimePlanSuccess();

    /**
     * 设置时间计划失败
     */
    void onSetTimePlanFailed(Throwable throwable);

    /**
     * 设置密码成功
     *
     * @param password
     */
    void onSetPasswordSuccess(AddPasswordBean.Password password);

    /**
     * 设置密码失败
     *
     * @param throwable
     */
    void onSetPasswordFailed(Throwable throwable);


    /**
     * 密码满了
     */
    void onPwdFull();

    /**
     * 上传密码到服务器成功
     */
    void onUploadSuccess(String password, String number, String nickName);

    /**
     * 上传密码到服务器失败
     */
    void onUploadFailed(Throwable throwable);

    /**
     * 上传密码到服务器失败
     */
    void onUploadFailedServer(BaseResult result);

    /**
     * 开始设置  显示圈圈
     */
    void startSetPwd();


    /**
     * 结束设置  隐藏圈圈
     */
    void endSetPwd();

    /**
     * 同步密码错粗
     */
    void onSyncPasswordFailed(Throwable throwable);



    /**
     *  时效密码满了
     */
    void onTimePwdFull();
}
