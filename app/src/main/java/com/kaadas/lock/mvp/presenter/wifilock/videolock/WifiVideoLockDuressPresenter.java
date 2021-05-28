package com.kaadas.lock.mvp.presenter.wifilock.videolock;

import com.blankj.utilcode.util.LogUtils;
import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.bean.DuressBean;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.wifilock.videolock.IWifiVideoLockDuressView;
import com.kaadas.lock.publiclibrary.bean.WiFiLockPassword;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.WifiLockGetPasswordListResult;
import com.kaadas.lock.publiclibrary.http.util.BaseObserver;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.SPUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

public class WifiVideoLockDuressPresenter<T> extends BasePresenter<IWifiVideoLockDuressView> {


    @Override
    public void attachView(IWifiVideoLockDuressView view) {
        super.attachView(view);
    }

    @Override
    public void detachView() {
        super.detachView();
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

    public List<DuressBean> setWifiLockPassword(String wifisn, WiFiLockPassword wiFiLockPassword) {
        if(wiFiLockPassword == null) return null;
        List<DuressBean> list = new ArrayList<>();
        if(wiFiLockPassword.getPwdList() != null && wiFiLockPassword.getPwdList().size() > 0){
            list.add(new DuressBean(wifisn,1,true));
            for(WiFiLockPassword.PwdListBean bean : wiFiLockPassword.getPwdList()){
                String sNum = bean.getNum() > 9 ? "" + bean.getNum() : "0" + bean.getNum();
                String nickName = "";
                for(WiFiLockPassword.PwdNicknameBean li :wiFiLockPassword.getPwdNickname()){
                    if(li.getNum() == bean.getNum()){
                        if(li.getNickName().isEmpty()){
                            nickName = sNum;
                        }else{
                            nickName = li.getNickName();
                        }
                    }
                }
                list.add(new DuressBean(wifisn,1,0,"138***8083",sNum,bean.getCreateTime(),nickName));
            }
        }

        if(wiFiLockPassword.getFingerprintList() != null && wiFiLockPassword.getFingerprintList().size() > 0){
            list.add(new DuressBean(wifisn,2,true));
            for(WiFiLockPassword.FingerprintListBean bean : wiFiLockPassword.getFingerprintList()){
                String sNum = bean.getNum() > 9 ? "" + bean.getNum() : "0" + bean.getNum();
                String nickName = "";
                for(WiFiLockPassword.FingerprintNicknameBean li : wiFiLockPassword.getFingerprintNickname()){
                    if(li.getNum() == bean.getNum()){
                        if(li.getNickName().isEmpty()){
                            nickName = sNum;
                        }else{
                            nickName = li.getNickName();
                        }
                    }
                }
                list.add(new DuressBean(wifisn,2,0,"138***8083",sNum,bean.getCreateTime(),nickName));
            }
        }
        return list;
    }
}
