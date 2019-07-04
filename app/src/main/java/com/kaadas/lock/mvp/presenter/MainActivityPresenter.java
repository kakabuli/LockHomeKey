package com.kaadas.lock.mvp.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.MainActivity;
import com.kaadas.lock.activity.cateye.VideoVActivity;
import com.kaadas.lock.bean.HomeShowBean;
import com.kaadas.lock.mvp.mvpbase.BlePresenter;
import com.kaadas.lock.mvp.view.IMainActivityView;
import com.kaadas.lock.publiclibrary.bean.BleLockInfo;
import com.kaadas.lock.publiclibrary.bean.CateEyeInfo;
import com.kaadas.lock.publiclibrary.bean.GatewayInfo;
import com.kaadas.lock.publiclibrary.bean.GwLockInfo;
import com.kaadas.lock.publiclibrary.bean.ServerGatewayInfo;
import com.kaadas.lock.publiclibrary.bean.ServerGwDevice;
import com.kaadas.lock.publiclibrary.ble.BleUtil;
import com.kaadas.lock.publiclibrary.ble.bean.NewVersionBean;
import com.kaadas.lock.publiclibrary.ble.responsebean.BleDataBean;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.ServerBleDevice;
import com.kaadas.lock.publiclibrary.http.result.ServerDevice;
import com.kaadas.lock.publiclibrary.http.util.BaseObserver;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.linphone.MemeManager;
import com.kaadas.lock.publiclibrary.linphone.linphone.callback.PhoneCallback;
import com.kaadas.lock.publiclibrary.linphone.linphone.callback.RegistrationCallback;
import com.kaadas.lock.publiclibrary.linphone.linphone.util.LinphoneHelper;
import com.kaadas.lock.publiclibrary.mqtt.MqttCommandFactory;
import com.kaadas.lock.publiclibrary.mqtt.eventbean.CatEyeEventBean;
import com.kaadas.lock.publiclibrary.mqtt.eventbean.DeleteDeviceLockBean;
import com.kaadas.lock.publiclibrary.mqtt.eventbean.GatewayLockAlarmEventBean;
import com.kaadas.lock.publiclibrary.mqtt.eventbean.GatewayLockInfoEventBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.GatewayComfirmOtaResultBean;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.GatewayOtaNotifyBean;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.GetBindGatewayStatusResult;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.RecordTools;
import com.kaadas.lock.utils.Rsa;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.SPUtils2;
import com.kaadas.lock.utils.ftp.GeTui;
import com.kaadas.lock.utils.greenDao.bean.BleLockServiceInfo;
import com.kaadas.lock.utils.greenDao.bean.CatEyeEvent;
import com.kaadas.lock.utils.greenDao.bean.CatEyeServiceInfo;
import com.kaadas.lock.utils.greenDao.bean.GatewayLockAlarmEventDao;
import com.kaadas.lock.utils.greenDao.bean.GatewayLockPwd;
import com.kaadas.lock.utils.greenDao.bean.GatewayLockServiceInfo;
import com.kaadas.lock.utils.greenDao.bean.GatewayServiceInfo;
import com.kaadas.lock.utils.greenDao.db.BleLockServiceInfoDao;
import com.kaadas.lock.utils.greenDao.db.CatEyeServiceInfoDao;
import com.kaadas.lock.utils.greenDao.db.DaoSession;
import com.kaadas.lock.utils.greenDao.db.GatewayLockAlarmEventDaoDao;
import com.kaadas.lock.utils.greenDao.db.GatewayLockPwdDao;
import com.kaadas.lock.utils.greenDao.db.GatewayLockServiceInfoDao;
import com.kaadas.lock.utils.greenDao.db.GatewayServiceInfoDao;

import net.sdvn.cmapi.Device;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;
import org.linphone.core.LinphoneCall;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import retrofit2.http.GET;

/**
 * Create By lxj  on 2019/3/18
 * Describe
 */
public class MainActivityPresenter<T> extends BlePresenter<IMainActivityView> {

    private Disposable warringDisposable;
    private Disposable deviceInBootDisposable;
    private Disposable disposable;
    private Disposable memeDisposable;
    private Disposable deviceChangeDisposable;
    private CateEyeInfo callInCatEyeInfo;  //呼叫进来的猫眼信息
    private Disposable catEyeEventDisposable;
    private Disposable listerBleVersionDisposable;
    private Disposable listenerGatewayOtaDisposable;
    private Disposable comfirmGatewayOtaDisposable;

    private Context mContext;

    public MainActivityPresenter() {
    }

    public MainActivityPresenter(Context mContext) {
        this.mContext = mContext;
    }


    @Override
    public void authSuccess() {

    }

    @Override
    public void attachView(IMainActivityView view) {
        super.attachView(view);
        //网关上线监听
        getPublishNotify();
        setHomeShowBean();
        //监听猫眼锁的报警信息
        listenCatEyeEvent();
        listerBleVersion();
        //监听网关ota升级
        listenGatewayOTA();
    }

    /**
     * 监听网关ota升级通知
     */
    private void listenGatewayOTA() {
        if (mqttService!=null){
            toDisposable(listenerGatewayOtaDisposable);
            listenerGatewayOtaDisposable=mqttService.listenerDataBack()
                    .compose(RxjavaHelper.observeOnMainThread())
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            if (mqttData != null) {
                                if (MqttConstant.NOTIFY_GATEWAY_OTA.equals(mqttData.getFunc())) {
                                    //接收到网关ota升级
                                    GatewayOtaNotifyBean gatewayOtaNotifyBean = new Gson().fromJson(mqttData.getPayload(), GatewayOtaNotifyBean.class);
                                    if (mViewRef.get()!=null){
                                        mViewRef.get().gatewayNotifyOtaSuccess(gatewayOtaNotifyBean);
                                    }

                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {

                        }
                    });
            compositeDisposable.add(listenerGatewayOtaDisposable);
        }
    }


    private void listerBleVersion() {
        //监听是否有新版本
        if (bleService!=null){
            toDisposable(listerBleVersionDisposable);
            listerBleVersionDisposable = bleService.listenBleVersionUpdate()
                    .subscribe(new Consumer<NewVersionBean>() {
                        @Override
                        public void accept(NewVersionBean newVersionBean) throws Exception {
                            updateBleVersion(newVersionBean.getDevname(), newVersionBean.getBleVersion());

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {


                        }
                    });
            compositeDisposable.add(listerBleVersionDisposable);

            //设置警报提醒
            toDisposable(warringDisposable);
            warringDisposable = bleService.listeneDataChange()
                    .filter(new Predicate<BleDataBean>() {
                        @Override
                        public boolean test(BleDataBean bleDataBean) throws Exception {
                            //最新的蓝牙模块才有报警提示
                            return bleDataBean.getCmd() == 0x07 && MyApplication.getInstance().getBleService().getBleVersion() == 3;
                        }
                    })
                    .compose(RxjavaHelper.observeOnMainThread())
                    .subscribe(new Consumer<BleDataBean>() {
                        @Override
                        public void accept(BleDataBean bleDataBean) throws Exception {
                            if (!MyApplication.getInstance().getBleService().getBleLockInfo().isAuth() || MyApplication.getInstance().getBleService().getBleLockInfo().getAuthKey() == null || MyApplication.getInstance().getBleService().getBleLockInfo().getAuthKey().length == 0) {
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
                                if (mViewRef.get() != null && !TextUtils.isEmpty(warringContent)) {
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

            //监听是否是boot模式
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
    }


    public void updateBleVersion(String deviceNam, String version) {
        XiaokaiNewServiceImp.updateBleVersion(deviceNam, MyApplication.getInstance().getUid(), version)
                .subscribe(new BaseObserver<BaseResult>() {
                    @Override
                    public void onSuccess(BaseResult baseResult) {
                        LogUtils.e("更新版本号成功   " + baseResult.toString());
                    }

                    @Override
                    public void onAckErrorCode(BaseResult baseResult) {
                        LogUtils.e("更新版本号失败   " + baseResult.toString());
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        LogUtils.e("更新版本号失败   " + throwable.getMessage());
                    }

                    @Override
                    public void onSubscribe1(Disposable d) {
                        compositeDisposable.add(d);
                    }
                });
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
        toDisposable(disposable);
        if (mqttService != null) {
            disposable = mqttService.listenerNotifyData()
                    .compose(RxjavaHelper.observeOnMainThread())
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            if (mqttData != null) {
                                if (MqttConstant.GATEWAY_STATE.equals(mqttData.getFunc())) {
                                    GetBindGatewayStatusResult gatewayStatusResult = new Gson().fromJson(mqttData.getPayload(), GetBindGatewayStatusResult.class);
                                    LogUtils.e("监听网关的状态" + gatewayStatusResult.getDevuuid());
                                    if (gatewayStatusResult != null) {
                                        List<HomeShowBean> homeShowBeans = MyApplication.getInstance().getAllDevices();
                                        SPUtils.put(gatewayStatusResult.getDevuuid(), gatewayStatusResult.getData().getState());
                                    }
                                }
                            }
                        }
                    });
            compositeDisposable.add(disposable);
        }
    }

    //猫眼信息上报
    public void listenCatEyeEvent() {
        toDisposable(catEyeEventDisposable);
        if (mqttService!=null){
        catEyeEventDisposable = mqttService.listenerDataBack()
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
                        // TODO: 2019/5/14 处理猫眼动态和锁上报的信息分别放在不同的表中
                        /**
                         * 猫眼信息上报
                         */
                        if (devtype.equals(KeyConstants.DEV_TYPE_CAT_EYE)) {
                            CatEyeEventBean catEyeEventBean = new Gson().fromJson(mqttData.getPayload(), CatEyeEventBean.class);
                            int eventType = -1;
                            CatEyeEvent catEyeEvent = new CatEyeEvent();
                            catEyeEvent.setDeviceId(catEyeEventBean.getDeviceId());
                            catEyeEvent.setDeviceType(CatEyeEvent.DEVICE_CAT_EYE);
                            catEyeEvent.setEventTime(Long.parseLong(catEyeEventBean.getTimestamp()));
                            if ("pir".equals(catEyeEventBean.getEventparams().getDevetype())) {
                                eventType = CatEyeEvent.EVENT_PIR;
                            } else if ("headLost".equals(catEyeEventBean.getEventparams().getDevetype())) {
                                eventType = CatEyeEvent.EVENT_HEAD_LOST;
//                        }else if ("doorBell".equals(catEyeEventBean.getEventparams().getDevetype())){
//                            eventType = CatEyeEvent.EVENT_DOOR_BELL;
                            } else if ("lowPower".equals(catEyeEventBean.getEventparams().getDevetype())) {
                                eventType = CatEyeEvent.EVENT_LOW_POWER;
                            } else if ("hostLost".equals(catEyeEventBean.getEventparams().getDevetype())) {
                                eventType = CatEyeEvent.EVENT_HOST_LOST;
                            }
                            if (eventType == -1) {
                                return;
                            }
                            catEyeEvent.setEventType(eventType);
                            catEyeEvent.setGatewayId(catEyeEventBean.getGwId());
                            //   保存到数据库
                            MyApplication.getInstance().getDaoWriteSession().getCatEyeEventDao().insert(catEyeEvent);
                            if (mViewRef.get() != null) {
                                mViewRef.get().onGwEvent(eventType, catEyeEventBean.getDeviceId(), catEyeEventBean.getGwId());
                            }
                            //网关锁信息上报
                        } else if (KeyConstants.DEV_TYPE_LOCK.equals(devtype)) {
                            //保存告警信息
                            if ("alarm".equals(eventtype)) {
                                GatewayLockAlarmEventBean gatewayLockAlarmEventBean = new Gson().fromJson(mqttData.getPayload(), GatewayLockAlarmEventBean.class);
                                if (gatewayLockAlarmEventBean.getEventparams() != null && gatewayLockAlarmEventBean.getEventparams().getAlarmCode() != 0 && gatewayLockAlarmEventBean.getEventparams().getClusterID() != 0) {
                                    //保存到数据库
                                    int alarmCode = gatewayLockAlarmEventBean.getEventparams().getAlarmCode();
                                    String deviceId = gatewayLockAlarmEventBean.getDeviceId();
                                    String gatewayId = gatewayLockAlarmEventBean.getGwId();
                                    int clusterID = gatewayLockAlarmEventBean.getEventparams().getClusterID();
                                    GatewayLockAlarmEventDao gatewayLockAlarmEventDao = new GatewayLockAlarmEventDao();
                                    gatewayLockAlarmEventDao.setDeviceId(deviceId); //设备id
                                    gatewayLockAlarmEventDao.setGatewayId(gatewayLockAlarmEventBean.getGwId()); //网关id
                                    gatewayLockAlarmEventDao.setTimeStamp(gatewayLockAlarmEventBean.getTimestamp()); //时间戳
                                    gatewayLockAlarmEventDao.setDevtype(gatewayLockAlarmEventBean.getDevtype()); //设备类型
                                    gatewayLockAlarmEventDao.setAlarmCode(alarmCode); //报警代码
                                    if (alarmCode == 1 && clusterID == 257) {
                                        //锁重置
                                        MyApplication.getInstance().getAllDevicesByMqtt(true);
                                        //删除锁的全部密码
                                        deleteAllPwd(gatewayId, deviceId, MyApplication.getInstance().getUid());

                                    }
                                    gatewayLockAlarmEventDao.setClusterID(clusterID); //257 代表锁的信息;1 代表电量信息
                                    gatewayLockAlarmEventDao.setEventcode(gatewayLockAlarmEventBean.getEventcode());
                                    //插入到数据库
                                    if (!checkSame(gatewayLockAlarmEventBean.getTimestamp(), gatewayLockAlarmEventBean.getEventparams().getAlarmCode())) {
                                        MyApplication.getInstance().getDaoWriteSession().getGatewayLockAlarmEventDaoDao().insert(gatewayLockAlarmEventDao);
                                        if (mViewRef.get() != null) {
                                            mViewRef.get().onGwLockEvent(alarmCode, clusterID, deviceId, gatewayId);
                                        }
                                    }

                                } else {
                                    DeleteDeviceLockBean deleteGatewayLockDeviceBean = new Gson().fromJson(mqttData.getPayload(), DeleteDeviceLockBean.class);
                                    if (deleteGatewayLockDeviceBean != null) {
                                        if ("zigbee".equals(deleteGatewayLockDeviceBean.getEventparams().getDevice_type()) && deleteGatewayLockDeviceBean.getEventparams().getEvent_str().equals("delete")) {
                                            refreshData(deleteGatewayLockDeviceBean.getGwId(), deleteGatewayLockDeviceBean.getDeviceId());

                                        }
                                    }
                                }
                            } else if ("info".equals(eventtype)) {
                                GatewayLockInfoEventBean gatewayLockInfoEventBean = new Gson().fromJson(mqttData.getPayload(), GatewayLockInfoEventBean.class);
                                String gatewayId = gatewayLockInfoEventBean.getGwId();
                                String deviceId = gatewayLockInfoEventBean.getDeviceId();
                                String uid = MyApplication.getInstance().getUid();
                                String eventParmDeveType = gatewayLockInfoEventBean.getEventparams().getDevetype();
                                int num = gatewayLockInfoEventBean.getEventparams().getUserID();
                                int devecode = gatewayLockInfoEventBean.getEventparams().getDevecode();
                                int pin = gatewayLockInfoEventBean.getEventparams().getPin();
                                if (eventParmDeveType.equals("lockprom") && devecode == 2 && pin == 255) {
                                    //添加单个密码
                                    //删除密码
                                    deleteOnePwd(gatewayId, deviceId, uid, "0" + num);
                                    //添加
                                    AddOnePwd(gatewayId, deviceId, uid, "0" + num);
                                    LogUtils.e("单个添加");
                                } else if (eventParmDeveType.equals("lockprom") && devecode == 3 && num == 255 && pin == 255) {
                                    //全部删除
                                    deleteAllPwd(gatewayId, deviceId, uid);
                                    LogUtils.e("全部删除");
                                } else if (eventParmDeveType.equals("lockprom") && devecode == 3 && pin == 255) {
                                    //删除单个密码
                                    deleteOnePwd(gatewayId, deviceId, uid, "0" + num);
                                    LogUtils.e("删除单个密码");
                                } else if (eventParmDeveType.equals("lockop") && devecode == 2 && pin == 255) {
                                    //使用一次性开锁密码
                                    if (num > 4 && num <= 8) {
                                        deleteOnePwd(gatewayId, deviceId, uid, "0" + num);
                                        LogUtils.e("使用一次性开锁");
                                    }
                                    LogUtils.e("开锁上报");
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
        compositeDisposable.add(catEyeEventDisposable);
    }
    }

    private void refreshData(String gatewayId, String deviceId) {
        GatewayInfo gatewayInfo = MyApplication.getInstance().getGatewayById(gatewayId);
        if (gatewayInfo != null) {
            MyApplication.getInstance().getAllDevicesByMqtt(true);
        }
    }

    static  boolean isFront =false;
    public void isFontShow(){
        isFront=true;
    }
    public void  noIsFont(){
        isFront=false;
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
                    Log.e(GeTui.VideoLog, "  Linphone  收到来电:"+isFront);
//                    if(VideoVActivity.isRunning && isFront){
//                          Toast.makeText(mContext,mContext.getString(R.string.video_desotry),Toast.LENGTH_LONG).show();
//                    }
                    if (VideoVActivity.isRunning) {
                        LogUtils.e("Linphone  收到来电   VideoActivity已经运行  不出来  ");
                        return;
                    }

                    if(TextUtils.isEmpty(MyApplication.getInstance().getSip_package_invite())){  //app启动呼叫过来
                        Log.e(GeTui.VideoLog,"麦克风状态:"+!RecordTools.validateMicAvailability());
                        if (!RecordTools.validateMicAvailability()) {  //打开false,没有打开true
                            if (mViewRef != null && mViewRef.get() != null) {
                                mViewRef.get().callError();
                            }
                            return;
                        }
                    }
                    //设置呼叫进来的时间
                    MyApplication.getInstance().setIsComingTime(System.currentTimeMillis());
                    //获取linphone的地址
                    String linphoneSn = linphoneCall.getRemoteAddress().getUserName();
                    LogUtils.e("Linphone   呼叫过来的linphoneSn   " + linphoneSn);
                    Log.e(GeTui.VideoLog, "  linphoneSn:" + linphoneSn);
                    String gwId = "";
                    GatewayInfo gatewayInfo = null;
                    Log.e(GeTui.VideoLog,"获取网关列表前");
                    List<CateEyeInfo> cateEyes = MyApplication.getInstance().getAllBindDevices().getCateEyes();
                    Log.e(GeTui.VideoLog,"cateEyes的大小:"+cateEyes.size());
                    for (CateEyeInfo cateEyeInfo : cateEyes) {
                        LogUtils.e("猫眼的  getDeviceId  " + cateEyeInfo.getServerInfo().getDeviceId());
                        if (linphoneSn.equalsIgnoreCase(cateEyeInfo.getServerInfo().getDeviceId())) {
                            LogUtils.e("获取到网关Id为  " + cateEyeInfo.getGwID());
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
                        Log.e(GeTui.VideoLog,"gwid为null");
                        return;
                    }
                    //获取米米网账号情况
                    int meBindState = gatewayInfo.getServerInfo().getMeBindState();
                    String mePwd = gatewayInfo.getServerInfo().getMePwd();
                    String meUsername = gatewayInfo.getServerInfo().getMeUsername();
                    if (TextUtils.isEmpty(meUsername) || TextUtils.isEmpty(mePwd)) {
                        //如果账号或者密码有一个为空  直接退出
                        Log.e(GeTui.VideoLog,"咪咪网账号为null");
                        return;
                    }
                    Log.e(GeTui.VideoLog, "MainActivityPresenter--> next..." + meUsername + " " + mePwd);
                    if (MemeManager.getInstance().isConnected()) { //meme网已经连接
                        //如果meme网登陆的账号密码为当前的账号密码，直接发起通信
                        if (meUsername.equals(MemeManager.getInstance().getCurrentAccount())
                                && mePwd.equals(MemeManager.getInstance().getCurrentPassword())) {
                            //查看是否有设备在线   如果有  弹出来电框
                            if (MemeManager.getInstance().getGwDevices().size() > 0) {
                                if (mViewRef.get() != null) {
                                    mViewRef.get().onCatEyeCallIn(callInCatEyeInfo);
                                }
                            } else {  //本地登录了米米网账号且是当前猫眼的meme网昂好   但是没有本地设备在线  等待五秒
                                listDeviceChange();
                            }
                        } else {  //本地登录的meme网账号不是呼叫进来猫眼的meme网账号   不处理

                        }
                    } else { //meme网没有连接
                        Log.e(GeTui.VideoLog, "MainPresenter------>登录 mimi");
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
     *
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
                        Log.e(GeTui.VideoLog, "米米网登陆成功  且网关在线 aBoolean:"+aBoolean+" devices.size:"+devices);
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
                            Log.e(GeTui.VideoLog, "米米网  登陆成功   弹出来电框");
                            if (mViewRef.get() != null) {
                                mViewRef.get().onCatEyeCallIn(callInCatEyeInfo);
                            }
                        } else { //米米网登陆失败或者网关不在线   不做处理
                               if(mViewRef!=null && mViewRef.get()!=null){
                                   mViewRef.get().onCatEyeCallFail();
                               }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtils.e("登录米米网失败或者设备部在线");
                        Log.e(GeTui.VideoLog,"登录米米网失败或者设备部在线:"+throwable.getMessage());
//                        if(mViewRef!=null && mViewRef.get()!=null){
//                            Log.e(GeTui.VideoLog,"MainAcvitiyPresenter===>失败");
//                            mViewRef.get().onCatEyeCallFail();
//                        }
                         if(mContext instanceof Activity){
                            Activity mainActivity= (Activity)mContext;
                            mainActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mContext,mContext.getString(R.string.video_connection_fail),Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                });
    }


    private void listDeviceChange() {
        toDisposable(deviceChangeDisposable);
        deviceChangeDisposable = MemeManager.getInstance().listDevicesChange()
                .compose(RxjavaHelper.observeOnMainThread())
                .timeout(5 * 1000, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<List<Device>>() {
                    @Override
                    public void accept(List<Device> devices) throws Exception {
                        toDisposable(deviceChangeDisposable);
                        if (devices.size() > 1) {
                            if (mViewRef.get() != null) {
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

    public void uploadpushmethod() {
        String uid = (String) SPUtils.get(SPUtils.UID, "");
        String JpushId = (String) SPUtils2.get(MyApplication.getInstance(), GeTui.JPUSH_ID, "");
        Log.e(GeTui.VideoLog, "uid:" + uid + " jpushid:" + JpushId + " token:" + MyApplication.getInstance().getToken());
        //uploadPushId(String uid, String jpushId, int type)
        if (!TextUtils.isEmpty(uid) && !TextUtils.isEmpty(JpushId)) {
            XiaokaiNewServiceImp.uploadPushId(uid, JpushId, 2).subscribe(new BaseObserver<BaseResult>() {
                @Override
                public void onSuccess(BaseResult baseResult) {
                    if (mViewRef != null) {
                        mViewRef.get().uploadpush(baseResult);
                    }
                }

                @Override
                public void onAckErrorCode(BaseResult baseResult) {
                    Log.e(GeTui.VideoLog, "pushid上传失败,服务返回:" + baseResult);
                }

                @Override
                public void onFailed(Throwable throwable) {
                    Log.e(GeTui.VideoLog, "pushid上传失败");
                }

                @Override
                public void onSubscribe1(Disposable d) {
                    compositeDisposable.add(d);
                }
            });
        }
    }


    @Override
    public void detachView() {
        super.detachView();
        LinphoneHelper.deleteUser();
        bleService.release();
        handler.removeCallbacksAndMessages(null);
    }


    //添加某个
    private void AddOnePwd(String gatewayId, String deviceId, String uid, String num) {
        GatewayLockPwdDao gatewayLockPwdDao = MyApplication.getInstance().getDaoWriteSession().getGatewayLockPwdDao();
        GatewayLockPwd gatewayLockPwd = new GatewayLockPwd();
        gatewayLockPwd.setUid(uid);
        gatewayLockPwd.setNum(num);
        gatewayLockPwd.setStatus(1);
        gatewayLockPwd.setName("");
        gatewayLockPwd.setGatewayId(gatewayId);
        gatewayLockPwd.setDeviceId(deviceId);
        Integer keyInt = Integer.parseInt(num);
        //用于zigbee锁是根据编号识别是永久密码，临时密码，还是胁迫密码
        if (keyInt <= 4) {
            gatewayLockPwd.setTime(1);
        } else if (keyInt <= 8 && keyInt > 4) {
            gatewayLockPwd.setTime(2);
        } else if (keyInt == 9) {
            gatewayLockPwd.setTime(3);
        }
        if (gatewayLockPwdDao != null) {
            gatewayLockPwdDao.insert(gatewayLockPwd);
        }


    }

    //删除某个数据
    private void deleteOnePwd(String gatewayId, String deviceId, String uid, String num) {
        GatewayLockPwdDao gatewayLockPwdDao = MyApplication.getInstance().getDaoWriteSession().getGatewayLockPwdDao();
        GatewayLockPwd gatewayLockPwd = gatewayLockPwdDao.queryBuilder().where(GatewayLockPwdDao.Properties.GatewayId.eq(gatewayId), GatewayLockPwdDao.Properties.DeviceId.eq(deviceId), GatewayLockPwdDao.Properties.Uid.eq(uid), GatewayLockPwdDao.Properties.Num.eq(num)).unique();
        if (gatewayLockPwd != null) {
            gatewayLockPwdDao.delete(gatewayLockPwd);
        }
    }

    //删除该锁的所有密码
    private void deleteAllPwd(String gatewayId, String deviceId, String uid) {
        GatewayLockPwdDao gatewayLockPwdDao = MyApplication.getInstance().getDaoWriteSession().getGatewayLockPwdDao();
        List<GatewayLockPwd> gatewayLockPwdList = gatewayLockPwdDao.queryBuilder().where(GatewayLockPwdDao.Properties.GatewayId.eq(gatewayId), GatewayLockPwdDao.Properties.DeviceId.eq(deviceId), GatewayLockPwdDao.Properties.Uid.eq(uid)).list();
        if (gatewayLockPwdList != null && gatewayLockPwdList.size() > 0) {
            for (GatewayLockPwd gatewayLockPwd : gatewayLockPwdList) {
                gatewayLockPwdDao.delete(gatewayLockPwd);
            }
        }
    }

    //去掉时间戳相同
    public boolean checkSame(String time, int alarmCode) {
        GatewayLockAlarmEventDao gatewayLockAlarmEventDao = MyApplication.getInstance().getDaoWriteSession().queryBuilder(GatewayLockAlarmEventDao.class).where(GatewayLockAlarmEventDaoDao.Properties.TimeStamp.eq(time), GatewayLockAlarmEventDaoDao.Properties.AlarmCode.eq(alarmCode)).unique();
        if (gatewayLockAlarmEventDao != null) {
            return true;
        }
        return false;
    }

    //设置HomeShowBean
    public void setHomeShowBean() {
        DaoSession daoSession = MyApplication.getInstance().getDaoWriteSession();
        String uid = MyApplication.getInstance().getUid();
        List<HomeShowBean> homeShowBeans = new ArrayList<>();
        //获取蓝牙
        List<BleLockServiceInfo> bleLockList = daoSession.getBleLockServiceInfoDao().queryBuilder().where(BleLockServiceInfoDao.Properties.Uid.eq(uid)).list();
        if (bleLockList != null && bleLockList.size() > 0) {
            for (BleLockServiceInfo bleDevice : bleLockList) {
                ServerBleDevice serverBleDevice = new ServerBleDevice();
                serverBleDevice.setAuto_lock(bleDevice.getAuto_lock());
                serverBleDevice.setCenter_latitude(bleDevice.getCenter_latitude());
                serverBleDevice.setCenter_longitude(bleDevice.getCenter_longitude());
                serverBleDevice.setCircle_radius(bleDevice.getCircle_radius());
                serverBleDevice.setLockName(bleDevice.getLockName());
                serverBleDevice.setLockNickName(bleDevice.getLockNickName());
                serverBleDevice.setMacLock(bleDevice.getMacLock()); //设置值
                serverBleDevice.setIs_admin(bleDevice.getIs_admin());
                serverBleDevice.setModel(bleDevice.getModel());
                serverBleDevice.setOpen_purview(bleDevice.getOpen_purview());
                serverBleDevice.setPassword1(bleDevice.getPassword1());
                serverBleDevice.setPassword2(bleDevice.getPassword2());

                serverBleDevice.setBleVersion(bleDevice.getBleVersion());

                serverBleDevice.setModel(bleDevice.getModel());

                serverBleDevice.setDeviceSN(bleDevice.getDeviceSN());
                serverBleDevice.setSoftwareVersion(bleDevice.getSoftwareVersion());
                serverBleDevice.setBleVersion(bleDevice.getBleVersion());
                serverBleDevice.setCreateTime(bleDevice.getCreateTime());

                BleLockInfo bleLockInfo = new BleLockInfo(serverBleDevice);
                homeShowBeans.add(new HomeShowBean(HomeShowBean.TYPE_BLE_LOCK, bleDevice.getLockName(), bleDevice.getLockNickName(), bleLockInfo));
            }
        }
        //获取网关
        List<GatewayServiceInfo> gatewayServiceInfoList = daoSession.getGatewayServiceInfoDao().queryBuilder().where(GatewayServiceInfoDao.Properties.Uid.eq(uid)).list();
        if (gatewayServiceInfoList != null && gatewayServiceInfoList.size() > 0) {
            for (GatewayServiceInfo gatewayServiceInfo : gatewayServiceInfoList) {
                GatewayInfo newGatewayInfo = new GatewayInfo(new ServerGatewayInfo(gatewayServiceInfo.getDeviceSN(), gatewayServiceInfo.getDeviceNickName(), gatewayServiceInfo.getAdminuid(), gatewayServiceInfo.getAdminName(), gatewayServiceInfo.getAdminNickname(), gatewayServiceInfo.getIsAdmin(), gatewayServiceInfo.getMeUsername(), gatewayServiceInfo.getMePwd(), gatewayServiceInfo.getMeBindState()));
                homeShowBeans.add(new HomeShowBean(HomeShowBean.TYPE_GATEWAY, gatewayServiceInfo.getDeviceSN(), gatewayServiceInfo.getDeviceNickName(), newGatewayInfo));
            }
        }

        //获取网关锁
        List<GatewayLockServiceInfo> gatewayLockList = daoSession.getGatewayLockServiceInfoDao().queryBuilder().where(GatewayLockServiceInfoDao.Properties.Uid.eq(uid)).list();
        if (gatewayLockList != null && gatewayLockList.size() > 0) {
            for (GatewayLockServiceInfo gwLock : gatewayLockList) {
                GwLockInfo gwLockInfo = new GwLockInfo(gwLock.getGatewayId(), new ServerGwDevice(gwLock.getSW(), gwLock.getDeviceId(), gwLock.getDevice_type(), gwLock.getEvent_str(), gwLock.getIpaddr(), gwLock.getMacaddr(), gwLock.getNickName(), gwLock.getTime()));
                homeShowBeans.add(new HomeShowBean(HomeShowBean.TYPE_GATEWAY_LOCK, gwLock.getDeviceId(),
                        gwLock.getNickName(), gwLockInfo));
            }
        }

        //获取猫眼
        List<CatEyeServiceInfo> catEyeServiceList = daoSession.getCatEyeServiceInfoDao().queryBuilder().where(CatEyeServiceInfoDao.Properties.Uid.eq(uid)).list();
        if (catEyeServiceList != null && catEyeServiceList.size() > 0) {
            for (CatEyeServiceInfo catEyeService : catEyeServiceList) {
                CateEyeInfo cateEyeInfo = new CateEyeInfo(catEyeService.getGatewayId(), new ServerGwDevice(catEyeService.getSW(), catEyeService.getDeviceId(), catEyeService.getDevice_type(), catEyeService.getEvent_str(), catEyeService.getIpaddr(), catEyeService.getMacaddr(), catEyeService.getNickName(), catEyeService.getTime()));
                homeShowBeans.add(new HomeShowBean(HomeShowBean.TYPE_CAT_EYE, catEyeService.getDeviceId(),
                        catEyeService.getNickName(), cateEyeInfo));
            }
        }
        MyApplication.getInstance().setHomeshowDevice(homeShowBeans);
    }



}


