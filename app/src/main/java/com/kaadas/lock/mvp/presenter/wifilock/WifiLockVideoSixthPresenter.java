package com.kaadas.lock.mvp.presenter.wifilock;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockVideoSixthView;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.postbean.WiFiLockVideoBindBean;
import com.kaadas.lock.publiclibrary.http.postbean.WiFiLockVideoUpdateBindBean;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.WifiLockVideoBindResult;
import com.kaadas.lock.publiclibrary.http.util.BaseObserver;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.SPUtils;

import java.security.Key;

import io.reactivex.disposables.Disposable;

public class WifiLockVideoSixthPresenter<T> extends BasePresenter<IWifiLockVideoSixthView> {

    public void bindDevice(String wifiSN, String lockNickName, String uid, String randomCode, String wifiName, int func,
                            int distributionNetwork,String device_sn,String mac,String device_did,String p2p_password) {

        WiFiLockVideoBindBean bean = new WiFiLockVideoBindBean(wifiSN,lockNickName,uid,randomCode,wifiName,func,distributionNetwork,
        device_sn,mac,device_did,p2p_password);
        LogUtils.e("WifiLockVideoSixthPresenter WiFiLockVideoBindBean-->" + bean.toString());
        XiaokaiNewServiceImp.wifiVideoLockBind(bean).subscribe(new BaseObserver<BaseResult>() {
                    @Override
                    public void onSuccess(BaseResult baseResult) {
                        MyApplication.getInstance().getAllDevicesByMqtt(true);
//                        SPUtils.put(KeyConstants.WIFI_VIDEO_LOCK_OPERATION_RECORD + wifiSN, "");
//                        SPUtils.put(KeyConstants.WIFI_VIDEO_LOCK_VISITOR_RECORD + wifiSN, "");
                        SPUtils.put(KeyConstants.WIFI_VIDEO_LOCK_ALARM_RECORD + wifiSN, "");
                        SPUtils.put(KeyConstants.WIFI_LOCK_OPERATION_RECORD + wifiSN, "");
                        SPUtils.put(KeyConstants.WIFI_VIDEO_LOCK_VISITOR_RECORD + wifiSN, "");
                        SPUtils.put(KeyConstants.WIFI_LOCK_ALARM_RECORD + wifiSN, "");
                        SPUtils.put(KeyConstants.WIFI_VIDEO_LOCK_RANDOMCODE + wifiSN,true);
                        if (isSafe()) {
                            mViewRef.get().onBindSuccess(wifiSN);
                            LogUtils.e("shulan WifiLockVideoSixthPresenter baseResult-->" + baseResult.getMsg());
                        }
                    }

                    @Override
                    public void onAckErrorCode(BaseResult baseResult) {
                        if (isSafe()) {
                            mViewRef.get().onBindFailed(baseResult);
                        }
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        if (isSafe()) {
                            mViewRef.get().onBindThrowable(throwable);
                        }
                    }

                    @Override
                    public void onSubscribe1(Disposable d) {
                        compositeDisposable.add(d);
                    }
                });
    }

    public void unBindDeviceFail(String wifiSN) {
        XiaokaiNewServiceImp.wifiVideoLockBindFail(wifiSN,0).subscribe(new BaseObserver<WifiLockVideoBindResult>() {
            @Override
            public void onSuccess(WifiLockVideoBindResult wifiLockVideoBindResult) {
                if (isSafe()) {
                    mViewRef.get().onBindFailed(wifiLockVideoBindResult);
                }
            }

            @Override
            public void onAckErrorCode(BaseResult baseResult) {
                if (isSafe()) {
                    mViewRef.get().onBindFailed(baseResult);
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                if (isSafe()) {
                    mViewRef.get().onBindThrowable(throwable);
                }
            }

            @Override
            public void onSubscribe1(Disposable d) {
                compositeDisposable.add(d);
            }


        });
    }

    public void updateBindDevice(String wifiSN, String uid, String randomCode, String wifiName,
                                 int functionSet,String device_did, String p2p_password){
        WiFiLockVideoUpdateBindBean bean = new WiFiLockVideoUpdateBindBean(wifiSN,uid,randomCode,wifiName,functionSet,device_did,p2p_password);
        XiaokaiNewServiceImp.wifiVideoLockUpdateBind(bean).subscribe(new BaseObserver<WifiLockVideoBindResult>() {
            @Override
            public void onSuccess(WifiLockVideoBindResult wifiLockVideoBindResult) {
                if(isSafe()){
                    mViewRef.get().onUpdateSuccess(wifiSN);
                }
            }

            @Override
            public void onAckErrorCode(BaseResult baseResult) {
                if(isSafe()){
                    mViewRef.get().onUpdateFailed(baseResult);
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                if(isSafe()){
                    mViewRef.get().onUpdateThrowable(throwable);
                }
            }

            @Override
            public void onSubscribe1(Disposable d) {

            }
        });
    }


    public void setNickName(String wifiSN, String lockNickname){
        XiaokaiNewServiceImp.wifiLockUpdateNickname(wifiSN, MyApplication.getInstance().getUid(), lockNickname)
                .subscribe(new BaseObserver<BaseResult>() {
                    @Override
                    public void onSuccess(BaseResult baseResult) {
                        if (isSafe()){
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
                });

    }

}
