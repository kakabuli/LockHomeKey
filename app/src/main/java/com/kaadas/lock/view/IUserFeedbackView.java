package com.kaadas.lock.view;

import com.kaadas.lock.base.mvpbase.IBaseView;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;


/**
 * Create By lxj  on 2019/3/4
 * Describe
 */
public interface IUserFeedbackView extends IBaseView {

    void userFeedbackSubmitSuccess();

    void userFeedbackSubmitFailed(Throwable throwable);

    void userFeedbackSubmitFailedServer(BaseResult result);

}
