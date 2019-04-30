package com.kaadas.lock.mvp.view;


import com.kaadas.lock.mvp.mvpbase.IBleView;
import com.kaadas.lock.publiclibrary.http.result.GetPasswordResult;

import java.util.List;

/**
 * Create By lxj  on 2019/3/7
 * Describe
 */
public interface IFingerprintManagerView extends IBleView {

    /**
     * 后台获取到密码列表
     */
    void onServerDataUpdate();


    void startSync();

    void endSync();

    void onSyncPasswordSuccess(List<GetPasswordResult.DataBean.Fingerprint> pwdList);

    void onSyncPasswordFailed(Throwable throwable);

    void onUpdate(List<GetPasswordResult.DataBean.Fingerprint> pwdList);


}
