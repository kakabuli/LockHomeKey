package com.kaadas.lock.mvp.view;

import com.kaadas.lock.mvp.mvpbase.IBleView;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;


/**
 * Create By lxj  on 2019/1/28
 * Describe
 */
public interface IPasswordDetailView extends IBleView {

    /**
     * 删除密码成功
     */
    void onDeletePwdSuccess();

    /**
     * 删除密码失败
     */
    void onDeletePwdFailed(Throwable throwable);

    /**
     * 删除服务器密码成功
     */
    void onDeleteServerPwdSuccess();

    /**
     * 删除服务器密码失败
     *
     * @param throwable
     */
    void onDeleteServerPwdFailed(Throwable throwable);

    /***
     * 删除服务器密码失败  服务器返回错误码
     * @param result
     */
    void onDeleteServerPwdFailedServer(BaseResult result);

    /**
     * 更新昵称成功
     */
    void updateNickNameSuccess(String nickName);

    /**
     * 更新昵称失败
     */
    void updateNickNameFailed(Throwable throwable);

    /**
     * 更新昵称失败，服务器返回错误码
     */
    void updateNickNameFailedServer(BaseResult result);

    /**
     * 锁上没有该密码
     */
    void onLockNoThisNumber();

    /**
     * 获取锁上秘钥列表失败
     */
    void onGetLockNumberFailed(Throwable throwable);
}
