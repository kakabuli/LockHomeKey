package com.kaadas.lock.mvp.presenter;

import android.text.TextUtils;
import android.util.Log;

import com.huawei.android.hms.agent.HMSAgent;
import com.huawei.android.hms.agent.push.handler.GetTokenHandler;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.activity.MainActivity;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.util.BaseObserver;
import com.kaadas.lock.mvp.view.ISystemSettingView;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.Rom;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.ftp.GeTui;
import com.kaidishi.lock.xiaomi.SPUtils2;
import com.kaidishi.lock.xiaomi.XiaoMiConstant;


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
                        if (isSafe()) {
                            mViewRef.get().onLoginOutSuccess();
                        }
                    }

                    @Override
                    public void onAckErrorCode(BaseResult baseResult) {
                        if (isSafe()) {
                            mViewRef.get().onLoginOutFailedServer(baseResult);
                        }
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        if (isSafe()) {
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
