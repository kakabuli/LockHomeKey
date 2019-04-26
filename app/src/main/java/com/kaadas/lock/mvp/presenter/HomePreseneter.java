package com.kaadas.lock.mvp.presenter;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.IHomeView;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.AllBindDevices;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class HomePreseneter<T> extends BasePresenter<IHomeView> {


    private Disposable listenerAllDevicesDisposable;

    @Override
    public void attachView(IHomeView view) {
        super.attachView(view);
        toDisposable(listenerAllDevicesDisposable);
        listenerAllDevicesDisposable = MyApplication.getInstance().listenerAllDevices()
                .subscribe(new Consumer<AllBindDevices>() {
                    @Override
                    public void accept(AllBindDevices allBindDevices) throws Exception {
                        if (mViewRef.get()!=null){
                            mViewRef.get().onDeviceRefresh(allBindDevices);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        compositeDisposable.add(listenerAllDevicesDisposable);
    }
}
