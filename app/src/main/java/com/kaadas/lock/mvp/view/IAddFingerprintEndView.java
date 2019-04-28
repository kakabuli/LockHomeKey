package com.kaadas.lock.mvp.view;

import com.kaadas.lock.mvp.mvpbase.IBleView;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;


/**
 * Create By lxj  on 2019/3/7
 * Describe
 */
public interface IAddFingerprintEndView extends IBleView {

    /**
     * 密码列表已满
     */
    void onPwdFull();

    /**
     * 设置指纹失败
     */
    void onSetFingerFailed(Throwable throwable);

    /**
     * 设置指纹成功  添加成功的用户编号
     */
    void onSetFingerSuccess(int userNumber);


    /**
     * 上传指纹昵称成功
     */
    void onUploadFingerSuccess(int number);

    /**
     * 上传指纹失败
     *
     * @param throwable
     */
    void onUploadFingerFailed(Throwable throwable);

    /**
     * 上传指纹
     *
     * @param result
     */
    void onUploadFingerFailedServer(BaseResult result);

    /**
     * 同步number失败
     */
    void onGetFingerNumberFailedFailed(Throwable throwable);


}
