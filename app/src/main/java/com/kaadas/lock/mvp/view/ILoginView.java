package com.kaadas.lock.mvp.view;

import com.kaadas.lock.mvp.mvpbase.IBaseView;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;


/**
 * Create By lxj  on 2019/1/9
 * Describe
 */
public interface ILoginView extends IBaseView {

    void onLoginSuccess();

    void onLoginFailed(Throwable e);

    void onLoginFailedServer(BaseResult result);



}
