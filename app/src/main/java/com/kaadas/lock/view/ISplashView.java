package com.kaadas.lock.view;

import com.kaadas.lock.base.mvpbase.IBaseView;
import com.kaadas.lock.bean.VersionBean;


public interface ISplashView extends IBaseView {

    void getVersionSuccess(VersionBean versionBean);

    void getVersionFail();

}
