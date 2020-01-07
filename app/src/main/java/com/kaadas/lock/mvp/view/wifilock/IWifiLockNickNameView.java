package com.kaadas.lock.mvp.view.wifilock;

import com.kaadas.lock.mvp.mvpbase.IBaseView;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;

public interface IWifiLockNickNameView extends IBaseView {

    void onUpdateNickSuccess();

    void onUpdateNickFailed(Throwable throwable);

    void onUpdateNickFailedServer(BaseResult result);


}
