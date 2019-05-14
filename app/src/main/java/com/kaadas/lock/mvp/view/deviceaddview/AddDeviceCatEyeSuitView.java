package com.kaadas.lock.mvp.view.deviceaddview;

import com.kaadas.lock.mvp.mvpbase.IBaseView;

public interface AddDeviceCatEyeSuitView extends IBaseView {

    //绑定咪咪成功的数据
    void bindMimiSuccess();


    //绑定咪咪失败
    void bindMimiFail(String code,String msg);

    //绑定咪咪异常
    void bindMimiThrowable(Throwable throwable);


}
