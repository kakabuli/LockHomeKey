package com.kaadas.lock.mvp.view.wifilock;

import com.kaadas.lock.mvp.mvpbase.IBaseView;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;

public interface ISettingDuressAlarm extends IBaseView {

    void onSettingDuressAccount(BaseResult baseResult);

    void onSettingDuress(BaseResult baseResult);
}
