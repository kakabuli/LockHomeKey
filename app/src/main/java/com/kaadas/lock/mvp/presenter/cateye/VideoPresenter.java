package com.kaadas.lock.mvp.presenter.cateye;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.cateye.IVideoView;
import com.kaadas.lock.publiclibrary.bean.CateEyeInfo;
import com.kaadas.lock.publiclibrary.bean.GatewayInfo;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.linphone.MemeManager;
import com.kaadas.lock.publiclibrary.linphone.linphone.callback.PhoneAutoAccept;
import com.kaadas.lock.publiclibrary.linphone.linphone.util.LinphoneHelper;
import com.kaadas.lock.publiclibrary.linphone.linphone.util.Util;
import com.kaadas.lock.publiclibrary.linphone.linphonenew.LinphoneManager;
import com.kaadas.lock.publiclibrary.mqtt.MqttCommandFactory;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;
import com.kaadas.lock.utils.Constants;
import com.kaadas.lock.utils.CountUpTimer;
import com.kaadas.lock.utils.FileUtils;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.db.MediaFileDBDao;

import net.sdvn.cmapi.Device;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.linphone.core.LinphoneCall;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public class VideoPresenter<T> extends BasePresenter<IVideoView> {

    private Disposable memeDisposable;
    private Disposable deviceChangeDisposable;
    private static String Tag = "视频界面";
    private MediaFileDBDao mMediaDBDao;
    private long startRecordTime;
    private boolean isRecoding = false;
    private boolean isCalling;  //正在呼叫猫眼
    private Disposable wakeupDisposable;
    private boolean wakeupSuccess;
    private CateEyeInfo currentCateEyeInfo;
    private boolean isConnected = false;

    public void init(Context context) {
        mMediaDBDao = MediaFileDBDao.getInstance(context);
        //设置麦克风不静音
        LinphoneHelper.toggleMicro(false);
        //默认关闭免提
        LinphoneHelper.toggleSpeaker(false);

        listenerCallStatus();
    }


    public void listenerCallStatus() {
        LinphoneHelper.addAutoAcceptCallBack(new PhoneAutoAccept() {
            @Override
            public void incomingCall(LinphoneCall linphoneCall) {

                //猫眼的设备Id
                String catEyeDeviceId = linphoneCall.getRemoteAddress().getUserName();
                CateEyeInfo callInCatEyeInfo = null;
                //呼叫过来的是当前的猫眼
                if (currentCateEyeInfo != null && catEyeDeviceId.equalsIgnoreCase(currentCateEyeInfo.getServerInfo().getDeviceId())) {
                    Log.e(Tag, "猫眼   incomingCall1.........");
                    String deviceIp = MemeManager.getInstance().getDeviceIp();
                    LinphoneHelper.acceptCall(deviceIp);
                    handler.removeCallbacks(timeoutRunnable);
                } else {

                    Log.e(Tag, "猫眼   incomingCall2.........");
                    String gwId = "";
                    GatewayInfo gatewayInfo = null;
                    List<CateEyeInfo> cateEyes = MyApplication.getInstance().getAllBindDevices().getCateEyes();
                    for (CateEyeInfo cateEyeInfo : cateEyes) {
                        LogUtils.e("猫眼的  getDeviceId  " + cateEyeInfo.getServerInfo().getDeviceId());
                        if (catEyeDeviceId.equalsIgnoreCase(cateEyeInfo.getServerInfo().getDeviceId())) {
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
                                if (mViewRef.get() != null) {
                                    mViewRef.get().onCatEyeCallIn();
                                }
                            } else {  //本地登录了米米网账号且是当前猫眼的meme网昂好   但是没有本地设备在线  等待五秒
                                listDeviceChange(callInCatEyeInfo, true);
                            }
                        } else {  //本地登录的meme网账号不是呼叫进来猫眼的meme网账号   不处理

                        }
                    } else { //meme网没有连接
                        loginMeme(meUsername, mePwd, callInCatEyeInfo, true);
                    }
                }
            }

            @Override
            public void callConnected() {
                Log.e(Tag, "猫眼1  callConnected.........");
                startCountUp();
                isConnected = true;
                if (mViewRef.get() != null) {
                    mViewRef.get().onCallConnected();
                }

            }

            @Override
            public void callReleased() {
                Log.e(Tag, "猫眼  callReleased.........");
            }

            @Override
            public void callFinish() {
                Log.e(Tag, "猫眼 callFinish.........");
                if (mViewRef.get() != null) {
                    mViewRef.get().onCallFinish();
                }
                MemeManager.getInstance().videoActivityDisconnectMeme();
            }

            @Override
            public void Streaming() {
                Log.e(Tag, "猫眼 Streaming.........");

            }
        });
    }


    public void toCapturePicture(String deviceId) {
        String mPicturePath = null;
        try {
            long timeMillis = System.currentTimeMillis();
            FileUtils.createFolder(Util.PICTURE_DIR);
            mPicturePath = Util.PICTURE_DIR + "/" + timeMillis + deviceId + ".jpeg";
            LinphoneManager.getLc().getCurrentCall().takeSnapshot(mPicturePath);
            mMediaDBDao.add(timeMillis + deviceId + ".jpeg", String.valueOf(timeMillis), 2, mPicturePath);
            if (mViewRef.get() != null) {
                mViewRef.get().screenShotSuccess();
            }

        } catch (Exception e) {
            if (mPicturePath != null) {
                File file = new File(mPicturePath);
                if (file.exists()) {
                    file.delete();
                }
            }
            if (mViewRef.get() != null) {
                mViewRef.get().screenShotFailed(e);
            }
        }
    }


    public void recordVideo(boolean isRecord, String deviceId) {
        if (isRecord) {
            if (!isRecoding) {
                try {
                    File oldFile = new File(Util.RECORD_VIDEO_PATH);
                    if (oldFile.exists()) {
                        oldFile.delete();
                    }
                    LinphoneManager.getLc().getCurrentCall().startRecording();
                    isRecoding = true;
                    startRecordTime = System.currentTimeMillis();
                } catch (Exception e) {
                    LogUtils.d("开启录屏失败 " + e);
                }
            }
        } else {
            if (System.currentTimeMillis() - startRecordTime > 5 * 1000) {
                stopRecordVideo(deviceId);
            } else {
                if (mViewRef.get() != null) {
                    mViewRef.get().recordTooShort();
                }
            }
        }
    }


    private void stopRecordVideo(String deviceId) {
        isRecoding = false;
        LinphoneManager.getLc().getCurrentCall().stopRecording();
        File oldFile = new File(Util.RECORD_VIDEO_PATH);
        long timeMillis = System.currentTimeMillis();
        FileUtils.createFolder(Util.VIDEO_DIR);
        //文件名以设备id结尾以区分不同猫眼的回放视频
        String newFilePath = Util.VIDEO_DIR + "/" + timeMillis + deviceId + ".mkv";
        File newFile = new File(newFilePath);
        String fileName = newFile.getName();
        if (oldFile.exists()) {
            oldFile.renameTo(newFile);
        }
        addVideoFile(fileName, String.valueOf(timeMillis), Constants.MEDIA_TYPE_VIDEO, newFile.getAbsolutePath());
    }

    public void addVideoFile(String fileName, String createTime, int type, String path) {
        mMediaDBDao.add(fileName, String.valueOf(createTime), type, path);
    }


    public void callCatEye(CateEyeInfo cateEyeInfo) {
        //判断网关是否在线

        //判断猫眼是否在线
       if ("offline".equals( cateEyeInfo.getServerInfo().getEvent_str())){
           //猫眼离线状态
            if (mViewRef.get()!=null){
                mViewRef.get().onCatEyeOffline();
            }
           return;
       }
        //meme网状态
        isCalling = true;
        GatewayInfo gatewayInfo = cateEyeInfo.getGatewayInfo();
        String meUsername = gatewayInfo.getServerInfo().getMeUsername();
        String mePwd = gatewayInfo.getServerInfo().getMePwd();
        if (MemeManager.getInstance().isConnected()) { //meme网已经连接
            //如果meme网登陆的账号密码为当前的账号密码，直接发起通信
            if (meUsername.equals(MemeManager.getInstance().getCurrentAccount())
                    && mePwd.equals(MemeManager.getInstance().getCurrentPassword())) {
                //查看是否有设备在线   如果有  弹出来电框
                if (MemeManager.getInstance().getGwDevices().size() > 0) {
                    wakeupCatEye(cateEyeInfo);
                } else {  //本地登录了米米网账号且是当前猫眼的meme网昂好   但是没有本地设备在线  等待五秒
                    listDeviceChange(cateEyeInfo, false);
                }
            } else {  //本地登录的meme网账号不是呼叫进来猫眼的meme网账号   不处理
                MemeManager.getInstance().videoActivityDisconnectMeme();
                loginMeme(meUsername, mePwd, cateEyeInfo, false);
            }
        } else { //meme网没有连接
            loginMeme(meUsername, mePwd, cateEyeInfo, false);
        }
    }


    /**
     * 登录米米网
     *
     * @param meUsername
     * @param mePwd
     * @param isCallIn   是不是猫眼呼叫过来的
     */
    private void loginMeme(String meUsername, String mePwd, CateEyeInfo cateEyeInfo, boolean isCallIn) {
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
                        if (!isCallIn) {
                            if (aBoolean) { // 米米网登陆成功且网关在线
                                LogUtils.e("米米网  登陆成功   呼叫猫眼");
                                wakeupCatEye(cateEyeInfo);
                            } else { //米米网登陆失败或者网关不在线
                                if (mViewRef.get() != null) {
                                    mViewRef.get().loginMemeFailed();
                                    MemeManager.getInstance().videoActivityDisconnectMeme();
                                }
                                isCalling = false;
                            }
                        } else { //米米网登录成功的话   通知界面   有电话呼叫过来  弹出对话框
                            if (aBoolean) { // 米米网登陆成功且网关在线
                                LogUtils.e("米米网  登陆成功   呼叫猫眼");
                                if (mViewRef.get() != null) {
                                    mViewRef.get().onCatEyeCallIn();
                                }
                            } else { //米米网登陆失败或者网关不在线  不处理
                                if (mViewRef.get() != null) {
                                    mViewRef.get().loginMemeFailed();
                                    MemeManager.getInstance().videoActivityDisconnectMeme();
                                }
                            }
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtils.e("登录米米网失败或者设备不在线");
                        if (!isCallIn) {
                            if (mViewRef.get() != null) {
                                mViewRef.get().loginMemeFailed();
                            }
                            isCalling = false;
                        } else { //登录米米网失败或者设备不在线   如果是猫眼呼叫过来的话  不做处理

                        }
                        MemeManager.getInstance().videoActivityDisconnectMeme();
                    }
                });
        compositeDisposable.add(memeDisposable);
    }

    /**
     * @param cateEyeInfo
     * @param isCallIn    是不是猫眼呼叫过来的
     */
    private void listDeviceChange(CateEyeInfo cateEyeInfo, boolean isCallIn) {
        deviceChangeDisposable = MemeManager.getInstance().listDevicesChange()
                .compose(RxjavaHelper.observeOnMainThread())
                .timeout(5 * 1000, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<List<Device>>() {
                    @Override
                    public void accept(List<Device> devices) throws Exception {
                        toDisposable(deviceChangeDisposable);
                        if (devices.size() > 1) {
                            if (!isCallIn) {
                                LogUtils.e("米米网  登陆成功   呼叫猫眼");
                                wakeupCatEye(cateEyeInfo);
                            } else {
                                if (mViewRef.get() != null) {
                                    mViewRef.get().onCatEyeCallIn();
                                }
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (!isCallIn) {  //监听设备在线情况   主动呼叫  提示失败   被呼叫不做处理
                            if (mViewRef.get() != null) {
                                mViewRef.get().loginMemeFailed();
                            }
                        } else {

                        }
                    }
                });
        compositeDisposable.add(deviceChangeDisposable);
    }


    /**
     * 唤醒猫眼
     *
     * @param cateEyeInfo
     */
    private void wakeupCatEye(CateEyeInfo cateEyeInfo) {
        toDisposable(wakeupDisposable);
        MqttMessage mqttMessage = MqttCommandFactory.wakeupCamera(cateEyeInfo.getServerInfo().getDeviceId(), cateEyeInfo.getGwID(), MyApplication.getInstance().getUid());
        waitCall();
        long start = System.currentTimeMillis();
        LogUtils.e("唤醒猫眼   ");
        wakeupSuccess = true;
        currentCateEyeInfo = cateEyeInfo;
        wakeupDisposable = mqttService.mqttPublish(MqttConstant.getCallTopic(MyApplication.getInstance().getUid()), mqttMessage)
                .filter(new Predicate<MqttData>() {
                    @Override
                    public boolean test(MqttData mqttData) throws Exception {
                        if (mqttData.getMessageId() == mqttMessage.getId()) {
                            return true;
                        }
                        return false;
                    }
                })
                .compose(RxjavaHelper.observeOnMainThread())
                .timeout(25 * 1000, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<MqttData>() {
                    @Override
                    public void accept(MqttData mqttData) throws Exception {
                        toDisposable(wakeupDisposable);
                        if ("200".equals(mqttData.getReturnCode())) {
                            LogUtils.e("唤醒猫眼成功");
                            if (mViewRef.get() != null) {
                                mViewRef.get().wakeupSuccess();
                            }
                        } else {
                            //407  猫眼离线  猫眼唤醒失败
                            LogUtils.e("唤醒猫眼失败   " + mqttData.getReturnCode() + "   耗时  " + (System.currentTimeMillis() - start));
                            if (mViewRef.get() != null && !isConnected) {
                                mViewRef.get().wakeupFailed();
                            }
                            wakeupSuccess = false;
                            handler.removeCallbacks(timeoutRunnable);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (mViewRef.get() != null && !isConnected) {
                            mViewRef.get().wakeupFailed();
                        }
                        handler.removeCallbacks(timeoutRunnable);
                        wakeupSuccess = false;
                    }
                });
    }

    private Runnable timeoutRunnable = new Runnable() {
        @Override
        public void run() {
            wakeupSuccess = false;
            currentCateEyeInfo = null;
            if (mViewRef.get() != null) {
                mViewRef.get().waitCallTimeout();
            }
        }
    };

    public void waitCall() {
        handler.postDelayed(timeoutRunnable, 35 * 1000);
    }

    @Override
    public void detachView() {
        super.detachView();
        handler.removeCallbacks(timeoutRunnable);
        countUpTimer.stop();
    }


    private SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
    private CountUpTimer countUpTimer = new CountUpTimer(1000) {
        @Override
        public void onTick(long millis) {
            if (mViewRef.get()!=null){
                formatter.setTimeZone(TimeZone.getTimeZone("GMT+0"));
                String time = formatter.format(new Date(millis));
                LogUtils.e("通话时间为   " +time);
                mViewRef.get().callTimes(time);
            }
        }
    };


    /**
     * 开始计时
     */
    private void startCountUp(){
        LogUtils.e("开始计时");
        countUpTimer.start();
    }

}
