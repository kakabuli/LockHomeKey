package com.kaadas.lock.presenter;




import com.kaadas.lock.MyApplication;
import com.kaadas.lock.base.mvpbase.BasePresenter;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.RegisterResult;
import com.kaadas.lock.publiclibrary.http.util.BaseObserver;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.view.IRegisterView;

import io.reactivex.disposables.Disposable;

public class RegisterPresenter<T> extends BasePresenter<IRegisterView> {

    public void sendRandomByPhone(String phone, String code) {
        XiaokaiNewServiceImp.sendMessage((phone), code)
                .subscribe(new BaseObserver<BaseResult>() {
                    @Override
                    public void onSuccess(BaseResult result) {
                        LogUtils.e("发送验证码成功  " + result.getMsg());
                        if (mViewRef != null) {
                            mViewRef.get().sendRandomSuccess();
                        }
                    }

                    @Override
                    public void onAckErrorCode(BaseResult baseResult) {
                        if (mViewRef != null) {
                            mViewRef.get().sendRandomFailedServer(baseResult);
                        }
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        if (mViewRef != null) {
                            mViewRef.get().sendRandomFailed(throwable);
                        }
                    }

                    @Override
                    public void onSubscribe1(Disposable d) {
                        compositeDisposable.add(d);
                    }
                });
    }


    public void registerByPhone(String phone, String pwd, String random) {
        XiaokaiNewServiceImp.registerByPhone(phone, pwd, random)
                .subscribe(
                        new BaseObserver<RegisterResult>() {
                            @Override
                            public void onSuccess(RegisterResult result) {
                                LogUtils.e("注册成功  " + result.getData().toString());
                                SPUtils.put(SPUtils.TOKEN, result.getData().getToken());
                                SPUtils.put(SPUtils.UID, result.getData().getUid());
                                MyApplication.getInstance().setToken(result.getData().getToken());
                                MyApplication.getInstance().setUid(result.getData().getUid());
                                if (mViewRef != null) {
                                    mViewRef.get().registerSuccess();
                                }
                            }

                            @Override
                            public void onAckErrorCode(BaseResult baseResult) {
                                if (mViewRef != null) {
                                    mViewRef.get().registerFailedServer(baseResult);
                                }
                            }

                            @Override
                            public void onFailed(Throwable throwable) {
                                if (mViewRef != null) {
                                    mViewRef.get().registerFailed(throwable);
                                }
                            }

                            @Override
                            public void onSubscribe1(Disposable d) {
                                compositeDisposable.add(d);
                            }
                        }
                );

    }

}
