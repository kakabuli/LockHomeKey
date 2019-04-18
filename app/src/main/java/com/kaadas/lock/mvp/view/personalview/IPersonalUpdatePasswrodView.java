package com.kaadas.lock.mvp.view.personalview;


import com.kaadas.lock.mvp.mvpbase.IBaseView;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;

public interface IPersonalUpdatePasswrodView  extends IBaseView {

    void  updatePwdSuccess(String newPwd);

    void updatePwdError(Throwable throwable);


    void updatePwdFail(BaseResult baseResult);
}
