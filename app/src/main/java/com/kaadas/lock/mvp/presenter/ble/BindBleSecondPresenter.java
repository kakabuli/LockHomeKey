package com.kaadas.lock.mvp.presenter.ble;

import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.mvpbase.BlePresenter;
import com.kaadas.lock.mvp.view.IBindBleSecondView;
import com.kaadas.lock.publiclibrary.ble.responsebean.BleStateBean;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class BindBleSecondPresenter<T> extends BasePresenter<IBindBleSecondView> {

    private Disposable listenConnectStateDisposable;

    public void listenConnectState() {
        toDisposable(listenConnectStateDisposable);
        listenConnectStateDisposable = bleService.subscribeDeviceConnectState()
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<BleStateBean>() {
                    @Override
                    public void accept(BleStateBean bleStateBean) throws Exception {
                        if (isSafe()) {
                            mViewRef.get().onDeviceStateChange(bleStateBean.isConnected());
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        compositeDisposable.add(listenConnectStateDisposable);
    }

}
