package com.kaadas.lock.mvp.view;

import com.kaadas.lock.mvp.mvpbase.IBaseView;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;


/**
 * Create By lxj  on 2019/3/4
 * Describe
 */
public interface ISystemSettingView extends IBaseView {

    void onLoginOutSuccess();

    void onLoginOutFailed(Throwable throwable);

    void onLoginOutFailedServer(BaseResult result);

}
