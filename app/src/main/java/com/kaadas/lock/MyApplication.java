package com.kaadas.lock;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.gson.Gson;
import com.huawei.android.hms.agent.HMSAgent;
import com.igexin.sdk.PushManager;
import com.kaadas.lock.activity.login.LoginActivity;
import com.kaadas.lock.bean.HomeShowBean;
import com.kaadas.lock.publiclibrary.bean.CateEyeInfo;
import com.kaadas.lock.publiclibrary.bean.GatewayInfo;
import com.kaadas.lock.publiclibrary.bean.GwLockInfo;
import com.kaadas.lock.publiclibrary.linphone.MemeManager;
import com.kaadas.lock.publiclibrary.ble.BleService;
import com.kaadas.lock.publiclibrary.http.result.GetPasswordResult;
import com.kaadas.lock.publiclibrary.http.util.RetrofitServiceManager;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.linphone.linphone.util.LinphoneHelper;
import com.kaadas.lock.publiclibrary.linphone.linphonenew.LinphoneService;
import com.kaadas.lock.publiclibrary.mqtt.MqttCommandFactory;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.AllBindDevices;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttService;
import com.kaadas.lock.utils.Constants;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.MyLog;
import com.kaadas.lock.utils.Rom;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.ToastUtil;
import com.kaadas.lock.utils.greenDao.db.DaoManager;
import com.kaadas.lock.utils.greenDao.db.DaoSession;
import com.kaidishi.lock.service.GeTuiIntentService;
import com.kaidishi.lock.service.GeTuiPushService;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
/*import com.uuzuche.lib_zxing.activity.ZXingLibrary;*/


import net.sdvn.cmapi.CMAPI;
import net.sdvn.cmapi.Config;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.reactivex.subjects.PublishSubject;


/**
 * Create By lxj  on 2019/1/7
 * Describe
 */
public class MyApplication extends Application {
    private static MyApplication instance;
    private String token;
    private String uid;
    private List<Activity> activities = new ArrayList<>();
    // APP_ID 替换为你的应用从官方网站申请到的合法appID
    private static final String APP_ID = "wxaa2df1f344ba0755";

    // IWXAPI 是第三方app和微信通信的openApi接口
    protected MqttService mqttService;
    private BleService bleService;
    private Disposable allBindDeviceDisposable;
    private AllBindDevices allBindDevices;
    private String TAG = "凯迪仕";
    private IWXAPI api;
    private List<HomeShowBean> homeShowDevices = new ArrayList<>();
    private int listService = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.e("attachView  App启动 ");
        instance = this;
        CrashReport.initCrashReport(getApplicationContext(), "3ac95f5a71", true);
        initBleService();
        initMqttService();//启动MqttService
        initLinphoneService();
        SPUtils.init(this);  //初始化SPUtils  传递Context进去  不需要每次都传递Context
        ToastUtil.init(this); //初始化ToastUtil 传递Context进去  不需要每次都传递
        SPUtils.remove(Constants.LINPHONE_REGESTER_STATE);
        initTokenAndUid();  //获取本地UUID
        try {
            MyLog.getInstance().init(this);
        } catch (IOException e) {
            Toast.makeText(this,getString(R.string.log_info),Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        listenerAppBackOrForge();
        //扫描二维码初始化
        /* ZXingLibrary.initDisplayOpinion(this);*/
        initMeme();
        regToWx();
        //配置数据库
        setUpWriteDataBase();
        // HuaWei phone
        if(Rom.isEmui()){
            HMSAgent.init(this);
        }
        PushManager.getInstance().initialize(this, userPushService);
        // 注册 intentService 后 PushDemoReceiver 无效, sdk 会使用 DemoIntentService 传递数据,
        // AndroidManifest 对应保留一个即可(如果注册 DemoIntentService, 可以去掉 PushDemoReceiver, 如果注册了
        // IntentService, 必须在 AndroidManifest 中声明)
        PushManager.getInstance().registerPushIntentService(this, GeTuiIntentService.class);
        // 应用未启动, 个推 service已经被唤醒,显示该时间段内离线消息
//        if (DemoApplication.payloadData != null) {
//            tLogView.append(DemoApplication.payloadData);
//        }

     }

    private void regToWx() {
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(this, APP_ID, true);
        // 将应用的appId注册到微信
        api.registerApp(APP_ID);
    }

    public IWXAPI getApi() {
        return api;
    }

    private void initMeme() {
        //设置配置项
        Config config = new Config(true);
        if (BuildConfig.DEBUG) {
            config.setLogLevel(5);
        } else {
            config.setLogLevel(0);
        }
        CMAPI.getInstance().setConfig(config);
        CMAPI.getInstance().init(this, MqttConstant.APP_ID, MqttConstant.PARTERN_ID, MqttConstant.DC_TEST);

        MemeManager.getInstance().init();
    }


    /**
     * 启动蓝牙服务
     */
    private void initBleService() {
        Intent intent = new Intent(this, BleService.class);
        bindService(intent, new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                if (service instanceof BleService.MyBinder) {
                    BleService.MyBinder binder = (BleService.MyBinder) service;
                    bleService = binder.getService();
                    listService++;
                    getServiceConnected.onNext(listService);
                    if (listService == 2) {
                        listService = 0;
                    }
                    LogUtils.e("服务启动成功    " + (bleService == null) + "当前服务中编号是多少 " + listService);
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        }, Context.BIND_AUTO_CREATE);

    }

    public BleService getBleService() {

        return bleService;
    }

    /**
     * 监听程序是前台还是后台
     */
    private int count;

    public void listenerAppBackOrForge() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityStopped(Activity activity) {
                count--;
//                LogUtils.e("程序切换", activity + "onActivityStopped  "+count);
                if (count == 0) {
                    LogUtils.e("程序切换", ">>>>>>>>>>>>>>>>>>>切到后台  lifecycle");
                    listenerAppChange.onNext(true);
                }

            }

            @Override
            public void onActivityStarted(Activity activity) {


            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
//                LogUtils.e("程序切换", activity + "onActivitySaveInstanceState");
            }

            @Override
            public void onActivityResumed(Activity activity) {
//                LogUtils.e("程序切换", activity + "onActivityResumed");
                if (count == 0) {
                    LogUtils.e("程序切换", ">>>>>>>>>>>>>>>>>>>切到前台  lifecycle");
                    //是不是
                    listenerAppChange.onNext(false);
                }
                count++;
//                LogUtils.e("程序切换", activity + "onActivityStarted  " + count);
            }

            @Override
            public void onActivityPaused(Activity activity) {
//                LogUtils.e("程序切换", activity + "onActivityPaused");

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
//                Log.d("程序切换", activity + "onActivityDestroyed");
            }

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
//                LogUtils.e("viclee", activity + "onActivityCreated");
            }
        });

    }

    /**
     * 启动MQTT服务
     */
    private void initMqttService() {
        Intent intent = new Intent(this, MqttService.class);
        bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                if (service instanceof MqttService.MyBinder) {
                    MqttService.MyBinder binder = (MqttService.MyBinder) service;
                    mqttService = binder.getService();
                    listService++;
                    getServiceConnected.onNext(listService);
                    if (listService == 2) {
                        listService = 0;
                    }
                    LogUtils.e("attachView service启动" + (mqttService == null) + "当前服务中编号是多少  " + listService);
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        }, Context.BIND_AUTO_CREATE);
    }


    /**
     * 初始化linphone服务
     */
    private void initLinphoneService() {
        Intent intent = new Intent(this, LinphoneService.class);
        bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {

            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        }, Context.BIND_AUTO_CREATE);
    }

    public MqttService getMqttService() {
        return mqttService;
    }

    public static MyApplication getInstance() {
        return instance;
    }

    public void initTokenAndUid() {
        token = (String) SPUtils.get(SPUtils.TOKEN, "");
        uid = (String) SPUtils.get(SPUtils.UID, "");
        RetrofitServiceManager.updateToken();
    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {  //每次设置token  都更新一下
        this.token = token;
        RetrofitServiceManager.updateToken();
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * @param isShowDialog 是否弹出对话框，提示用户token失效，需要重新登陆。如果是主动退出登录的那么不需要提示 是false
     *                     <p>
     *                     <p>
     *                     Token过期  需要处理的事情
     *                     清除token和UID
     *                     bleService  断开蓝牙连接  清除蓝牙数据
     *                     清除缓存到的本地数据
     *                     关闭所有界面，退出到登陆界面
     *                     ....
     */
    public void tokenInvalid(boolean isShowDialog) {
        LogUtils.e("token过期   ");
        SPUtils.put(KeyConstants.HEAD_PATH, "");
        boolean alreadyStart = false;
        Boolean appUpdate = (Boolean) SPUtils.get(SPUtils.APPUPDATE, false);
        String phone = (String) SPUtils.get(SPUtils.PHONEN, "");
        //退出登录  清除数据
        SPUtils.clear();
        if (appUpdate == true) {
            SPUtils.put(SPUtils.APPUPDATE, true);
        }
        if (!TextUtils.isEmpty(phone)) {
            SPUtils.put(SPUtils.PHONEN, phone);
        }
        //清除内存中缓存的数据
        if (bleService!=null){
            bleService.release();
            bleService.removeBleLockInfo();
        }
        homeShowDevices.clear();
        MyApplication.getInstance().initTokenAndUid();
        //退出linphone
        LinphoneHelper.deleteUser();
        //退出meme网
        MemeManager.getInstance().videoActivityDisconnectMeme();
        //清除数据库数据
        for (Activity activity : activities) {
            if (activity != null) {
                if (!alreadyStart) { //如果还没有启动Activity
                    alreadyStart = true;
                    Intent intent = new Intent(activity, LoginActivity.class);
                    intent.putExtra(KeyConstants.IS_SHOW_DIALOG, isShowDialog);
                    activity.startActivity(intent);
                    /*if (isShowDialog) {  //
                        ToastUtil.getInstance().showShort(R.string.token_invalid_relogin_please);
                    }*/
                }
                activity.finish();
            }
        }


    }


    public void addActivity(Activity activity) {
        if (!activities.contains(activity)) {
            activities.add(activity);
        }
    }

    public void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    static {
        //static 代码段可以防止内存泄露
        // 设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @NonNull
            @Override
            public RefreshHeader createRefreshHeader(@NonNull Context context, @NonNull RefreshLayout layout) {
//                layout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.white);
                //全局设置主题颜色
                return new ClassicsHeader(context).setSpinnerStyle(SpinnerStyle.Translate);
                //指定为经典Header，默认是 贝塞尔雷达Header
            }
        });
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @NonNull
            @Override
            public RefreshFooter createRefreshFooter(@NonNull Context context, @NonNull RefreshLayout layout) {
                //指定为经典Footer，默认是 BallPulseFooter
                return new ClassicsFooter(context).setSpinnerStyle(SpinnerStyle.Translate);
            }
        });
    }

    /**
     * 密码列表的存取
     */
    private Map<String, GetPasswordResult> passwordResults = new HashMap();

    public void setPasswordResults(String deviceName, GetPasswordResult passwordList, boolean isNotify) {
        LogUtils.e("设置数据  密码列表  " + deviceName + (passwordList.getData() == null));
        passwordResults.put(deviceName, passwordList);
        if (isNotify) {
            passwordLoaded.onNext(true);
        }
    }

    public GetPasswordResult getPasswordResults(String deviceName) {
        return passwordResults.get(deviceName);
    }

    /**
     * 密码变动通知获取
     */
    private PublishSubject<Boolean> passwordChange = PublishSubject.create();

    public PublishSubject<Boolean> passwordChangeListener() {
        return passwordChange;
    }

    /**
     * 密码获取成功，通知更新界面
     */
    private PublishSubject<Boolean> passwordLoaded = PublishSubject.create();

    public PublishSubject<Boolean> passwordLoadedListener() {
        return passwordLoaded;
    }

    private PublishSubject<Boolean> listenerAppChange = PublishSubject.create();

    public PublishSubject<Boolean> listenerAppState() {
        return listenerAppChange;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        //程序终止时
        LogUtils.e("程序终止了");
    }

    //清除所有与的Actvity
    public void removeAllActivity() {
        LogUtils.e("清除所有的Activity");
        for (Activity activity : activities) {
            if (activity != null) {
                activity.finish();
            }
        }
    }


    /**
     * 全局的获取所有设备信息
     *
     * @param isForce 是否强制刷新
     */

    public void getAllDevicesByMqtt(boolean isForce) {
        if (!isForce) {
            if (allBindDevices != null) {
                getDevicesFromServer.onNext(allBindDevices);
                return;
            }
        }
        MqttMessage allBindDevice = MqttCommandFactory.getAllBindDevice(getUid());
        if (allBindDeviceDisposable != null && !allBindDeviceDisposable.isDisposed()) {
            allBindDeviceDisposable.dispose();
        }
        allBindDeviceDisposable = mqttService.mqttPublish(MqttConstant.MQTT_REQUEST_APP, allBindDevice)
                .filter(new Predicate<MqttData>() {
                    @Override
                    public boolean test(MqttData mqttData) throws Exception {
                        return mqttData.getFunc().equalsIgnoreCase(MqttConstant.GET_ALL_BIND_DEVICE);
                    }
                })
                .timeout(10 * 1000, TimeUnit.MILLISECONDS)
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<MqttData>() {
                    @Override
                    public void accept(MqttData mqttData) throws Exception {
                        if (allBindDeviceDisposable != null && !allBindDeviceDisposable.isDisposed()) {
                            allBindDeviceDisposable.dispose();
                        }
                        String payload = mqttData.getPayload();
                        allBindDevices = new Gson().fromJson(payload, AllBindDevices.class);
                        SPUtils.put(Constants.ALL_DEVICES_DATA,payload);

                        long serverCurrentTime = Long.parseLong(allBindDevices.getTimestamp());
                        SPUtils.put(KeyConstants.SERVER_CURRENT_TIME, serverCurrentTime);

                        if (allBindDevices != null) {
                            homeShowDevices = allBindDevices.getHomeShow();
                            LogUtils.e("设备更新  application");
                            getDevicesFromServer.onNext(allBindDevices);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }


    public void setAllBindDevices(AllBindDevices allBindDevices) {
        homeShowDevices = allBindDevices.getHomeShow();
        LogUtils.e("获取到的首页设备个数是   " + homeShowDevices.size());
        getDevicesFromServer.onNext(allBindDevices);
    }

    public void setHomeshowDevice(List<HomeShowBean> homeshowDeviceList) {
        homeShowDevices = homeshowDeviceList;
    }

    public List<HomeShowBean> getHomeShowDevices() {
        List<HomeShowBean> tem = new ArrayList<>();
        for (HomeShowBean homeShowBean : homeShowDevices) {
            if (homeShowBean.getDeviceType() != HomeShowBean.TYPE_GATEWAY) {
                tem.add(homeShowBean);
            }
        }
        return tem;
    }

    public List<HomeShowBean> getAllDevices() {
        return homeShowDevices;
    }


    public String getNickByDeviceId(String deviceId) {
        for (HomeShowBean homeShowBean : homeShowDevices) {
            if (deviceId.equals(homeShowBean.getDeviceId())) {
                return homeShowBean.getDeviceNickName();
            }
        }
        return deviceId;

    }

    //根据网关id查找出网关
    public GatewayInfo getGatewayById(String gatewayId) {
        for (HomeShowBean homeShowBean : homeShowDevices) {
            if (gatewayId.equals(homeShowBean.getDeviceId())) {
                return (GatewayInfo) homeShowBean.getObject();
            }
        }
        return null;
    }

    //根据设备id查找出设备
    public GwLockInfo getGatewayLockById(String deviceId) {
        for (HomeShowBean homeShowBean : homeShowDevices) {
            if (deviceId.equals(homeShowBean.getDeviceId())) {
                return (GwLockInfo) homeShowBean.getObject();
            }
        }
        return null;
    }

    //遍历网关下的设备
    public List<HomeShowBean> getGatewayBindList(String gatewayId) {
        //遍历绑定的网关设备
        List<HomeShowBean> gatewayBindList = new ArrayList<>();
        for (HomeShowBean homeShowBean : homeShowDevices) {
            if (homeShowBean.getDeviceType() == HomeShowBean.TYPE_GATEWAY_LOCK) {
                GwLockInfo gwLockInfo = (GwLockInfo) homeShowBean.getObject();
                if (gwLockInfo.getGwID().equals(gatewayId)) {
                    gatewayBindList.add(homeShowBean);
                }
            } else if (homeShowBean.getDeviceType() == HomeShowBean.TYPE_CAT_EYE) {
                CateEyeInfo cateEyeInfo = (CateEyeInfo) homeShowBean.getObject();
                if (cateEyeInfo.getGwID().equals(gatewayId)) {
                    gatewayBindList.add(homeShowBean);
                }
            }
        }
        return gatewayBindList;

    }

    //所有网关
    public List<GatewayInfo> getAllGateway() {
        List<GatewayInfo> gatewayList = new ArrayList<>();
        for (HomeShowBean homeShowBean : homeShowDevices) {
            if (homeShowBean.getDeviceType() == HomeShowBean.TYPE_GATEWAY) {
                gatewayList.add((GatewayInfo) homeShowBean.getObject());
            }
        }
        return gatewayList;
    }


    /**
     * 获取缓存的设备
     */

    public AllBindDevices getAllBindDevices() {
        return allBindDevices;
    }

    /**
     * 从服务器获取到设备
     */
    public PublishSubject<AllBindDevices> getDevicesFromServer = PublishSubject.create();

    /**
     *
     */
    public Observable<AllBindDevices> listenerAllDevices() {
        return getDevicesFromServer;
    }

    /**
     * 监听蓝牙服务是否启动成功和mqtt服务是否启动成功
     */
    public PublishSubject<Integer> getServiceConnected = PublishSubject.create();

    public Observable<Integer> listenerServiceConnect() {
        return getServiceConnected;
    }


    private long isComingTime;

    /**
     * 获取  呼进来的时间
     *
     * @return
     */
    public long getIsComingTime() {
        return isComingTime;
    }

    public void setIsComingTime(long isComingTime) {
        this.isComingTime = isComingTime;
    }


    private int linphone_port;

    /**
     * 获取 linphone的端口号
     *
     * @return
     */
    public int getLinphone_port() {
        return linphone_port;
    }

    public void setLinphone_port(int linphone_port) {
        this.linphone_port = linphone_port;
    }


    private boolean isVideoActivityRun = false;

    public boolean isVideoActivityRun() {
        return isVideoActivityRun;
    }

    public void setVideoActivityRun(boolean videoActivityRun) {
        isVideoActivityRun = videoActivityRun;
    }

    // 快照图片
    private LinkedList<String> pirListImg;

    public LinkedList<String> getPirListImg() {
        return pirListImg;
    }

    public void setPirListImg(LinkedList<String> pirListImg) {
        this.pirListImg = pirListImg;
    }

    boolean isPreviewActivity = false;

    public boolean isPreviewActivity() {
        return isPreviewActivity;
    }

    public void setPreviewActivity(boolean previewActivity) {
        isPreviewActivity = previewActivity;
    }

    boolean isMediaPlayerActivity = false;

    public boolean isMediaPlayerActivity() {
        return isMediaPlayerActivity;
    }

    public void setMediaPlayerActivity(boolean mediaPlayerActivity) {
        isMediaPlayerActivity = mediaPlayerActivity;
    }

    private static DaoSession daoWriteSession;
    private DaoManager manager;

    private void setUpWriteDataBase() {
        manager = DaoManager.getInstance(this);
        daoWriteSession = manager.getDaoSession();
    }

    public DaoSession getDaoWriteSession() {
        return daoWriteSession;
    }

    // DemoPushService.class 自定义服务名称, 核心服务
    private Class userPushService = GeTuiPushService.class;

    String currentGeTuiMimiUserName;
    String currentGeTuiMImiPwd;

    public String getCurrentGeTuiMimiUserName() {
        return currentGeTuiMimiUserName;
    }

    public void setCurrentGeTuiMimiUserName(String currentGeTuiMimiUserName) {
        this.currentGeTuiMimiUserName = currentGeTuiMimiUserName;
    }

    public String getCurrentGeTuiMImiPwd() {
        return currentGeTuiMImiPwd;
    }

    public void setCurrentGeTuiMImiPwd(String currentGeTuiMImiPwd) {
        this.currentGeTuiMImiPwd = currentGeTuiMImiPwd;
    }

    String sip_package_invite;

    public String getSip_package_invite() {
        return sip_package_invite;
    }

    public void setSip_package_invite(String sip_package_invite) {
        this.sip_package_invite = sip_package_invite;
    }

    boolean isFromWel=false;

    public boolean isFromWel() {
        return isFromWel;
    }

    public void setFromWel(boolean fromWel) {
        isFromWel = fromWel;
    }

    int pirEnableStates = 1;

    public int getPirEnableStates() {
        return pirEnableStates;
    }

    public void setPirEnableStates(int pirEnableStates) {
        this.pirEnableStates = pirEnableStates;
    }

    boolean isPopDialog = false;

    public boolean isPopDialog() {
        return isPopDialog;
    }

    public void setPopDialog(boolean popDialog) {
        isPopDialog = popDialog;
    }


    public void reStartApp() {
        Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
        PendingIntent restartIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager mgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() , restartIntent); // 1秒钟后重启应用
        System.exit(0);
    }
}



