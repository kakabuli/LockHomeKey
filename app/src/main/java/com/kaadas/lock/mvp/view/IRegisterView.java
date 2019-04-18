package com.kaadas.lock.mvp.view;


import com.kaadas.lock.mvp.mvpbase.IBaseView;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;


public interface IRegisterView extends IBaseView {
    void sendRandomSuccess();

    void registerSuccess();


    void sendRandomFailed(Throwable e);
    void sendRandomFailedServer(BaseResult result);

    void registerFailed(Throwable e);
    void registerFailedServer(BaseResult result);
}
