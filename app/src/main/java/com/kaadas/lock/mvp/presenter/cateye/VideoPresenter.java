package com.kaadas.lock.mvp.presenter.cateye;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.cateye.IVideoView;
import com.kaadas.lock.publiclibrary.bean.CateEyeInfo;
import com.kaadas.lock.publiclibrary.bean.GatewayInfo;
import com.kaadas.lock.publiclibrary.bean.GwLockInfo;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.linphone.MemeManager;
import com.kaadas.lock.publiclibrary.linphone.linphone.callback.PhoneAutoAccept;
import com.kaadas.lock.publiclibrary.linphone.linphone.util.LinphoneHelper;
import com.kaadas.lock.publiclibrary.linphone.linphone.util.Util;
import com.kaadas.lock.publiclibrary.linphone.linphonenew.LinphoneManager;
import com.kaadas.lock.publiclibrary.mqtt.MqttCommandFactory;
import com.kaadas.lock.publiclibrary.mqtt.eventbean.DeviceOnLineBean;
import com.kaadas.lock.publiclibrary.mqtt.eventbean.GatewayLockInfoEventBean;
import com.kaadas.lock.publiclibrary.mqtt.eventbean.OpenLockNotifyBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.OpenLockBean;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;
import com.kaadas.lock.utils.Constants;
import com.kaadas.lock.utils.CountUpTimer;
import com.kaadas.lock.utils.FileUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.MyLog;
import com.kaadas.lock.utils.RecordTools;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.db.MediaFileDBDao;
import com.kaadas.lock.utils.ftp.GeTui;
import com.kaadas.lock.utils.networkListenerutil.NetWorkChangReceiver;

import net.sdvn.cmapi.Device;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;
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
    private static String Tag = "猫眼通话界面";
    private MediaFileDBDao mMediaDBDao;
    private long startRecordTime;
    private boolean isRecoding = false;
    public boolean isCalling;  //正在呼叫猫眼
    private Disposable wakeupDisposable;
    private boolean wakeupSuccess;
    private CateEyeInfo currentCateEyeInfo;
    public boolean isConnected = false;
    private Disposable openLockDisposable;
    private String recordDeviceId;
    private Disposable closeLockNotifyDisposable;
    private Disposable lockCloseDisposable;
    private Context mContext;
    private Disposable listenerDeviceOnLineDisposable;
    private Disposable networkChangeDisposable;
    private Disposable openLockEventDisposable;
    private Disposable closeDisposable;
    public void init(Context context) {
        this.mContext = context;
        mMediaDBDao = MediaFileDBDao.getInstance(context);
        //设置麦克风不静音
        LinphoneHelper.toggleMicro(false);
        //默认关闭免提
        LinphoneHelper.toggleSpeaker(false);

        listenerCallStatus();
    }

    public  boolean isConnectedEye=false;
    final int CLOSE=1;
    int CLOSE_TIME=15*1000;
   Handler videoHandler=new Handler(){
       @Override
       public void handleMessage(Message msg) {
           super.handleMessage(msg);
           switch (msg.what) {
               case  CLOSE:
                   Log.e(GeTui.VideoLog,"音视频传输失败,请重新呼叫");
                   MyLog.getInstance().save("音视频传输失败,请重新呼叫");
                Toast.makeText(mContext,mContext.getString(R.string.cateye_video_audio_send_fail),Toast.LENGTH_LONG).show();
                   if (mViewRef.get() != null) {
                       mViewRef.get().closeMain();
                   }
                   break;
           }
       }
   };

    public void listenerCallStatus() {
        LinphoneHelper.addAutoAcceptCallBack(new PhoneAutoAccept() {
            @Override
            public void incomingCall(LinphoneCall linphoneCall) {
                if (!RecordTools.validateMicAvailability()) {  //打开
                    Toast.makeText(mContext, mContext.getString(R.string.cateye_call_record), Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.e(GeTui.VideoLog, "VideoPresenter==>incomingCalll....");
                Log.e(Tag, "猫眼   incomingCall1 ");
                MyLog.getInstance().save("猫眼   incomingCall1 ");
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
                        LogUtils.e(Tag, "猫眼的  getDeviceId  " + cateEyeInfo.getServerInfo().getDeviceId());
                        if (catEyeDeviceId.equalsIgnoreCase(cateEyeInfo.getServerInfo().getDeviceId())) {
                            LogUtils.e(Tag, "获取到网关Id为  " + cateEyeInfo.getGwID());
                            gwId = cateEyeInfo.getGwID();
                            List<GatewayInfo> allGateway = MyApplication.getInstance().getAllGateway();
                            for (GatewayInfo info : allGateway) {
                                if (cateEyeInfo.getGwID().equals(info.getServerInfo().getDeviceSN())) {
                                    gatewayInfo = info;
                                    break;
                                }
                            }
                            callInCatEyeInfo = cateEyeInfo;
                        }
                    }
                    //如果网关Id为空    不朝下走了
                    if (TextUtils.isEmpty(gwId) || gatewayInfo == null) {
                        Toast.makeText(mContext,mContext.getString(R.string.call_error_cateInfoEmpty),Toast.LENGTH_LONG).show();
                        return;
                    }
                    //获取米米网账号情况
                    int meBindState = gatewayInfo.getServerInfo().getMeBindState();
                    String mePwd = gatewayInfo.getServerInfo().getMePwd();
                    String meUsername = gatewayInfo.getServerInfo().getMeUsername();
                    if (TextUtils.isEmpty(meUsername) || TextUtils.isEmpty(mePwd)) {
                        //如果账号或者密码有一个为空  直接退出
                        Toast.makeText(mContext,mContext.getString(R.string.call_error_mimi),Toast.LENGTH_LONG).show();
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
                Message msg= Message.obtain();
                msg.what=CLOSE;
                videoHandler.sendMessageDelayed(msg,CLOSE_TIME);
                Log.e(Tag, "猫眼1  callConnected.........");
                Log.e(GeTui.VideoLog, "VideoPresenter==>callConnected....");
                MyLog.getInstance().save("猫眼1  callConnected.........");
                startCountUp();
                isConnected = true;
                if (mViewRef.get() != null) {
                    mViewRef.get().onCallConnected();
                }

            }

            @Override
            public void callReleased() {
                Log.e(GeTui.VideoLog, "VideoPresenter==>callReleased....");
                Log.e(Tag, "猫眼  callReleased.........");
                MyLog.getInstance().save("猫眼  callReleased.........");
            }

            @Override
            public void callFinish() {
                Log.e(GeTui.VideoLog, "VideoPresenter==>callFinish....");
                Log.e(Tag, "猫眼 callFinish.........");
                MyLog.getInstance().save("猫眼 callFinish.........");
                videoHandler.removeCallbacksAndMessages(null);
                if (mViewRef.get() != null) {
                    mViewRef.get().onCallFinish();
                }
                stopCountUp();
                MemeManager.getInstance().videoActivityDisconnectMeme();
                isConnectedEye=false;
            }

            @Override
            public void Streaming() {
                Log.e(GeTui.VideoLog, "VideoPresenter==>Streaming....");
                Log.e(Tag, "猫眼 Streaming.........");
                MyLog.getInstance().save("猫眼 Streaming.........");
                videoHandler.removeCallbacksAndMessages(null);
                isConnectedEye=true;
                if(mViewRef!=null && mViewRef.get()!=null){
                    mViewRef.get().callSuccess();
                }
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
            if (mViewRef != null && mViewRef.get() != null) {
                mViewRef.get().screenShotSuccessPath(mPicturePath);
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

    /**
     * 删除临时存在的视频
     */
    public void deleteTempVideo() {
        File oldFile = new File(Util.RECORD_VIDEO_PATH);
        if (oldFile.exists()) {
            oldFile.delete();
        }
    }

    /**
     * 录制视频
     *
     * @param isRecord 开始还是结束录制
     * @param deviceId
     */
    public void recordVideo(boolean isRecord, String deviceId) {
        recordDeviceId = deviceId;
        if (isRecord) {
            if (!isRecoding) {
                try {
                    //开始录制之前需要删除临时视频  可能会崩溃
                    deleteTempVideo();
                    if (mViewRef.get() != null) {
                        mViewRef.get().recordTooStart();
                    }
                    LinphoneManager.getLc().getCurrentCall().startRecording();
                    isRecoding = true;
                    startRecordTime = System.currentTimeMillis();
                } catch (Exception e) {
                    LogUtils.d("开启录屏失败 " + e);
                    if (mViewRef.get() != null) {
                        mViewRef.get().recordExceptionTooShort();
                    }
                }
            }else {
            }
        } else {
            if (System.currentTimeMillis() - startRecordTime > 5 * 1000) {
                if(mViewRef.get()!=null){
                    mViewRef.get().recordTooEnd();
                }
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

    public void hangup() {
        if (isRecoding) { //挂断时，如果在录制视频，那么先保存视频
            stopRecordVideo(recordDeviceId);
        }
        LinphoneHelper.hangUp();
    }

    public void callCatEye(CateEyeInfo cateEyeInfo) {
        //判断网关是否在线
        //判断猫眼是否在线
        if ("offline".equals(cateEyeInfo.getServerInfo().getEvent_str())) {
            //猫眼离线状态
            if (mViewRef.get() != null) {
                mViewRef.get().onCatEyeOffline();
            }
            return;
        }
        //meme网状态
        isCalling = true;
        List<GatewayInfo> allGateway = MyApplication.getInstance().getAllGateway();
        GatewayInfo gatewayInfo = null;
        for (GatewayInfo info : allGateway) {
            if (cateEyeInfo.getGwID().equals(info.getServerInfo().getDeviceSN())) {
                gatewayInfo = info;
                break;
            }
        }
        if (gatewayInfo != null) {
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
    }


    /**
     * 登录米米网
     *
     * @param meUsername
     * @param mePwd
     * @param isCallIn   是不是猫眼呼叫过来的
     */
    private void loginMeme(String meUsername, String mePwd, CateEyeInfo cateEyeInfo, boolean isCallIn) {
        MyLog.getInstance().save("meUsername:"+meUsername+" mePwd:"+mePwd+" cateEyeInfo:"+cateEyeInfo+" isCallIn:"+isCallIn);
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
                        LogUtils.e(Tag, "米米网登陆成功  且网关在线");
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
                        if (!isCallIn) {
                            if (aBoolean) { // 米米网登陆成功且网关在线
                                LogUtils.e(Tag, "米米网  登陆成功   呼叫猫眼");
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
                                LogUtils.e(Tag, "米米网  登陆成功   呼叫猫眼");
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
                        LogUtils.e(Tag, "登录米米网失败或者设备不在线");
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
                .timeout(5 * 1000, TimeUnit.MILLISECONDS)
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<List<Device>>() {
                    @Override
                    public void accept(List<Device> devices) throws Exception {
                        toDisposable(deviceChangeDisposable);
                        if (devices.size() > 1) {
                            if (!isCallIn) {
                                LogUtils.e(Tag, "米米网  登陆成功   呼叫猫眼");
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
        LogUtils.e(Tag, "唤醒猫眼   ");
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
                .timeout(60 * 1000, TimeUnit.MILLISECONDS)
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<MqttData>() {
                    @Override
                    public void accept(MqttData mqttData) throws Exception {
                        toDisposable(wakeupDisposable);
                        Log.e(Tag, "唤醒猫眼:" + mqttData.toString());
                    //    Toast.makeText(mContext,mqttData.toString(),Toast.LENGTH_LONG).show();
                        if ("200".equals(mqttData.getReturnCode())) {
                            LogUtils.e(Tag, "唤醒猫眼成功");
                            if (mViewRef != null && mViewRef.get() != null) {
                                mViewRef.get().wakeupSuccess();
                            }
                        } else {
                            //407  猫眼离线  猫眼唤醒失败
                            LogUtils.e(Tag, "唤醒猫眼失败   " + mqttData.getReturnCode() + "   耗时  " + (System.currentTimeMillis() - start));
                            if (mViewRef != null && mViewRef.get() != null && !isConnected) {
                                //  mViewRef.get().wakeupFailed();
                                mViewRef.get().wakeupFailedStateCode(mqttData.getReturnCode());
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
        compositeDisposable.add(wakeupDisposable);
    }

    private Runnable timeoutRunnable = new Runnable() {
        @Override
        public void run() {
            wakeupSuccess = false;
            currentCateEyeInfo = null;
            if (mViewRef.get() != null) {
                mViewRef.get().waitCallTimeout();
            }

            MemeManager.getInstance().videoActivityDisconnectMeme();
        }
    };

    public void waitCall() {
        handler.postDelayed(timeoutRunnable, 35 * 1000);
    }

    @Override
    public void detachView() {
        handler.removeCallbacks(timeoutRunnable);
        countUpTimer.stop();
        LinphoneHelper.addAutoAcceptCallBack(null);
        super.detachView();

    }


    private SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
    private CountUpTimer countUpTimer = new CountUpTimer(1000) {
        @Override
        public void onTick(long millis) {
            if (mViewRef != null && mViewRef.get() != null) {
                formatter.setTimeZone(TimeZone.getTimeZone("GMT+0"));
                String time = formatter.format(new Date(millis));
                mViewRef.get().callTimes(time);
            }
        }
    };


    /**
     * 开始计时
     */
    private void startCountUp() {
        countUpTimer.start();
    }

    /**
     * 停止计时
     */
    private void stopCountUp() {
        if (countUpTimer != null) {
            LogUtils.e(Tag, "停止计时");
            countUpTimer.stop();
        }

    }

    public void openLock(GwLockInfo gwLockInfo) {
        String deviceId = gwLockInfo.getServerInfo().getDeviceId();
        String lockPwd = (String) SPUtils.get(KeyConstants.SAVA_LOCK_PWD + deviceId, "");
        if (TextUtils.isEmpty(lockPwd)) { //密码为空
            if (mViewRef.get() != null) {
                mViewRef.get().inputPwd(gwLockInfo);
            }
        } else {
            realOpenLock(gwLockInfo.getGwID(), deviceId, lockPwd);
        }
    }

    //开锁
    public void realOpenLock(String gatewayId, String deviceId, String pwd) {
        toDisposable(openLockDisposable);
        if (mViewRef.get() != null) {
            mViewRef.get().startOpenLock(deviceId);
        }
        listenerLockOpen(deviceId);
        listenerLockClose(deviceId);
        if (mqttService != null) {
            openLockDisposable = mqttService.mqttPublish(MqttConstant.getCallTopic(MyApplication.getInstance().getUid()), MqttCommandFactory.openLock(gatewayId, deviceId, "unlock", "pin", pwd))
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if (MqttConstant.OPEN_LOCK.equals(mqttData.getFunc())) {
                                return true;
                            }
                            return false;
                        }
                    })
                    .timeout(30 * 1000, TimeUnit.MILLISECONDS)
                    .compose(RxjavaHelper.observeOnMainThread())
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            toDisposable(openLockDisposable);
                            OpenLockBean openLockBean = new Gson().fromJson(mqttData.getPayload(), OpenLockBean.class);
                            if ("200".equals(openLockBean.getReturnCode())) {
                                if (deviceId.equals(openLockBean.getDeviceId())){
                                    SPUtils.put(KeyConstants.SAVA_LOCK_PWD + deviceId, pwd);
                                }
                            } else if (deviceId.equals(openLockBean.getDeviceId())){
                                if (mViewRef.get() != null) {
                                    mViewRef.get().openLockFailed(deviceId);
                                }
                                SPUtils.remove(KeyConstants.SAVA_LOCK_PWD + deviceId);
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            //开锁异常
                            if (mViewRef.get() != null) {
                                SPUtils.remove(KeyConstants.SAVA_LOCK_PWD + deviceId);
                                mViewRef.get().openLockThrowable(throwable,deviceId);
                            }
                        }
                    });
            compositeDisposable.add(openLockDisposable);
        }
    }


    private void listenerLockOpen(String deviceId) {
        if (mqttService != null) {
            toDisposable(closeLockNotifyDisposable);
            //表示锁已开
            closeLockNotifyDisposable = mqttService.listenerDataBack()
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if (mqttData.getFunc().equals(MqttConstant.GW_EVENT)) {
                                OpenLockNotifyBean openLockNotifyBean = new Gson().fromJson(mqttData.getPayload(), OpenLockNotifyBean.class);
                                int deviceCode = openLockNotifyBean.getEventparams().getDevecode();
                                if ("kdszblock".equals(openLockNotifyBean.getDevtype())
                                        && deviceId.equals(openLockNotifyBean.getDeviceId())) {
                                    if (deviceCode == 2) {
                                        //表示锁已开
                                        return true;
                                    }
                                }
                            }
                            return false;
                        }
                    })
                    .timeout(30 * 1000, TimeUnit.MILLISECONDS)
                    .compose(RxjavaHelper.observeOnMainThread())
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            toDisposable(closeLockNotifyDisposable);
                            LogUtils.e(Tag, "门锁打开上报");
                            if (mViewRef.get() != null) {
                                mViewRef.get().openLockSuccess(deviceId);
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (mViewRef.get() != null) {
                                SPUtils.remove(KeyConstants.SAVA_LOCK_PWD + deviceId);
                                mViewRef.get().openLockThrowable(throwable,deviceId);
                            }
                        }
                    });
            compositeDisposable.add(closeLockNotifyDisposable);
        }


    }

    private void listenerLockClose(String deviceId) {
        if (mqttService != null) {
            toDisposable(lockCloseDisposable);
            //表示锁已开
            //表示锁已经关闭
            lockCloseDisposable = mqttService.listenerDataBack()
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if (mqttData.getFunc().equals(MqttConstant.GW_EVENT)) {
                                OpenLockNotifyBean openLockNotifyBean = new Gson().fromJson(mqttData.getPayload(), OpenLockNotifyBean.class);
                                int deviceCode = openLockNotifyBean.getEventparams().getDevecode();
                                if ("kdszblock".equals(openLockNotifyBean.getDevtype())
                                        && deviceId.equals(openLockNotifyBean.getDeviceId())) {
                                    if (deviceCode == 10 || deviceCode == 1) {
                                        //表示锁已经关闭
                                        return true;
                                    }
                                }
                            }
                            return false;
                        }
                    })
                    .timeout(30 * 1000, TimeUnit.MILLISECONDS)
                    .compose(RxjavaHelper.observeOnMainThread())
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            toDisposable(lockCloseDisposable);
                            LogUtils.e(Tag, "门锁关闭 上报");
                            //关门
                            if (mViewRef.get() != null) {
                                mViewRef.get().lockCloseSuccess(deviceId);
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (mViewRef.get() != null) {
                                mViewRef.get().lockCloseFailed(deviceId);
                            }
                        }
                    });
            compositeDisposable.add(lockCloseDisposable);
        }
    }


    /**
     * 监听设备上线下线
     */
    public void listenerDeviceOnline() {
        if (mqttService != null) {
            toDisposable(listenerDeviceOnLineDisposable);
            listenerDeviceOnLineDisposable = mqttService.listenerDataBack()
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            return mqttData.getFunc().equals(MqttConstant.GW_EVENT);
                        }
                    })
                    .compose(RxjavaHelper.observeOnMainThread())
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            DeviceOnLineBean deviceOnLineBean = new Gson().fromJson(mqttData.getPayload(), DeviceOnLineBean.class);
                            if (deviceOnLineBean != null) {
                                LogUtils.e("设备上下线    " + deviceOnLineBean.toString());
                                if (mViewRef.get() != null && deviceOnLineBean.getEventparams().getEvent_str() != null) {
                                    mViewRef.get().deviceStatusChange(deviceOnLineBean);
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                        }
                    });
            compositeDisposable.add(listenerDeviceOnLineDisposable);
        }
    }


    //网络变化通知
    public void listenerNetworkChange() {
        LogUtils.e("监听网络变化");
        toDisposable(networkChangeDisposable);
        networkChangeDisposable = NetWorkChangReceiver.notifyNetworkChange().subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                LogUtils.e("监听网络变化");
                if (mViewRef != null && mViewRef.get() != null) {
                    mViewRef.get().netWorkChange(aBoolean);
                }
            }
        });
        compositeDisposable.add(networkChangeDisposable);
    }

    //监听开锁上报
    public void listenGaEvent() {
        toDisposable(openLockEventDisposable);
        if (mqttService != null) {
            openLockEventDisposable = mqttService.listenerDataBack()
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if (MqttConstant.EVENT.equals(mqttData.getMsgtype())
                                    && MqttConstant.GW_EVENT.equals(mqttData.getFunc())) {
                                return true;
                            }
                            return false;
                        }
                    })
                    .compose(RxjavaHelper.observeOnMainThread())
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            JSONObject jsonObject = new JSONObject(mqttData.getPayload());
                            String devtype = jsonObject.getString("devtype");
                            String eventtype = jsonObject.getString("eventtype");
                            if (TextUtils.isEmpty(devtype)) { //devtype为空   无法处理数据
                                return;
                            }
                            if (KeyConstants.DEV_TYPE_LOCK.equals(devtype)) {
                                if ("info".equals(eventtype)) {
                                    GatewayLockInfoEventBean gatewayLockInfoEventBean = new Gson().fromJson(mqttData.getPayload(), GatewayLockInfoEventBean.class);
                                    String eventParmDeveType = gatewayLockInfoEventBean.getEventparams().getDevetype();
                                    int devecode = gatewayLockInfoEventBean.getEventparams().getDevecode();
                                    int pin = gatewayLockInfoEventBean.getEventparams().getPin();
                                    String gatewayId = gatewayLockInfoEventBean.getGwId();
                                    String deviceId = gatewayLockInfoEventBean.getDeviceId();
                                    if (eventParmDeveType.equals("lockop") && devecode == 2 && pin == 255) {
                                        if (mViewRef != null && mViewRef.get() != null) {
                                            mViewRef.get().getLockEvent(gatewayId, deviceId);
                                        }
                                    }
                                }

                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            LogUtils.e("报警消息失败   " + throwable.getMessage());
                        }
                    });
            compositeDisposable.add(openLockEventDisposable);
        }
    }


    public void lockClose() {
        if (mqttService != null) {
            toDisposable(closeDisposable);
            //表示锁已开
            //表示锁已经关闭
            closeDisposable = mqttService.listenerDataBack()
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if (mqttData.getFunc().equals(MqttConstant.GW_EVENT)) {
                                OpenLockNotifyBean openLockNotifyBean = new Gson().fromJson(mqttData.getPayload(), OpenLockNotifyBean.class);
                                int deviceCode = openLockNotifyBean.getEventparams().getDevecode();
                                if ("kdszblock".equals(openLockNotifyBean.getDevtype())) {
                                    if (deviceCode == 10 || deviceCode == 1) {
                                        //表示锁已经关闭
                                        LogUtils.e("");
                                        return true;
                                    }
                                }
                            }
                            return false;
                        }
                    })
                    .compose(RxjavaHelper.observeOnMainThread())
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            //关门
                            OpenLockNotifyBean openLockNotifyBean = new Gson().fromJson(mqttData.getPayload(), OpenLockNotifyBean.class);
                            String deviceId= openLockNotifyBean.getDeviceId();
                            String gatewayId=openLockNotifyBean.getGwId();
                            if (mViewRef!=null&&mViewRef.get() != null) {
                                mViewRef.get().closeLockSuccess(deviceId,gatewayId);
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (mViewRef!=null&&mViewRef.get() != null) {
                                mViewRef.get().closeLockThrowable();
                            }
                        }
                    });
            compositeDisposable.add(closeDisposable);
        }

    }


    public void  destoryPre(){
        videoHandler.removeCallbacksAndMessages(null);
    }


}
