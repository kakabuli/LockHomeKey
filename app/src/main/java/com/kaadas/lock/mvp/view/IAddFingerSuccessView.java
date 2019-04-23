package com.kaadas.lock.mvp.view;

import com.kaadas.lock.mvp.mvpbase.IBaseView;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;


/**
 * Create By lxj  on 2019/3/8
 * Describe
 */
public interface IAddFingerSuccessView extends IBaseView {

    void onUploadSuccess();

    void onUploadFailed(Throwable throwable);

    void onUploadFailedServer(BaseResult result);


}
