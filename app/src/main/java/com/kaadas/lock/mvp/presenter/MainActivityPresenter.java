package com.kaadas.lock.mvp.presenter;

import android.text.TextUtils;


import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BlePresenter;
import com.kaadas.lock.mvp.view.IMainActivityView;
import com.kaadas.lock.publiclibrary.bean.BleLockInfo;
import com.kaadas.lock.publiclibrary.bean.CateEyeInfo;
import com.kaadas.lock.publiclibrary.bean.GatewayInfo;
import com.kaadas.lock.publiclibrary.ble.BleUtil;
import com.kaadas.lock.publiclibrary.ble.responsebean.BleDataBean;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.linphone.MemeManager;
import com.kaadas.lock.publiclibrary.linphone.linphone.callback.PhoneCallback;
import com.kaadas.lock.publiclibrary.linphone.linphone.callback.RegistrationCallback;
import com.kaadas.lock.publiclibrary.linphone.linphone.util.LinphoneHelper;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.GetBindGatewayStatusResult;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.Rsa;
import com.kaadas.lock.utils.SPUtils;

import org.linphone.core.LinphoneCall;

import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

/**
 * Create By lxj  on 2019/3/18
 * Describe
 */
public class MainActivityPresenter<T> extends BlePresenter<IMainActivityView> {

    private Disposable warringDisposable;
    private Disposable deviceInBootDisposable;
    private Disposable disposable;

    @Override
    public void authSuccess() {

    }

    @Override
    public void attachView(IMainActivityView view) {
        super.attachView(view);
        getPublishNotify();
        //设置警报提醒
        toDisposable(warringDisposable);
        warringDisposable = bleService.listeneDataChange()
                .filter(new Predicate<BleDataBean>() {
                    @Override
                    public boolean test(BleDataBean bleDataBean) throws Exception {
                        return bleDataBean.getCmd() == 0x07;
                    }
                })
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<BleDataBean>() {
                    @Override
                    public void accept(BleDataBean bleDataBean) throws Exception {
                        if (MyApplication.getInstance().getBleService().getBleLockInfo().getAuthKey() == null || MyApplication.getInstance().getBleService().getBleLockInfo().getAuthKey().length == 0) {
                            LogUtils.e("收到报警记录，但是鉴权帧为空");
                            return;
                        }
                        bleDataBean.getDevice().getName();
                        bleDataBean.getCmd();
                        byte[] deValue = Rsa.decrypt(bleDataBean.getPayload(), MyApplication.getInstance().getBleService().getBleLockInfo().getAuthKey());
                        LogUtils.e("收到报警上报    " + Rsa.toHexString(deValue));
                        String nickNameByDeviceName = getNickNameByDeviceName(bleDataBean.getDevice().getName());
                        int state9 = (deValue[5] & 0b00000010) == 0b00000010 ? 1 : 0;
                        MyApplication.getInstance().getBleService().getBleLockInfo().setSafeMode(state9);
                        if (!TextUtils.isEmpty(nickNameByDeviceName)) {
                            String warringContent = BleUtil.parseWarring(MyApplication.getInstance(), deValue, nickNameByDeviceName);
                            if (mViewRef.get() != null) {
                                mViewRef.get().onWarringUp(warringContent);
                            }
                        } else {
                            LogUtils.e("收到报警记录，但是缓存信息中没有该设备  ");
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        compositeDisposable.add(warringDisposable);


        toDisposable(deviceInBootDisposable);
        deviceInBootDisposable = bleService.onDeviceStateInBoot()
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<BleLockInfo>() {
                    @Override
                    public void accept(BleLockInfo bleLockInfo) throws Exception {
                        LogUtils.e("设备  正在升级模式   " + bleLockInfo.getServerLockInfo().toString());
                        if (mViewRef.get() != null) {
                            mViewRef.get().onDeviceInBoot(bleLockInfo);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtils.e("设备   正在升级模式   监听失败    " + bleLockInfo.getServerLockInfo().toString());
                    }
                });
        compositeDisposable.add(deviceInBootDisposable);

    }

    public String getNickNameByDeviceName(String name) {
        List<BleLockInfo> devices = MyApplication.getInstance().getDevices();
        if (devices != null && devices.size() > 0) {
            for (BleLockInfo lockInfo : devices) {
                if (lockInfo.getServerLockInfo().getDevice_name().equals(name)) {
                    return lockInfo.getServerLockInfo().getDevice_nickname();
                }
            }
        }
        return "";
    }

    //获取通知
    public void getPublishNotify() {
        disposable = MyApplication.getInstance().getMqttService().listenerNotifyData()
                .subscribe(new Consumer<MqttData>() {
                    @Override
                    public void accept(MqttData mqttData) throws Exception {
                        if (mqttData != null) {
                            if (MqttConstant.GATEWAY_STATE.equals(mqttData.getFunc())) {
                                GetBindGatewayStatusResult gatewayStatusResult = new Gson().fromJson(mqttData.getPayload(), GetBindGatewayStatusResult.class);
                                if (gatewayStatusResult != null) {
                                    SPUtils.putProtect(gatewayStatusResult.getDevuuid(), gatewayStatusResult.getData().getState());
                                }

                            }
                        }
                    }
                });
        compositeDisposable.add(disposable);
    }

    public void initLinphone() {
        if (!TextUtils.isEmpty(MyApplication.getInstance().getToken()) && !TextUtils.isEmpty(MyApplication.getInstance().getUid())) {
            LinphoneHelper.setAccount(MyApplication.getInstance().getUid(), "12345678Bm", MqttConstant.LINPHONE_URL);
            LogUtils.e("设置LinPhone  监听  ");
            LinphoneHelper.addCallback(new RegistrationCallback() {
                @Override
                public void registrationNone() {
                    super.registrationNone();
                }

                @Override
                public void registrationProgress() {
                    super.registrationProgress();
                }

                @Override
                public void registrationOk() {
                    super.registrationOk();
//                    LogUtils.e("Linphone注册成功     ");
                }

                @Override
                public void registrationCleared() {
                    super.registrationCleared();
                }

                @Override
                public void registrationFailed() {
                    super.registrationFailed();
                    LogUtils.e("Linphone注册失败     ");
                }
            }, new PhoneCallback() {


                @Override
                public void incomingCall(LinphoneCall linphoneCall) {
                    //收到来电通知
                    LogUtils.e("Linphone  收到来电     ");
                    if (MyApplication.getInstance().isVideoActivityRun()){
                        LogUtils.e("Linphone  收到来电   VideoActivity已经运行  不出来  ");
                        return;
                    }
                    //设置呼叫进来的时间
                    MyApplication.getInstance().setIsComingTime(System.currentTimeMillis());
                    //获取linphone的地址
                    String linphoneSn =linphoneCall.getRemoteAddress().getUserName();
                    LogUtils.e("Linphone   呼叫过来的linphoneSn   "  + linphoneSn);
                    String gwId = "";
                    GatewayInfo gatewayInfo = null;
                    List<CateEyeInfo> cateEyes = MyApplication.getInstance().getAllBindDevices().getCateEyes();
                    for (CateEyeInfo cateEyeInfo:cateEyes ) {
                        if (linphoneSn.equalsIgnoreCase(cateEyeInfo.getServerInfo().getDeviceId())){
                            LogUtils.e("获取到网关Id为  " + cateEyeInfo.getGwID());
                            gwId = cateEyeInfo.getGwID();
                            gatewayInfo = cateEyeInfo.getGatewayInfo();
                        }
                    }

                    //如果网关Id为空    不朝下走了
                    if (TextUtils.isEmpty(gwId) ||gatewayInfo == null ){
                        return;
                    }
                    //获取米米网账号情况
                    int meBindState = gatewayInfo.getServerInfo().getMeBindState();
                    String mePwd = gatewayInfo.getServerInfo().getMePwd();
                    String meUsername = gatewayInfo.getServerInfo().getMeUsername();
                    if (MemeManager.getInstance().isConnected()){ //meme网已经连接


                    }else { //meme网没有连接
                        MemeManager.getInstance().LoginMeme(meUsername,mePwd,MyApplication.getInstance());
                    }
                }

                @Override
                public void outgoingInit() {
                    super.outgoingInit();




                }

                @Override
                public void callConnected() {
                    super.callConnected();

                    LogUtils.e("Linphone  通话连接成功   ");
                    // 视频通话默认免提，语音通话默认非免提
                    LinphoneHelper.toggleSpeaker(true);
                    // 所有通话默认非静音
                    LinphoneHelper.toggleMicro(false);



                }

                @Override
                public void callEnd() {
                    super.callEnd();
                    LogUtils.e("Linphone  通话结束   ");
                }
            });

            LogUtils.e("登录linphone   UID   " + MyApplication.getInstance().getUid());
            LinphoneHelper.login();
        } else {
            LogUtils.e("登录linphone   失败   uid或者token为空   ");

        }

    }


}
