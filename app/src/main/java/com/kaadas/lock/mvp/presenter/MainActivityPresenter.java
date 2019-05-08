package com.kaadas.lock.mvp.presenter;

import android.text.TextUtils;


import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.activity.cateye.VideoVActivity;
import com.kaadas.lock.bean.HomeShowBean;
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

import net.sdvn.cmapi.Device;

import org.linphone.core.LinphoneCall;
import org.linphone.mediastream.Log;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
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
    private Disposable memeLoginDisposable;
    private Disposable memeDisposable;
    private Disposable deviceChangeDisposable;
    private CateEyeInfo callInCatEyeInfo;  //呼叫进来的猫眼信息

    @Override
    public void authSuccess() {

    }

    @Override
    public void attachView(IMainActivityView view) {
        super.attachView(view);
        //网关上线监听
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
        List<HomeShowBean> homeShowDevices = MyApplication.getInstance().getHomeShowDevices();
        for (HomeShowBean homeShowBean : homeShowDevices) {
            if (homeShowBean.getDeviceType() == HomeShowBean.TYPE_BLE_LOCK) {
                BleLockInfo bleLockInfo = (BleLockInfo) homeShowBean.getObject();
                if (bleLockInfo.getServerLockInfo().getLockName().equals(name)) {
                    return bleLockInfo.getServerLockInfo().getLockNickName();
                }
            }
        }
        return "";
    }

    //获取网关状态通知
    public void getPublishNotify() {
        disposable = MyApplication.getInstance().getMqttService().listenerNotifyData()
                .subscribe(new Consumer<MqttData>() {
                    @Override
                    public void accept(MqttData mqttData) throws Exception {
                        if (mqttData != null) {
                            if (MqttConstant.GATEWAY_STATE.equals(mqttData.getFunc())) {
                                GetBindGatewayStatusResult gatewayStatusResult = new Gson().fromJson(mqttData.getPayload(), GetBindGatewayStatusResult.class);
                                LogUtils.e("监听网关的状态"+gatewayStatusResult.getDevuuid());
                                if (gatewayStatusResult != null) {
                                    List<HomeShowBean> homeShowBeans=MyApplication.getInstance().getAllDevices();
                                   if (homeShowBeans.size()>0) {
                                       for (HomeShowBean homeShowBean : homeShowBeans) {
                                           if (homeShowBean.getDeviceType() == HomeShowBean.TYPE_GATEWAY) {
                                               GatewayInfo gatewayInfo = (GatewayInfo) homeShowBean.getObject();
                                               if (gatewayInfo.getServerInfo().getDeviceSN().equals(gatewayStatusResult.getDevuuid())) {
                                                   LogUtils.e("监听网关的状态" + gatewayStatusResult.getDevuuid());
                                                   gatewayInfo.setEvent_str(gatewayStatusResult.getData().getState());
                                               }
                                           }
                                       }
                                   }else{
                                       SPUtils.put(gatewayStatusResult.getDevuuid(),gatewayStatusResult.getData().getState());
                                   }
                                }

                            }
                        }
                    }
                });
        compositeDisposable.add(disposable);
    }

    //获取锁设备

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
                    if (VideoVActivity.isRunning) {
                        LogUtils.e("Linphone  收到来电   VideoActivity已经运行  不出来  ");
                        return;
                    }
                    //设置呼叫进来的时间
                    MyApplication.getInstance().setIsComingTime(System.currentTimeMillis());
                    //获取linphone的地址
                    String linphoneSn = linphoneCall.getRemoteAddress().getUserName();
                    LogUtils.e("Linphone   呼叫过来的linphoneSn   " + linphoneSn);
                    String gwId = "";
                    GatewayInfo gatewayInfo = null;
                    List<CateEyeInfo> cateEyes = MyApplication.getInstance().getAllBindDevices().getCateEyes();
                    for (CateEyeInfo cateEyeInfo : cateEyes) {
                        LogUtils.e("猫眼的  getDeviceId  " + cateEyeInfo.getServerInfo().getDeviceId());
                        if (linphoneSn.equalsIgnoreCase(cateEyeInfo.getServerInfo().getDeviceId())) {
                            LogUtils.e("获取到网关Id为  " + cateEyeInfo.getGwID());
                            gwId = cateEyeInfo.getGwID();
                            gatewayInfo = cateEyeInfo.getGatewayInfo();
                            callInCatEyeInfo = cateEyeInfo;
                        }
                    }
                    //如果网关Id为空    不朝下走了
                    if (TextUtils.isEmpty(gwId) || gatewayInfo == null) {
                        return;
                    }
                    //获取米米网账号情况
                    int meBindState = gatewayInfo.getServerInfo().getMeBindState();
                    String mePwd = gatewayInfo.getServerInfo().getMePwd();
                    String meUsername = gatewayInfo.getServerInfo().getMeUsername();
                    if (TextUtils.isEmpty(meUsername) || TextUtils.isEmpty(mePwd)) {
                        //如果账号或者密码有一个为空  直接退出
                        return;
                    }
                    if (MemeManager.getInstance().isConnected()) { //meme网已经连接
                        //如果meme网登陆的账号密码为当前的账号密码，直接发起通信
                        if (meUsername.equals(MemeManager.getInstance().getCurrentAccount())
                                && mePwd.equals(MemeManager.getInstance().getCurrentPassword())) {
                            //查看是否有设备在线   如果有  弹出来电框
                            if (MemeManager.getInstance().getGwDevices().size() > 0) {
                                if (mViewRef.get()!=null){
                                    mViewRef.get().onCatEyeCallIn(callInCatEyeInfo);
                                }
                            } else {  //本地登录了米米网账号且是当前猫眼的meme网昂好   但是没有本地设备在线  等待五秒
                                listDeviceChange();
                            }
                        } else {  //本地登录的meme网账号不是呼叫进来猫眼的meme网账号   不处理

                        }
                    } else { //meme网没有连接
                        loginMeme(meUsername, mePwd);
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
//                    LinphoneHelper.toggleSpeaker(true);
//                    // 所有通话默认非静音
//                    LinphoneHelper.toggleMicro(false);
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

    /**
     * 登录米米网
     * @param meUsername
     * @param mePwd
     */
    private void loginMeme(String meUsername, String mePwd) {
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
                        LogUtils.e("米米网登陆成功  且网关在线");
                        if (aBoolean && devices.size() > 0) { //米米网登陆成功且网关在线  正常到此处，那么都应该是成功的
                            return true;
                        }
                        return false;
                    }
                })
                .compose(RxjavaHelper.observeOnMainThread())
                .timeout(5 * 1000, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (memeDisposable != null && !memeDisposable.isDisposed()) {
                            memeDisposable.dispose();
                        }
                        if (aBoolean) { // 米米网登陆成功且网关在线
                            LogUtils.e("米米网  登陆成功   弹出来电框");
                            if (mViewRef.get()!=null){
                                mViewRef.get().onCatEyeCallIn(callInCatEyeInfo);
                            }
                        } else { //米米网登陆失败或者网关不在线   不做处理

                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtils.e("登录米米网失败或者设备部在线");
                    }
                });
    }


    private void listDeviceChange(){
        deviceChangeDisposable = MemeManager.getInstance().listDevicesChange()
                .compose(RxjavaHelper.observeOnMainThread())
                .timeout(5 * 1000, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<List<Device>>() {
                    @Override
                    public void accept(List<Device> devices) throws Exception {
                        toDisposable(deviceChangeDisposable);
                        if (devices.size()>1){
                            if (mViewRef.get()!=null){
                                mViewRef.get().onCatEyeCallIn(callInCatEyeInfo);
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        compositeDisposable.add(deviceChangeDisposable);
    }

    @Override
    public void detachView() {
        super.detachView();
        LinphoneHelper.deleteUser();
    }
}
