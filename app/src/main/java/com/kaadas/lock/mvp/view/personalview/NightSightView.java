package com.kaadas.lock.mvp.view.personalview;

import com.kaadas.lock.mvp.mvpbase.IBaseView;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.CatEyeInfoBeanPropertyResultUpdate;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.CatEyeInfoBeanResult;

public interface NightSightView extends IBaseView {


    //获取猫眼信息成功
    void updateNightSightSuccess(CatEyeInfoBeanPropertyResultUpdate catEyeInfoBeanPropertyResultUpdate);

    //获取猫眼信息失败
    void updateNightSightFail();
    void updateNightSighEveThrowable(Throwable throwable);
}
