package com.kaadas.lock.mvp.presenter.cateye;

import android.util.Log;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.cateye.IVedeoCallBack;
import com.kaadas.lock.mvp.view.cateye.IVideoView;
import com.kaadas.lock.publiclibrary.bean.CateEyeInfo;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.linphone.MemeManager;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.ftp.GeTui;

import net.sdvn.cmapi.Device;



import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;

/**
 * Create By denganzhi  on 2019/5/11
 * Describe
 */

public class VideoCallBackPresenter <T> extends BasePresenter<IVedeoCallBack> {

    private Disposable memeDisposable;
    /**
     * 登录米米网
     *
     * @param meUsername
     * @param mePwd
     */
    public void loginMeme(String meUsername, String mePwd) {
        //获取到设备列表
        if (memeDisposable != null && !memeDisposable.isDisposed()) {
            memeDisposable.dispose();
        }
        Observable<Boolean> loginMeme = MemeManager.getInstance().LoginMeme(meUsername, mePwd, MyApplication.getInstance());
        Observable<List<Device>> devicesChange = MemeManager.getInstance().listDevicesChange();
        memeDisposable = Observable.zip(loginMeme, devicesChange,
                new BiFunction<Boolean, List<Device>, Boolean>() {
                    @Override
                    public Boolean apply(Boolean aBoolean, List<Device> devices) throws Exception {
                    //    LogUtils.e("米米网登陆成功  且网关在线");
                         Log.e(GeTui.VideoLog,"米米网登陆成功  且网关在线........");
                        if (aBoolean && devices.size() > 0) { //米米网登陆成功且网关在线  正常到此处，那么都应该是成功的
                            return true;
                        }
                        return false;
                    }
                })
                .timeout(5 * 1000, TimeUnit.MILLISECONDS)
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (memeDisposable != null && !memeDisposable.isDisposed()) {
                            memeDisposable.dispose();
                        }
//                        if (!isCallIn) {
//                            if (aBoolean) { // 米米网登陆成功且网关在线
//                                LogUtils.e("米米网  登陆成功   呼叫猫眼");
//                                wakeupCatEye(cateEyeInfo);
//                            } else { //米米网登陆失败或者网关不在线
//                                if (mViewRef.get() != null) {
//                                    mViewRef.get().loginMemeFailed();
//                                    MemeManager.getInstance().videoActivityDisconnectMeme();
//                                }
//                                isCalling = false;
//                            }
//                        } else { //米米网登录成功的话   通知界面   有电话呼叫过来  弹出对话框
                            if (aBoolean) { // 米米网登陆成功且网关在线
                            //    LogUtils.e("米米网  登陆成功   呼叫猫眼");
                                Log.e(GeTui.VideoLog,"米米网  登陆成功........");
                                if (mViewRef.get() != null) {
                                    mViewRef.get().onCatEyeCallIn();
                                }
                            } else { //米米网登陆失败或者网关不在线  不处理
                                if (mViewRef.get() != null) {
                                    mViewRef.get().loginMemeFailed();
                                    MemeManager.getInstance().videoActivityDisconnectMeme();
                                }
                       //     }
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(GeTui.VideoLog,"登录米米网失败或者设备不在线........");
                            if (mViewRef.get() != null) {
                                mViewRef.get().loginMemeFailed();
                            }

                        MemeManager.getInstance().videoActivityDisconnectMeme();
                    }
                });
                  compositeDisposable.add(memeDisposable);
    }



}
