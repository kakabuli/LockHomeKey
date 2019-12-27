package com.kaadas.lock.mvp.presenter.wifilock;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockDetailView;
import com.kaadas.lock.publiclibrary.bean.WiFiLockPassword;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.WifiLockGetPasswordListResult;
import com.kaadas.lock.publiclibrary.http.result.WifiLockShareResult;
import com.kaadas.lock.publiclibrary.http.util.BaseObserver;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.SPUtils;

import java.util.List;

import io.reactivex.disposables.Disposable;

public class WifiLockDetailPresenter<T> extends BasePresenter<IWifiLockDetailView> {

    public void getPasswordList(String wifiSn) {
        XiaokaiNewServiceImp.wifiLockGetPwdList(wifiSn, MyApplication.getInstance().getUid())
                .subscribe(new BaseObserver<WifiLockGetPasswordListResult>() {
                    @Override
                    public void onSuccess(WifiLockGetPasswordListResult wifiLockGetPasswordListResult) {
                        WiFiLockPassword wiFiLockPassword = wifiLockGetPasswordListResult.getDataX();
                        SPUtils.put(KeyConstants.WIFI_LOCK_PASSWORD_LIST + wifiSn, new Gson().toJson(wiFiLockPassword));
                        if (isSafe()) {
                            mViewRef.get().onGetPasswordSuccess(wiFiLockPassword);
                        }
                    }
                    @Override
                    public void onAckErrorCode(BaseResult baseResult) {
                        if (isSafe()) {
                            mViewRef.get().onGetPasswordFailedServer(baseResult);
                        }
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        if (isSafe()) {
                            mViewRef.get().onGetPasswordFailed(throwable);
                        }
                    }

                    @Override
                    public void onSubscribe1(Disposable d) {
                        compositeDisposable.add(d);
                    }
                });
    }

    public void queryUserList(String wifiSN) {
        XiaokaiNewServiceImp.wifiLockGetShareUserList(wifiSN, MyApplication.getInstance().getUid())
                .subscribe(new BaseObserver<WifiLockShareResult>() {
                    @Override
                    public void onSuccess(WifiLockShareResult wifiLockShareResult) {
                        List<WifiLockShareResult.WifiLockShareUser> shareUsers = wifiLockShareResult.getData();
                        Gson gson = new Gson();
                        SPUtils.put(KeyConstants.WIFI_LOCK_SHARE_USER_LIST + wifiSN, gson.toJson(shareUsers));
                        if (isSafe()) {
                            mViewRef.get().querySuccess(shareUsers);
                        }
                    }

                    @Override
                    public void onAckErrorCode(BaseResult baseResult) {
                        if (isSafe()) {
                            mViewRef.get().queryFailedServer(baseResult);
                        }
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        if (isSafe()) {
                            mViewRef.get().queryFailed(throwable);
                        }
                    }
                    @Override
                    public void onSubscribe1(Disposable d) {
                        compositeDisposable.add(d);
                    }
                });
    }

}
