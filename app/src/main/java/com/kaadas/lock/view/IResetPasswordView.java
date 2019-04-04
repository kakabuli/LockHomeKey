package com.kaadas.lock.view;

import com.kaadas.lock.base.mvpbase.IBaseView;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;


public interface IResetPasswordView extends IBaseView {
    void senRandomSuccess();

    void resetPasswordSuccess();


    void sendRandomFailed(Throwable e);

    void resetPasswordFailed(Throwable e);

    void sendRandomFailedServer(BaseResult result);

    void resetPasswordFailedServer(BaseResult result);
}
