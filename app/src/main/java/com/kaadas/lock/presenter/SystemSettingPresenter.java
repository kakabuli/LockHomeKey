package com.kaadas.lock.presenter;

import com.kaadas.lock.base.mvpbase.BasePresenter;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.util.BaseObserver;
import com.kaadas.lock.view.ISystemSettingView;


import io.reactivex.disposables.Disposable;

/**
 * Create By lxj  on 2019/3/4
 * Describe
 */
public class SystemSettingPresenter<T> extends BasePresenter<ISystemSettingView> {

    public void loginOut() {
        XiaokaiNewServiceImp.loginOut()
                .subscribe(new BaseObserver<BaseResult>() {
                    @Override
                    public void onSuccess(BaseResult result) {
                        if (mViewRef.get() != null) {
                            mViewRef.get().onLoginOutSuccess();
                        }
                    }

                    @Override
                    public void onAckErrorCode(BaseResult baseResult) {
                        if (mViewRef.get() != null) {
                            mViewRef.get().onLoginOutFailedServer(baseResult);
                        }
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        if (mViewRef.get() != null) {
                            mViewRef.get().onLoginOutFailed(throwable);
                        }
                    }

                    @Override
                    public void onSubscribe1(Disposable d) {
                        compositeDisposable.add(d);
                    }
                });
    }


    public void getProtocolVersion() {

    }

    public void getProtocolContent() {

    }

}
