package com.kaadas.lock.mvp.view.deviceaddview;

import com.kaadas.lock.mvp.mvpbase.IBaseView;

public interface IAddCatEyeView extends IBaseView {

    void allowCatEyeJoinSuccess();

    void allowCatEyeJoinFailed(Throwable throwable);

    void joinTimeout();



}
