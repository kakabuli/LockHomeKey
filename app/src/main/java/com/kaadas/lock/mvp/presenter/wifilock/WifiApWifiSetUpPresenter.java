package com.kaadas.lock.mvp.presenter.wifilock;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockAPWifiSetUpView;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.util.BaseObserver;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.utils.LogUtils;


import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class WifiApWifiSetUpPresenter<T> extends BasePresenter<IWifiLockAPWifiSetUpView> {

    private long times = 0;
    private Disposable bindDisposable;

    @Override
    public void detachView() {
        super.detachView();
    }

    public void onReadSuccess(String wifiSN,   String randomCode, String wifiName, int func){
        bindDisposable = Observable.interval(3, 3, TimeUnit.SECONDS)//设置0延迟，每隔一秒发送一条数据
                .take(10)//设置截取前20次
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        LogUtils.e("第  " + aLong +"次访问");
                        times = aLong;
                        WifiLockInfo wifiLockInfo = MyApplication.getInstance().getWifiLockInfoBySn(wifiSN);
                        if (wifiLockInfo != null && wifiLockInfo.getIsAdmin() == 1) {
                              update(wifiSN, randomCode, wifiName, func);
                        } else {
                             bindDevice(wifiSN, wifiSN, MyApplication.getInstance().getUid(), randomCode, wifiName, func);
                        }
                    }
                });
        compositeDisposable.add(bindDisposable);
    }

    public void bindDevice(String wifiSN, String lockNickName, String uid, String randomCode, String wifiName, int func) {
        XiaokaiNewServiceImp.wifiLockBind(wifiSN, lockNickName, uid, randomCode, wifiName, func)
                .subscribe(new BaseObserver<BaseResult>() {
                    @Override
                    public void onSuccess(BaseResult baseResult) {
                        MyApplication.getInstance().getAllDevicesByMqtt(true);
                        toDisposable(bindDisposable);
                        if (isSafe()) {
                            mViewRef.get().onBindSuccess(wifiSN);
                        }
                    }

                    @Override
                    public void onAckErrorCode(BaseResult baseResult) {
                        if (isSafe() && times>=9) {
                            mViewRef.get().onBindFailed(baseResult);
                            toDisposable(bindDisposable);
                        }
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        if (isSafe() && times>=9) {
                            mViewRef.get().onBindThrowable(throwable);
                            toDisposable(bindDisposable);
                        }
                    }

                    @Override
                    public void onSubscribe1(Disposable d) {
                        compositeDisposable.add(d);
                    }
                });
    }


    public void update(String wifiSN, String randomCode, String wifiName, int func) {
        XiaokaiNewServiceImp.wifiLockUpdateInfo(MyApplication.getInstance().getUid(), wifiSN, randomCode, wifiName, func)
                .subscribe(new BaseObserver<BaseResult>() {
                    @Override
                    public void onSuccess(BaseResult baseResult) {
                        MyApplication.getInstance().getAllDevicesByMqtt(true);
                        toDisposable(bindDisposable);
                        if (isSafe()) {
                            mViewRef.get().onUpdateSuccess(wifiSN);
                        }
                    }

                    @Override
                    public void onAckErrorCode(BaseResult baseResult) {
                        if (isSafe() && times>=9) {
                            mViewRef.get().onUpdateFailed(baseResult);
                            toDisposable(bindDisposable);
                        }
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        if (isSafe() && times>=9) {
                            mViewRef.get().onUpdateThrowable(throwable);
                            toDisposable(bindDisposable);
                        }
                    }

                    @Override
                    public void onSubscribe1(Disposable d) {
                        compositeDisposable.add(d);
                    }
                });
    }


}
