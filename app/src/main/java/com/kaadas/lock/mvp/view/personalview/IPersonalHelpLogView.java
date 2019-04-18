package com.kaadas.lock.mvp.view.personalview;


import com.kaadas.lock.mvp.mvpbase.IBaseView;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.GetHelpLogResult;

public interface IPersonalHelpLogView extends IBaseView {

    void getHelpLogSuccess(GetHelpLogResult getHelpLogResult);

    void getHelpLogError(Throwable throwable);


    void getHelpLogFail(BaseResult baseResult);
}
