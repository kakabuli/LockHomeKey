package com.kaadas.lock.presenter;

import com.kaadas.lock.base.mvpbase.BasePresenter;
import com.kaadas.lock.bean.VersionBean;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.view.ISplashView;


import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class SplashPresenter<T> extends BasePresenter<ISplashView> {

    //请求App的版本信息
    public void getAppVersion() {
        XiaokaiNewServiceImp.getAppVersion().subscribe(new Observer<VersionBean>() {
            @Override
            public void onSubscribe(Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onNext(VersionBean versionBean) {
                if (mViewRef.get() != null) {
                    mViewRef.get().getVersionSuccess(versionBean);
                }
            }

            @Override
            public void onError(Throwable e) {
                if (mViewRef.get() != null) {
                    mViewRef.get().getVersionFail();
                }
            }

            @Override
            public void onComplete() {

            }
        });
    }

}
