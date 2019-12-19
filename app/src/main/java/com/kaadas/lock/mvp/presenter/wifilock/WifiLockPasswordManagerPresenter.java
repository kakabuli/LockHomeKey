package com.kaadas.lock.mvp.presenter.wifilock;

import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.bean.WiFiLockCardAndFingerShowBean;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockPasswordManagerView;
import com.kaadas.lock.publiclibrary.bean.ForeverPassword;
import com.kaadas.lock.publiclibrary.bean.WiFiLockPassword;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.WifiLockGetPasswordListResult;
import com.kaadas.lock.publiclibrary.http.util.BaseObserver;
import com.kaadas.lock.utils.Constants;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.SPUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

public class WifiLockPasswordManagerPresenter<T> extends BasePresenter<IWifiLockPasswordManagerView> {

    public void getPasswordList(String wifiSn){
        XiaokaiNewServiceImp.wifiLockGetPwdList(wifiSn, MyApplication.getInstance().getUid())
                .subscribe(new BaseObserver<WifiLockGetPasswordListResult>() {
                    @Override
                    public void onSuccess(WifiLockGetPasswordListResult wifiLockGetPasswordListResult) {
                        WiFiLockPassword wiFiLockPassword = wifiLockGetPasswordListResult.getDataX();
                        SPUtils.put(KeyConstants.WIFI_LOCK_PASSWORD_LIST + wifiSn, new Gson().toJson(wiFiLockPassword));
                        if (isSafe()){
                            mViewRef.get().onGetPasswordSuccess(wiFiLockPassword);
                        }
                    }

                    @Override
                    public void onAckErrorCode(BaseResult baseResult) {
                        if (isSafe()){
                            mViewRef.get().onGetPasswordFailedServer(baseResult);
                        }
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        if (isSafe()){
                            mViewRef.get().onGetPasswordFailed(throwable);
                        }
                    }

                    @Override
                    public void onSubscribe1(Disposable d) {
                        compositeDisposable.add(d);
                    }
                });
    }



    public List<ForeverPassword> getShowPasswords(WiFiLockPassword wiFiLockPassword) {
        List<ForeverPassword> passwords = new ArrayList<>();
        if (wiFiLockPassword != null) {
            List<WiFiLockPassword.PwdListBean> pwdList = wiFiLockPassword.getPwdList();
            List<WiFiLockPassword.PwdNicknameBean> pwdNickname = wiFiLockPassword.getPwdNickname();
            if (pwdList != null) {
                //String num, String nickName, long createTime, int type, long startTime, long endTime, List<String> items
                for (WiFiLockPassword.PwdListBean password : pwdList) {
                    int num = password.getNum();
                    String sNum = num > 9 ? "" + num : "0" + num;
                    ForeverPassword foreverPassword = new ForeverPassword(sNum, sNum,
                            password.getCreateTime(), password.getType(), password.getStartTime(), password.getEndTime(), password.getItems());
                    if (pwdNickname != null) {
                        for (WiFiLockPassword.PwdNicknameBean nickname : pwdNickname) {
                            if (nickname.getNum() == num) {
                                foreverPassword.setNickName(nickname.getNickName());
                            }
                        }
                    }
                    passwords.add(foreverPassword);
                }
            }
        }
        return passwords;
    }


    public List<WiFiLockCardAndFingerShowBean> getShowCardsFingers(WiFiLockPassword wiFiLockPassword, int type) {
        List<WiFiLockCardAndFingerShowBean> passwords = new ArrayList<>();
        if (wiFiLockPassword != null) {
            switch (type) {
                case 2: //指纹
                    List<WiFiLockPassword.FingerprintListBean> fingerprintList = wiFiLockPassword.getFingerprintList();
                    List<WiFiLockPassword.FingerprintNicknameBean> fingerprintNickname = wiFiLockPassword.getFingerprintNickname();
                    if (fingerprintList != null) {
                        for (WiFiLockPassword.FingerprintListBean password : fingerprintList) {
                            int num = password.getNum();
                            String sNick = num > 9 ? "" + num : "0" + num;
                            WiFiLockCardAndFingerShowBean fingerShowBean = new WiFiLockCardAndFingerShowBean(num, password.getCreateTime(), sNick);
                            if (fingerprintNickname != null) {
                                for (WiFiLockPassword.FingerprintNicknameBean nickname : fingerprintNickname) {
                                    if (nickname.getNum() == num) {
                                        fingerShowBean.setNickName(nickname.getNickName());
                                    }
                                }
                            }
                            passwords.add(fingerShowBean);
                        }
                    }
                    break;
                case 3:  //卡片
                    List<WiFiLockPassword.CardListBean> cardList = wiFiLockPassword.getCardList();
                    List<WiFiLockPassword.CardNicknameBean> cardNickname = wiFiLockPassword.getCardNickname();
                    if (cardList != null) {
                        for (WiFiLockPassword.CardListBean password : cardList) {
                            int num = password.getNum();
                            String sNick = num > 9 ? "" + num : "0" + num;
                            WiFiLockCardAndFingerShowBean fingerShowBean = new WiFiLockCardAndFingerShowBean(num, password.getCreateTime(), sNick);
                            if (cardNickname != null) {
                                for (WiFiLockPassword.CardNicknameBean nickname : cardNickname) {
                                    if (nickname.getNum() == num) {
                                        fingerShowBean.setNickName(nickname.getNickName());
                                    }
                                }
                            }
                            passwords.add(fingerShowBean);
                        }
                    }
                    break;
            }
        }
        return passwords;
    }




}
