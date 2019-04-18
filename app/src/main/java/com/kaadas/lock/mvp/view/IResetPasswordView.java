package com.kaadas.lock.mvp.view;

import com.kaadas.lock.mvp.mvpbase.IBaseView;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;


public interface IResetPasswordView extends IBaseView {
    void senRandomSuccess();

    void resetPasswordSuccess();


    void sendRandomFailed(Throwable e);

    void resetPasswordFailed(Throwable e);

    void sendRandomFailedServer(BaseResult result);

    void resetPasswordFailedServer(BaseResult result);
}
