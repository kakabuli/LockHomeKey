package com.kaadas.lock.mvp.presenter;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.bean.VersionBean;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.mvp.view.ISplashView;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;


import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class SplashPresenter<T> extends BasePresenter<ISplashView> {

    private Disposable listenerServiceDisposable;


    //请求App的版本信息
    @Deprecated
    public void getAppVersion() {
        XiaokaiNewServiceImp.getAppVersion().subscribe(new Observer<VersionBean>() {
            @Override
            public void onSubscribe(Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onNext(VersionBean versionBean) {
                if (isSafe()) {
                    mViewRef.get().getVersionSuccess(versionBean);
                }
            }

            @Override
            public void onError(Throwable e) {
                if (isSafe()) {
                    mViewRef.get().getVersionFail();
                }
            }

            @Override
            public void onComplete() {

            }
        });
    }

    //监听蓝牙服务启动成功和Mqtt服务启动成功
    public void listenerServiceConnect() {
        toDisposable(listenerServiceDisposable);
        listenerServiceDisposable = MyApplication.getInstance()
                .listenerServiceConnect()
                .timeout(6 * 1000, TimeUnit.MILLISECONDS)
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        if (integer == 2) {
                            toDisposable(listenerServiceDisposable);
                            if (isSafe()) {
                                mViewRef.get().serviceConnectSuccess();
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (isSafe()) {
                            mViewRef.get().serviceConnectThrowable();
                        }
                    }
                });
        compositeDisposable.add(listenerServiceDisposable);
    }
}