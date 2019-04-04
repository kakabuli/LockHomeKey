package com.kaadas.lock.view.personalview;


import com.kaadas.lock.base.mvpbase.IBaseView;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.GetHelpLogResult;

public interface IPersonalHelpLogView extends IBaseView {

    void getHelpLogSuccess(GetHelpLogResult getHelpLogResult);

    void getHelpLogError(Throwable throwable);


    void getHelpLogFail(BaseResult baseResult);
}
