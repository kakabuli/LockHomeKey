package com.kaadas.lock.mvp.presenter.wifilock;

import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockFamilyManagerView;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.WifiLockShareResult;
import com.kaadas.lock.publiclibrary.http.util.BaseObserver;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.SPUtils;

import java.util.List;

import io.reactivex.disposables.Disposable;

public class WifiLockFamilyManagerPresenter<T> extends BasePresenter<IWifiLockFamilyManagerView> {

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
