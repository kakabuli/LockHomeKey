package com.kaadas.lock;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;

import com.kaadas.lock.activity.login.LoginActivity;
import com.kaadas.lock.publiclibrary.bean.BleLockInfo;
import com.kaadas.lock.publiclibrary.http.result.GetPasswordResult;
import com.kaadas.lock.publiclibrary.http.util.RetrofitServiceManager;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttService;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.ToastUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
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
    private static final String APP_ID = "wx8e524a5da9bfdd78";
    //数据库文件名称
    private static final String DB_NAME="xiaokai.db";
    // IWXAPI 是第三方app和微信通信的openApi接口
    protected MqttService mqttService;
    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.e("attachView  App启动 ");
        instance = this;
        initMqttService();//启动MqttService
        SPUtils.init(this);  //初始化SPUtils  传递Context进去  不需要每次都传递Context
        ToastUtil.init(this); //初始化ToastUtil 传递Context进去  不需要每次都传递
        initTokenAndUid();  //获取本地UUID
        listenerAppBackOrForge();
        //扫描二维码初始化
        ZXingLibrary.initDisplayOpinion(this);
    }

    /**
     * 监听程序是前台还是后台
     */
    private int count;
    public void listenerAppBackOrForge(){
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityStopped(Activity activity) {
                count--;
                LogUtils.e("程序切换", activity + "onActivityStopped  "+count);
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
                LogUtils.e("程序切换", activity + "onActivitySaveInstanceState");
            }

            @Override
            public void onActivityResumed(Activity activity) {
                LogUtils.e("程序切换", activity + "onActivityResumed");
                if (count == 0) {
                    LogUtils.e("程序切换", ">>>>>>>>>>>>>>>>>>>切到前台  lifecycle");
                    //是不是
                    listenerAppChange.onNext(false);
                }
                count++;
                LogUtils.e("程序切换", activity + "onActivityStarted  " + count);
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
                MqttService.MyBinder binder = (MqttService.MyBinder) service;
                mqttService = binder.getService();
                LogUtils.e("attachView service启动" + (mqttService == null));
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        }, Context.BIND_AUTO_CREATE);
    }


    public MqttService getMqttService(){
        return  mqttService;
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
        Boolean appUpdate= (Boolean) SPUtils.get(SPUtils.APPUPDATE,false);
        String phone= (String) SPUtils.get(SPUtils.PHONEN,"");
        //退出登录  清除数据
        SPUtils.clear();
        if (appUpdate==true){
            SPUtils.put(SPUtils.APPUPDATE,true);
        }
        if (!TextUtils.isEmpty(phone)){
            SPUtils.put(SPUtils.PHONEN,phone);
        }
       MyApplication.getInstance().initTokenAndUid();
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
     * 设备列表信息的监听
     * 如果List<BleLockInfo>为空，那么认为是有数据更新，需要重新请求数据
     * 如果List<BleLockInfo>不为空，那么认为已经请求到最新数据
     * 所以此APP只有一个地方请求数据
     */
    private PublishSubject<List<BleLockInfo>> bleLockInfoSubject = PublishSubject.create();
    List<BleLockInfo> bleLockInfos;


    //通知到DeviceActivity   和HomeFragment  在添加设备成功和删除设备成功的时候都会调用此方法，但是调用的都是
    public void setBleLockInfos(List<BleLockInfo> bleLockInfos) {
        if (bleLockInfos != null) {
            this.bleLockInfos = bleLockInfos;
        }
        bleLockInfoSubject.onNext(bleLockInfos);
    }

    public  List<BleLockInfo> getDevices(){
        return bleLockInfos;
    }

    public Observable<List<BleLockInfo>> onLoadDevice() {
        return bleLockInfoSubject;
    }


    /**
     * 设备变动
     */
    private PublishSubject<Boolean> deviceChange = PublishSubject.create();

    public PublishSubject listenerDeviceChange() {
        return deviceChange;
    }

    public void setDeviceChange() {
        deviceChange.onNext(true);
    }

    public void deleteDevice(String deviceName) {
        for (int i=0;i<bleLockInfos.size();i++){
            if (deviceName.equals(bleLockInfos.get(i).getServerLockInfo().getDevice_name())){
                bleLockInfos.remove(i);
                bleLockInfoSubject.onNext(bleLockInfos);
            }
        }
    }

    /**
     * 密码列表的存取
     */
    private Map<String, GetPasswordResult> passwordResults = new HashMap();

    public void setPasswordResults(String deviceName, GetPasswordResult passwordList,boolean isNotify) {
        LogUtils.e("设置数据  密码列表  " + deviceName + (passwordList.getData() == null));
        passwordResults.put(deviceName, passwordList);
        if (isNotify){
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
    public PublishSubject<Boolean> listenerAppState(){
        return listenerAppChange;
    }

}
