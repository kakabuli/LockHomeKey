package com.kaadas.lock.mvp.presenter.wifilock;

import com.blankj.utilcode.util.LogUtils;
import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.wifilock.ISettingDuressAlarm;
import com.kaadas.lock.publiclibrary.bean.WiFiLockPassword;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.postbean.SettingPwdDuressAccountBean;
import com.kaadas.lock.publiclibrary.http.postbean.SettingPwdDuressAlarmBean;
import com.kaadas.lock.publiclibrary.http.postbean.SettingPwdDuressAlarmSwitchBean;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.WifiLockGetPasswordListResult;
import com.kaadas.lock.publiclibrary.http.util.BaseObserver;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.SPUtils;

import io.reactivex.disposables.Disposable;

public class SettingDuressAlarmPresenter<T> extends BasePresenter<ISettingDuressAlarm> {
    private Disposable duressPwdAccountDisposable;
    private Disposable duressPwdAlarmDisposable;
    private Disposable duressSwitchDisposable;

    public void setDuressPwdAccount(String wifiSN,int pwdType,int num,int accountType,String duressAccount){
        SettingPwdDuressAccountBean mSettingPwdDuressAccountBean = new SettingPwdDuressAccountBean(MyApplication.getInstance().getUid(),wifiSN,
                pwdType,num,accountType,duressAccount);
        toDisposable(duressPwdAccountDisposable);
        XiaokaiNewServiceImp.wifiPwdDuressAccount(mSettingPwdDuressAccountBean).subscribe(new BaseObserver<BaseResult>() {
            @Override
            public void onSuccess(BaseResult baseResult) {
                if(isSafe()){
                    mViewRef.get().onSettingDuressAccount(baseResult);
                }
            }

            @Override
            public void onAckErrorCode(BaseResult baseResult) {
                if(isSafe()){
                    mViewRef.get().onSettingDuressAccount(baseResult);
                }
            }

            @Override
            public void onFailed(Throwable throwable) {

            }

            @Override
            public void onSubscribe1(Disposable d) {
                duressPwdAccountDisposable = d;
            }
        });
    }


    public void setDuressPwdAlarm(String wifiSN,int pwdType,int num,int pwdDuressSwitch){
        SettingPwdDuressAlarmBean mSettingPwdDuressAlarmBean =  new SettingPwdDuressAlarmBean(MyApplication.getInstance().getUid(),wifiSN,
                pwdType,num,pwdDuressSwitch);
        toDisposable(duressPwdAlarmDisposable);
        XiaokaiNewServiceImp.wifiPwdDuressAlarm(mSettingPwdDuressAlarmBean).subscribe(new BaseObserver<BaseResult>() {
            @Override
            public void onSuccess(BaseResult baseResult) {
                if(isSafe()){
                    MyApplication.getInstance().getAllDevicesByMqtt(true);
                    mViewRef.get().onSettingDuress(baseResult);
                }
            }

            @Override
            public void onAckErrorCode(BaseResult baseResult) {
                if(isSafe()){
                    mViewRef.get().onSettingDuress(baseResult);
                }
            }

            @Override
            public void onFailed(Throwable throwable) {

            }

            @Override
            public void onSubscribe1(Disposable d) {
                duressPwdAlarmDisposable = d;
            }
        });
    }


    public void getPasswordList(String wifiSn) {
        XiaokaiNewServiceImp.wifiLockGetPwdList(wifiSn, MyApplication.getInstance().getUid())
                .subscribe(new BaseObserver<WifiLockGetPasswordListResult>() {
                    @Override
                    public void onSuccess(WifiLockGetPasswordListResult wifiLockGetPasswordListResult) {

                        WiFiLockPassword wiFiLockPassword = wifiLockGetPasswordListResult.getData();
                        String object = new Gson().toJson(wiFiLockPassword);
                        LogUtils.d("服务器数据是   " + object);

                        SPUtils.put(KeyConstants.WIFI_LOCK_PASSWORD_LIST + wifiSn, object);
                    }

                    @Override
                    public void onAckErrorCode(BaseResult baseResult) {

                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                    }

                    @Override
                    public void onSubscribe1(Disposable d) {
                        compositeDisposable.add(d);
                    }
                });
    }
}
