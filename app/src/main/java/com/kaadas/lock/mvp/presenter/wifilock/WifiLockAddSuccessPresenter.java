package com.kaadas.lock.mvp.presenter.wifilock;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockAddSuccessView;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.postbean.WiFiLockVideoBindBean;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.util.BaseObserver;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.SPUtils;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public class WifiLockAddSuccessPresenter<T> extends BasePresenter<IWifiLockAddSuccessView> {

    private long times = 0;
    private Disposable bindDisposable;


    private Disposable updateDisposable;
    private Disposable realBindDisposable;

    public void setNickName(String wifiSN, String lockNickname){
        XiaokaiNewServiceImp.wifiLockUpdateNickname(wifiSN, MyApplication.getInstance().getUid(), lockNickname)
        .subscribe(new BaseObserver<BaseResult>() {
            @Override
            public void onSuccess(BaseResult baseResult) {
                if (isSafe()){
                    MyApplication.getInstance().getWifiLockInfoBySn(wifiSN).setLockNickname(lockNickname);
                    mViewRef.get().onSetNameSuccess();
                }
            }

            @Override
            public void onAckErrorCode(BaseResult baseResult) {
                if (isSafe()){
                    mViewRef.get().onSetNameFailedServer(baseResult);
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                if (isSafe()){
                    mViewRef.get().onSetNameFailedNet(throwable);
                }
            }

            @Override
            public void onSubscribe1(Disposable d) {
                compositeDisposable.add(d);
            }
        })
        ;
    }



    @Override
    public void attachView(IWifiLockAddSuccessView view) {
        super.attachView(view);
    }

    @Override
    public void detachView() {
        super.detachView();
    }


}
