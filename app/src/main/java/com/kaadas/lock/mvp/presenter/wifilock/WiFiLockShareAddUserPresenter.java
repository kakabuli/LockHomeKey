package com.kaadas.lock.mvp.presenter.wifilock;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.wifilock.IWiFiLockShareAddUserView;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.util.BaseObserver;

import io.reactivex.disposables.Disposable;

public class WiFiLockShareAddUserPresenter<T> extends BasePresenter<IWiFiLockShareAddUserView> {


    public void addUser(String wifiSN,   String uname, String userNickname){
        XiaokaiNewServiceImp.wifiLockAddShareUser(wifiSN, MyApplication.getInstance().getUid(), uname, userNickname)

            .subscribe(new BaseObserver<BaseResult>() {
                @Override
                public void onSuccess(BaseResult baseResult) {
                    if (isSafe()){
                        mViewRef.get().onAddUserSuccess();
                    }
                }

                @Override
                public void onAckErrorCode(BaseResult baseResult) {
                    if (isSafe()){
                        mViewRef.get().onAddUserFailed(baseResult);
                    }
                }

                @Override
                public void onFailed(Throwable throwable) {
                    if (isSafe()){
                        mViewRef.get().onAddUserFailedServer(throwable);
                    }
                }

                @Override
                public void onSubscribe1(Disposable d) {
                    compositeDisposable.add(d);
                }
            })
        ;



    }

}
