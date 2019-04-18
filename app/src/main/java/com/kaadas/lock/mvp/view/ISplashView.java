package com.kaadas.lock.mvp.view;

import com.kaadas.lock.mvp.mvpbase.IBaseView;
import com.kaadas.lock.bean.VersionBean;


public interface ISplashView extends IBaseView {

    void getVersionSuccess(VersionBean versionBean);

    void getVersionFail();

}
