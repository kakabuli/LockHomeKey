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

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

public class WifiLockDetailPresenter<T> extends BasePresenter<IWifiLockDetailView> {

    public void getPasswordList(String wifiSn) {
        XiaokaiNewServiceImp.wifiLockGetPwdList(wifiSn, MyApplication.getInstance().getUid())
                .subscribe(new BaseObserver<WifiLockGetPasswordListResult>() {
                    @Override
                    public void onSuccess(WifiLockGetPasswordListResult wifiLockGetPasswordListResult) {
                        WiFiLockPassword wiFiLockPassword = wifiLockGetPasswordListResult.getData();
//                        List<WiFiLockPassword.PwdListBean> pwdList = new ArrayList<>();
//                        long time =  System.currentTimeMillis() / 1000;
//                        pwdList.add(new WiFiLockPassword.PwdListBean(time, 0, 0, 0, 1, null));
//                        pwdList.add(new WiFiLockPassword.PwdListBean(time, 0, 2, 0, 0, null));
//                        List<WiFiLockPassword.FingerprintListBean> fingerprintList = new ArrayList<>();
//                        fingerprintList.add(new WiFiLockPassword.FingerprintListBean(time, 0));
//                        fingerprintList.add(new WiFiLockPassword.FingerprintListBean(time, 1));
//                        List<WiFiLockPassword.CardListBean> cardList = new ArrayList<>();
//                        cardList.add(new WiFiLockPassword.CardListBean(time, 0));
//                        cardList.add(new WiFiLockPassword.CardListBean(time, 1));
//                        cardList.add(new WiFiLockPassword.CardListBean(time, 2));
//                        List<WiFiLockPassword.PwdNicknameBean> pwdNickname = new ArrayList<>();
//                        pwdNickname.add(new WiFiLockPassword.PwdNicknameBean(0, "密码1"));
//                        pwdNickname.add(new WiFiLockPassword.PwdNicknameBean(1, "密码2"));
//                        List<WiFiLockPassword.FingerprintNicknameBean> fingerprintNickname = new ArrayList<>();
//                        fingerprintNickname.add(new WiFiLockPassword.FingerprintNicknameBean(0, "指纹1"));
//                        fingerprintNickname.add(new WiFiLockPassword.FingerprintNicknameBean(1, "指纹2"));
//                        List<WiFiLockPassword.CardNicknameBean> cardNickname = new ArrayList<>();
//                        cardNickname.add(new WiFiLockPassword.CardNicknameBean(0, "卡片1"));
//                        cardNickname.add(new WiFiLockPassword.CardNicknameBean(1, "卡片2"));
//                        wiFiLockPassword = new WiFiLockPassword( pwdList,  fingerprintList,  cardList,   pwdNickname,  fingerprintNickname,  cardNickname);

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
