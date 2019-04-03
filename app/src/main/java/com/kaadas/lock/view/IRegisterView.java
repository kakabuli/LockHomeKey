package com.kaadas.lock.view;


import com.kaadas.lock.base.mvpbase.IBaseView;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;


public interface IRegisterView extends IBaseView {
    void sendRandomSuccess();

    void registerSuccess();


    void sendRandomFailed(Throwable e);
    void sendRandomFailedServer(BaseResult result);

    void registerFailed(Throwable e);
    void registerFailedServer(BaseResult result);
}
