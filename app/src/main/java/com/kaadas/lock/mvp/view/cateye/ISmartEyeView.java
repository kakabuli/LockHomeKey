package com.kaadas.lock.mvp.view.cateye;

import com.kaadas.lock.mvp.mvpbase.IBaseView;

public interface ISmartEyeView extends IBaseView {
    //pir徘徊设置成功
    void setPirWanderSuccess();

    //pir徘徊设置失败
    void setPirWanderFail();

    //pir徘徊设置异常
    void setPirWander(Throwable throwable);

    //设置pir成功
    void setPirEnableSuccess(int status);

    //设置pir失败
    void setPirEnableFail();

    //设备pir异常
    void setPirEnableThrowable(Throwable throwable);

}
