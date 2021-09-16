package com.kaadas.lock.mvp.presenter.clotheshangermachinepresenter;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.fragment.help.PersonalFAQHangerHelpFragment;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.clotheshangermachineview.IClothesHangerMachineDetailView;
import com.kaadas.lock.mvp.view.clotheshangermachineview.IClothesHangerMachineNicknameView;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.postbean.MultiOTABean;
import com.kaadas.lock.publiclibrary.http.postbean.UpgradeMultiOTABean;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.ClothesHangerMachineUnBindResult;
import com.kaadas.lock.publiclibrary.http.result.MultiCheckOTAResult;
import com.kaadas.lock.publiclibrary.http.util.BaseObserver;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

public class ClothesHangerMachineNicknamePresenter<T> extends BasePresenter<IClothesHangerMachineNicknameView> {

    private Disposable settingNicknameDisposable;

    @Override
    public void attachView(IClothesHangerMachineNicknameView view) {
        super.attachView(view);
    }

    @Override
    public void detachView() {
        super.detachView();
    }


    public void setNickname(String wifiSN, String trim) {
        XiaokaiNewServiceImp.hangerUpdateNickname(wifiSN,trim)
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new BaseObserver<BaseResult>() {
                    @Override
                    public void onSuccess(BaseResult baseResult) {
                        if("200".equals(baseResult.getCode() + "")){
                            MyApplication.getInstance().getClothesHangerMachineBySn(wifiSN)
                                    .setHangerNickName(trim);
                            if(isSafe()){
                                mViewRef.get().onSettingNicknameSuccess();
                            }
                        }else{
                            if(isSafe()){
                                mViewRef.get().onSettingNicknameFailed(baseResult);
                            }
                        }
                    }

                    @Override
                    public void onAckErrorCode(BaseResult baseResult) {
                        if(isSafe()){
                            mViewRef.get().onSettingNicknameFailed(baseResult);
                        }
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        if(isSafe()){
                            mViewRef.get().onSettingNicknameThrowable(throwable);
                        }
                    }

                    @Override
                    public void onSubscribe1(Disposable d) {

                    }
                });
    }
}

