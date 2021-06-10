package com.kaadas.lock.activity;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hms.aaid.HmsInstanceId;
import com.huawei.hms.common.ApiException;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.cateye.VideoVActivity;
import com.kaadas.lock.bean.UpgradeBean;
import com.kaadas.lock.fragment.PersonalCenterFragment;
import com.kaadas.lock.fragment.device.DeviceFragment;
import com.kaadas.lock.fragment.home.HomePageFragment;
import com.kaadas.lock.mvp.mvpbase.BaseBleActivity;
import com.kaadas.lock.mvp.presenter.MainActivityPresenter;
import com.kaadas.lock.mvp.presenter.UpgradePresenter;
import com.kaadas.lock.mvp.view.IMainActivityView;
import com.kaadas.lock.publiclibrary.bean.BleLockInfo;
import com.kaadas.lock.publiclibrary.bean.CateEyeInfo;
import com.kaadas.lock.publiclibrary.bean.GatewayInfo;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.GatewayOtaNotifyBean;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttService;
import com.kaadas.lock.publiclibrary.ota.ble.OTADialogActivity;
import com.kaadas.lock.publiclibrary.ota.gatewayota.GatewayOTADialogActivity;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.Constants;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.MyLog;
import com.kaadas.lock.utils.NotificationUtil;
import com.kaadas.lock.utils.PermissionUtil;
import com.kaadas.lock.utils.Rom;
import com.kaadas.lock.utils.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.kaadas.lock.utils.ftp.GeTui;
import com.kaadas.lock.utils.greenDao.bean.CatEyeEvent;
import com.kaadas.lock.utils.networkListenerutil.NetWorkChangReceiver;
import com.kaadas.lock.widget.BottomMenuSelectMarketDialog;
import com.kaadas.lock.widget.NoScrollViewPager;
import com.kaidishi.lock.xiaomi.SPUtils2;
import com.kaidishi.lock.xiaomi.XiaoMiConstant;

import net.sdvn.cmapi.CMAPI;
import net.sdvn.cmapi.ConnectionService;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.kaadas.lock.utils.PermissionUtil.REQUEST_AUDIO_PERMISSION_REQUEST_CODE;

public class MainActivity extends BaseBleActivity<IMainActivityView, MainActivityPresenter<IMainActivityView>>
        implements ViewPager.OnPageChangeListener, IMainActivityView, RadioGroup.OnCheckedChangeListener {
    @BindView(R.id.rb_one)
    RadioButton rbOne;
    @BindView(R.id.rb_two)
    RadioButton rbTwo;
    @BindView(R.id.rb_three)
    RadioButton rbThree;

    @BindView(R.id.rg)
    RadioGroup rg;
    @BindView(R.id.home_view_pager)
    NoScrollViewPager homeViewPager;


    private List<Fragment> fragments = new ArrayList<>();
    private boolean isOnBackground = false;
    private static MainActivity instance;
    private static final int REQUEST_CODE_VPN_SERVICE = 11;

    public boolean isSelectHome = true;
    private NetWorkChangReceiver netWorkChangReceiver;
    private WifiReceiver mReceiver = null;
    private boolean isRegistered = false;
    UpgradePresenter upgradePresenter = null;
    public static boolean isRunning = false;
//    public static NetEvevt evevt;
    boolean isCreate = false;
    String sip_pacage_invite = null;
    boolean isFromWelCom = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        LogUtils.e("MainActivity启动 ");
        ButterKnife.bind(this);
        PermissionUtil.getInstance().requestPermission(PermissionUtil.getInstance().permission, this);
        isRunning = true;
        rg.setOnCheckedChangeListener(this);
        MqttService mqttService = MyApplication.getInstance().getMqttService();
        if (mqttService != null) {
            boolean connected = false;
            try {
                connected = mqttService.getMqttClient().isConnected();
            } catch (Exception e) {
                LogUtils.e("  获取连接状态失败  " + e.getMessage());
                connected = false;
            }
            if (mqttService.getMqttClient() == null || !connected) {
                MyApplication.getInstance().getMqttService().mqttConnection(); //连接mqtt
            }
        }
        MyLog.getInstance().save("MainActivity==>OnCreate");
        fragments.add(new HomePageFragment());
        fragments.add(new DeviceFragment());
//        fragments.add(new ShopFragment());
        fragments.add(new PersonalCenterFragment());
//        evevt = this;
        instance = this;
        isCreate = true;
        homeViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return fragments.get(i);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        });

        //首页的fragment不重新加载，导致各种问题
        homeViewPager.setOffscreenPageLimit(fragments.size());

        //设置Linphone的监听

        mPresenter.initLinphone();

//        boolean isfromlogin= getIntent().getBooleanExtra(Constants.ISFROMLOGIN,false);
//        boolean ispush= (boolean) SPUtils.get(Constants.PUSHID,false);
//        Log.e(GeTui.VideoLog,"isfromlogin:"+isfromlogin);
//        if(isfromlogin){
//             mPresenter.uploadpushmethod();
//        }else if(!isfromlogin && !ispush){
//             Log.e(GeTui.VideoLog,"重新上传pushid.......");
//             mPresenter.uploadpushmethod();
//        }
        registerNetwork();
        sip_pacage_invite = MyApplication.getInstance().getSip_package_invite();
        // if come from phone, dont to prompt update
        if (!TextUtils.isEmpty(sip_pacage_invite)) {  // not null sip_package
            startcallmethod(sip_pacage_invite);
            return;
        }
        // app update info
        initPackages(this);
        upgradePresenter = new UpgradePresenter();
        upgradePresenter.getUpgreadJson(new UpgradePresenter.IUpgradePresenter() {
            @Override
            public void ShowUpgradePresenterSuccess(String jsonPresenterResult) {
                //   Log.e(GeTui.VideoLog,jsonPresenterResult);
                if (!TextUtils.isEmpty(jsonPresenterResult)) {
                    UpgradeBean upgradeBean = new Gson().fromJson(jsonPresenterResult, UpgradeBean.class);
                    try {
                        PackageManager manager = getPackageManager();
                        PackageInfo packageInfo = manager.getPackageInfo(getPackageName(), 0);
                        int cuurentversioncode = packageInfo.versionCode;
                        String versionname = packageInfo.versionName;
                        int servercode = Integer.parseInt(upgradeBean.getVersionCode());
                        boolean isPrompt = Boolean.parseBoolean(upgradeBean.getIsPrompt()); //是否提示用户升级
                        boolean isForced = Boolean.parseBoolean(upgradeBean.getIsForced()); //是否强制升级
                        if (isPrompt) {
                            if (servercode > cuurentversioncode) {
                                SelectMarket(isForced, upgradeBean);
                            }
                        }
                        Log.e(GeTui.VideoLog, "currentCode:" + cuurentversioncode + " servercode:" + upgradeBean.getVersionCode());

                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    Log.e(GeTui.VideoLog, "update.....获取数据为null");
                }
            }

            @Override
            public void ShowUpgradePresenterFail() {
                Log.e(GeTui.VideoLog, "update.....fail.......失败");
            }
        });
        registerWifiReceiver();

//        RadioButton rb_shop = findViewById(R.id.rb_shop);
//        rb_shop.setVisibility(View.GONE);
        LogUtils.e("MainActivity启动完成 ");

        checkNotificatoinEnabled();
    }

    private void checkNotificatoinEnabled() {
        if(!NotificationUtil.isNotifyEnabled(this)){
            AlertDialogUtil.getInstance().noEditTitleTwoButtonDialog(this, "检测到您没有打开通知权限，是否去打开",
                    getString(R.string.cancel), getString(R.string.confirm), "#A4A4A4", "#1F96F7", new AlertDialogUtil.ClickListener() {
                        @Override
                        public void left() {

                        }

                        @Override
                        public void right() {
                            NotificationUtil.settingActivity(MainActivity.this);
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(String toString) {

                        }
                    });
        }
    }

    private static List<String> packages;
    private BottomMenuSelectMarketDialog bottomMenuDialog;

    public void SelectMarket(boolean isforce, UpgradeBean upgradeBean) {
        BottomMenuSelectMarketDialog.Builder dialogBuilder = new BottomMenuSelectMarketDialog.Builder(this);
        for (final String pkg : packages) {
            String menu = getNameByPackage(pkg);
            dialogBuilder.setVersionStr(upgradeBean.getVersionName());
            dialogBuilder.addMenu(menu, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (bottomMenuDialog != null) {
                        drumpMarket(pkg);
                        bottomMenuDialog.dismiss();
                    }
                }
            });
        }

        dialogBuilder.setCancelListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Message message = new Message();
//                message.what = 2;
//                mHandler.sendMessage(message);
                if (isforce) {
                    Toast.makeText(MainActivity.this, getString(R.string.isforce), Toast.LENGTH_SHORT).show();
                } else {
                    bottomMenuDialog.dismiss();
                }

            }
        });

//        if (versionBean.getIsForced()) {
//            dialogBuilder.goneCancel();
//        }
        bottomMenuDialog = dialogBuilder.create();
        bottomMenuDialog.setCancelable(false);
        bottomMenuDialog.show();
    }

    private void drumpMarket(String packageName) {
        Log.e(GeTui.VideoLog, "跳入的市场是:" + packageName);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse("market://details?id=" + getPackageName());//app包名
        intent.setData(uri);
        intent.setPackage(packageName);//应用市场包名
        startActivity(intent);
    }

    @Override
    protected MainActivityPresenter<IMainActivityView> createPresent() {
        return new MainActivityPresenter<>(this);
    }

    private class WifiReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) {
                return;
            }
            WifiManager wifiManager = (WifiManager) context.getApplicationContext()
                    .getSystemService(WIFI_SERVICE);
            assert wifiManager != null;

            switch (action) {
                case WifiManager.NETWORK_STATE_CHANGED_ACTION:
                case LocationManager.PROVIDERS_CHANGED_ACTION:
                    onWifiChanged(wifiManager.getConnectionInfo());
                    break;
            }
        }
    }

    private void onWifiChanged(WifiInfo info) {
        boolean disconnected = info == null
                || info.getNetworkId() == -1
                || "<unknown ssid>".equals(info.getSSID());
        if (disconnected) {
            LogUtils.e("切换wifi  断开连接 from MainActivity");
        } else {
            String ssid = info.getSSID();
            if (ssid.startsWith("\"") && ssid.endsWith("\"")) {
                ssid = ssid.substring(1, ssid.length() - 1);
            }
            LogUtils.e("切换wifi  ssid " + ssid);
        }
    }


    /*//TODO：删除华为推送位置
    @Override
    public void onNetEventToken(String token) {
        uploadToken(3,token);
    }*/


    public interface HomeSelectListener {
        void onHomeSelectChange(boolean isSelect);
    }

    private List<HomeSelectListener> listeners = new ArrayList<>();

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_one:
                homeViewPager.setCurrentItem(0);
                break;
            case R.id.rb_two:
                homeViewPager.setCurrentItem(1);
                break;
//            case R.id.rb_shop:
//                homeViewPager.setCurrentItem(2);
//                break;
            case R.id.rb_three:
                homeViewPager.setCurrentItem(3);
                break;
        }
    }

    public boolean isOnBackground() {
        return isOnBackground;
    }

    boolean ispush = false;

    @Override
    protected void onStart() {
        super.onStart();
        isOnBackground = false;

        ispush = (boolean) SPUtils.get(Constants.PUSHID, false);
        if(ispush){
            LogUtils.e("上传成功token...");
            return;
        }
        if (Rom.isEmui() || Rom.isHarmony()) {
            // no get token
              String huawei = (String) SPUtils.get(GeTui.HUAWEI_KEY, "");
            if (TextUtils.isEmpty(huawei)) {
                // 初始化,生成token失败
                getToken();
            } else {
                uploadToken(3,huawei);
            }
        } else if(Rom.isMiui()){
            String xiaoMiToken = (String) SPUtils2.get(MainActivity.this, XiaoMiConstant.XIAOMIKEY,"");
            uploadToken(4,xiaoMiToken);
            LogUtils.e("shulan xiaoMiToken--->" + xiaoMiToken);
        } else{
            // 使用个推
//            if (!ispush) {
//                String JpushId = (String) SPUtils2.get(MyApplication.getInstance(), GeTui.JPUSH_ID, "");
//                mPresenter.uploadpushmethod(2,JpushId );
//            } else {
//                Log.e(GeTui.VideoLog, "getui upload to success");
//            }
                String JpushId = (String) SPUtils.get(GeTui.JPUSH_ID, "");
            LogUtils.e("shulan---JpushId-->---" + JpushId );
                uploadToken(2,JpushId);
        }

        boolean isUploadPhoneMsg = (boolean) SPUtils.get(Constants.PHONE_MSG_UPLOAD_STATUS, false);
        if (!isUploadPhoneMsg) {
            mPresenter.uploadPhoneMessage();
        }

    }

    @Override
    protected void onResume() {

        String sip_package_json = getIntent().getStringExtra("stringType");
        Log.e(GeTui.VideoLog, "..........MainActivity.........:" + sip_package_json);

        sip_pacage_invite = MyApplication.getInstance().getSip_package_invite();
        // if come from phone, dont to prompt update
//        if(TextUtils.isEmpty(sip_pacage_invite)){
//            MyLog.getInstance().save("sip_package_invert..1");
//            Log.e(GeTui.VideoLog,"sip_package_invert..1");
//            sip_pacage_invite= getIntent().getStringExtra(Constants.SIP_INVERT_PKG_INTENT);
//            if(TextUtils.isEmpty(sip_pacage_invite)){
//                MyLog.getInstance().save("sip_package_invert..2");
//                Log.e(GeTui.VideoLog,"sip_package_invert..2");
//                sip_pacage_invite = (String) SPUtils.get(Constants.SIP_INVERT_PKG_SP,null);
//            }
//        }
//        isFromWelCom= getIntent().getBooleanExtra(Constants.IS_FROM_WEL_INTENT,false);
//        if(!isFromWelCom){
//            MyLog.getInstance().save("isFromWelCom..1");
//            Log.e(GeTui.VideoLog,"isFromWelCom..1");
//              isFromWelCom= (boolean) SPUtils.get(Constants.IS_FROM_WEL_SP,false);
//        }
        isFromWelCom = MyApplication.getInstance().isFromWel();
        MyLog.getInstance().save("MainAcvity...onResume.." + !isCreate + " invert_package:" + !TextUtils.isEmpty(sip_pacage_invite) + " isFromWelCom:" + isFromWelCom);
        Log.e(GeTui.VideoLog, "MainAcvity...onResume.." + !isCreate + " invert_package:" + !TextUtils.isEmpty(sip_pacage_invite) + " isFromWelCom:" + isFromWelCom);

        if (isFromWelCom && !isCreate && !TextUtils.isEmpty(sip_pacage_invite)) {
            startcallmethod(sip_pacage_invite);  // 获取Linphone端口号
        } else if (!isCreate && isFromWelCom && TextUtils.isEmpty(sip_pacage_invite)) {
            Toast.makeText(MainActivity.this, getString(R.string.cateye_call_fail), Toast.LENGTH_SHORT).show();
        }
        isFromWelCom = false;
        isCreate = false;
        sip_pacage_invite = null;

        super.onResume();
    }

    public void uploadToken(int type,String token) {
        mPresenter.uploadpushmethod( type, token);
    }

    public static final boolean isInstanciated() {
        return instance != null;
    }


    public static final MainActivity instance() {
        if (instance != null)
            return instance;
        throw new RuntimeException("LinphoneActivity not instantiated yet");
    }

    @Override
    protected void onPause() {
        super.onPause();
        //   isFromWelCom=false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(GeTui.VideoLog, "MainAcvity .. onStop..");
        MyLog.getInstance().save("MainAcvity .. onStop..");
        SPUtils.remove(Constants.SIP_INVERT_PKG_SP);
        SPUtils.remove(Constants.IS_FROM_WEL_SP);
    }


    //检查vpn授权
    public void checkVpnService() {
        Intent prepare = ConnectionService.prepare(this);
        boolean resultvpn = prepare == null ? true : false;
        net.sdvn.cmapi.util.LogUtils.d(resultvpn + " 已授权 " + " 未授权 ");
        if (prepare != null) {
            startActivityForResult(prepare, REQUEST_CODE_VPN_SERVICE);
        } else {
            onActivityResult(REQUEST_CODE_VPN_SERVICE, RESULT_OK, null);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //  vpn  授权
        if (requestCode == REQUEST_CODE_VPN_SERVICE) {
            CMAPI.getInstance().onVpnPrepareResult(requestCode, resultCode);
        }
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        if (i == 0) {
            isSelectHome = true;
            for (HomeSelectListener listener : listeners) {
                listener.onHomeSelectChange(true);
            }
        } else {
            isSelectHome = false;
            for (HomeSelectListener listener : listeners) {
                listener.onHomeSelectChange(false);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }


    @Override
    public void onWarringUp(String warringContent) {
        Toast.makeText(this, warringContent, Toast.LENGTH_LONG).show();
//        ToastUtil.getInstance().showLong(warringContent);
    }

    @Override
    public void onDeviceInBoot(BleLockInfo bleLockInfo) {
        //todo ota 处理
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, OTADialogActivity.class);
        intent.putExtra(KeyConstants.BLE_DEVICE_INFO, bleLockInfo);  //需要使用
        startActivity(intent);
    }

    @Override
    public void onCatEyeCallIn(CateEyeInfo cateEyeInfo) {
        Log.e(GeTui.VideoLog, "MainActivity---->跳入=====>VideoVActivity:" + cateEyeInfo);
        MyLog.getInstance().save("跳入=====>VideoVActivity:" + cateEyeInfo);
        Intent intent = new Intent(this, VideoVActivity.class);
        intent.putExtra(KeyConstants.IS_CALL_IN, true);
        intent.putExtra(KeyConstants.CATE_INFO, cateEyeInfo);
        startActivity(intent);
    }

    @Override
    public void onCatEyeCallFail() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, getString(R.string.video_connection_fail), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onGwEvent(int eventType, String deviceId, String gatewayId) {
        GatewayInfo gatewayInfo = MyApplication.getInstance().getGatewayById(gatewayId);
        if (gatewayInfo != null) {
            String nickName = MyApplication.getInstance().getNickByDeviceId(deviceId);
            String content = null;
            switch (eventType) {
                case CatEyeEvent.EVENT_PIR:
                    content = String.format(getString(R.string.pir_notify), nickName);
                    break;
                case CatEyeEvent.EVENT_DOOR_BELL:
//                content = String.format(getString(R.string.door_bell), nickName);
//                ToastUtil.getInstance().showShort(content);
                    break;
                case CatEyeEvent.EVENT_HEAD_LOST:
                    content = String.format(getString(R.string.head_lost_notify), nickName);
                    break;
                case CatEyeEvent.EVENT_HOST_LOST:
                    content = String.format(getString(R.string.host_lost_notify), nickName);
                    break;
                case CatEyeEvent.EVENT_LOW_POWER:
                    content = String.format(getString(R.string.low_power_notify), nickName);
                    break;
            }
            ToastUtils.showLong(content);
            LogUtils.e("猫眼报警的内容为   " + content);
        }
    }

    @Override
    public void uploadpush(BaseResult baseResult) {
        String code = baseResult.getCode();
        if (code.equals("200")) {
            Log.e(GeTui.VideoLog, "push上传成功");
            SPUtils.put(Constants.PUSHID, true);
        } else {
            Log.e(GeTui.VideoLog, "push上传失败");
        }
    }

    @Override
    public void onGwLockEvent(int alarmCode, int clusterID, String deviceId, String gatewayId) {
        GatewayInfo gatewayInfo = MyApplication.getInstance().getGatewayById(gatewayId);
        if (gatewayInfo != null) {
            String str = "";
            String nickName = MyApplication.getInstance().getNickByDeviceId(deviceId);
            if (clusterID == 257) {
                switch (alarmCode) {
                    case 0:    //门锁堵转报警
                        str = String.format(getString(R.string.lock_blocked_notify), nickName);
                        ToastUtils.showShort(str);
                        break;
                    case 1:
                        str = String.format(getString(R.string.lock_resect_notify), nickName);
                        ToastUtils.showShort(str);
                        break;
                    case 4:
                        str = String.format(getString(R.string.lock_system_notify), nickName);
                        ToastUtils.showShort(str);
                        break;
                    case 6:
                        str = String.format(getString(R.string.lock_pick_proof_notify), nickName);
                        ToastUtils.showShort(str);
                        break;
                    case 9:
                        str = String.format(getString(R.string.lock_stress_alarm_notify), nickName);
                        ToastUtils.showShort(str);
                        break;
                }
                //电量
            } else if (clusterID == 1) {
                switch (alarmCode) {
                    case 16:
                        str = String.format(getString(R.string.low_power_notify), nickName);
                        ToastUtils.showShort(str);
                        break;
                }
            }
        }
    }

    @Override
    public void callError() {
        Toast.makeText(this, getString(R.string.cateye_call_record), Toast.LENGTH_LONG).show();
    }

    @Override
    public void callErrorCateInfoEmpty() {
        Toast.makeText(this, getString(R.string.call_error_cateInfoEmpty), Toast.LENGTH_LONG).show();
    }

    @Override
    public void callErrorCateInfoMimi() {
        Toast.makeText(this, getString(R.string.call_error_mimi), Toast.LENGTH_LONG).show();
    }

    @Override
    public void gatewayNotifyOtaSuccess(GatewayOtaNotifyBean notifyBean) {
        LogUtils.e("网关ota升级通知 gatewayNotifyOtaSuccess");
        //网关ota升级通知
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, GatewayOTADialogActivity.class);
        intent.putExtra(KeyConstants.GATEWAY_OTA_UPGRADE, notifyBean);
        startActivity(intent);
    }

    @Override
    public void gatewayResetSuccess(String gatewayId) {
        ToastUtils.showShort(gatewayId + "网关:" + getString(R.string.gateway_reset_unbind));
    }

    @Override
    public void onWifiLockAlarmEvent(String wifiSn, int alarmCode) {
        String content = "";
        WifiLockInfo wifiLockInfoBySn = MyApplication.getInstance().getWifiLockInfoBySn(wifiSn);
        String nickName = "";
        if (wifiLockInfoBySn != null) {
            nickName = wifiLockInfoBySn.getLockNickname();
        }

        switch (alarmCode) {
            case 0x01: //锁定报警（输入错误密码或指纹或卡片超过 10 次就报 警系统锁定）
                content = String.format(getString(R.string.wifi_lock_alarm_1), nickName);
                break;
            case 0x02:// 劫持报警（输入防劫持密码或防劫持指纹开锁就报警）
                content = String.format(getString(R.string.wifi_lock_alarm_2), nickName);
                break;
            case 0x03:// 三次错误报警
                content = String.format(getString(R.string.wifi_lock_alarm_3), nickName);
                break;
            case 0x04:// 防撬报警（锁被撬开）
                content = String.format(getString(R.string.wifi_lock_alarm_4), nickName);
                break;
            case 0x08:// 机械方式报警（使用机械方式开锁）
                content = String.format(getString(R.string.wifi_lock_alarm_8), nickName);
                break;
            case 0x10:// 低电压报警（电池电量不足）
                content = String.format(getString(R.string.wifi_lock_alarm_10), nickName);
                break;
            case 0x20:// 锁体异常报警（旧:门锁不上报警）
                content = String.format(getString(R.string.wifi_lock_alarm_20), nickName);
                break;
            case 0x40:// 门锁布防报警
                content = String.format(getString(R.string.wifi_lock_alarm_40), nickName);
                break;
            case 0x80:// 一级低电告警（电量低，进入节能模式）
                content = String.format(getString(R.string.wifi_lock_alarm_80), nickName);
                break;
            case 0x60://门铃报警
                break;
            case 0x70://pir 动作
                break;
        }
        if(!content.isEmpty())
            Toast.makeText(MainActivity.this, content, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void needCheckVpnService() {
        checkVpnService();
    }


    public NoScrollViewPager getViewPager() {

        return homeViewPager;
    }

    Timer timer;
    public static final int ERROR_1 = 1;
    public static final int ERROR_2 = 2;
    public static final int ERROR_3 = 3;
    public static final int ERROR_4 = 4;
    Handler errorHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ERROR_1:
                    Toast.makeText(MainActivity.this, getString(R.string.sip_register_fail), Toast.LENGTH_LONG).show();
                    break;
                case ERROR_2:
                    Toast.makeText(MainActivity.this, getString(R.string.socket_send_failed), Toast.LENGTH_LONG).show();
                    break;
                case ERROR_3:
                    Toast.makeText(MainActivity.this, getString(R.string.io_send_failed), Toast.LENGTH_LONG).show();
                    break;
                case ERROR_4:
                    Toast.makeText(MainActivity.this, getString(R.string.get_port_failed), Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };
    int getPortTimes = 0;

    private void startcallmethod(String sip_pacage_invite1) {

        MyApplication.getInstance().setSip_package_invite(null); //制空
        MyApplication.getInstance().setFromWel(false);

        long startTime = 2500;
        if (Rom.isFlyme()) {
            startTime = 5000;
        } else if (Rom.isMiui()) {
            startTime = 3500;
        }
        final String Tag1 = "sip_kaidishi";
        if (timer == null) {
            timer = new Timer();
            //           sip_pacage_invite = MyApplication.getInstance().getSip_package_invite();
//            if(TextUtils.isEmpty(sip_pacage_invite)){
//                sip_pacage_invite= getIntent().getStringExtra("invert");
//            }
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    int linphone_port = MyApplication.getInstance().getLinphone_port();

                    boolean isRegisterStatus = (boolean) SPUtils.get(Constants.LINPHONE_REGESTER_STATE, false);
                    Log.e(GeTui.VideoLog, "MainActivity==>port:" + linphone_port + " sip_pkg:" + sip_pacage_invite1 + " isRegisterStatus:" + isRegisterStatus);
//                    Log.e(GeTui.VideoLog,"sip_pacage_invite:"+sip_pacage_invite);
                    MyLog.getInstance().save("prot:" + linphone_port + " sip_pkg:" + sip_pacage_invite1 + " isRegisterStatus:" + isRegisterStatus);

                    if (!isRegisterStatus && linphone_port > 0) {
                        SystemClock.sleep(3000);
                        isRegisterStatus = (boolean) SPUtils.get(Constants.LINPHONE_REGESTER_STATE, false);
                    }

                    if (!isRegisterStatus && linphone_port > 0) {
                        Log.e(GeTui.VideoLog, "MainActivity==>port:" + linphone_port + " isRegisterStatus:" + isRegisterStatus);
                        errorHandler.sendEmptyMessage(ERROR_1);
                        timer.cancel();
                        timer = null;
                        getPortTimes = 0;
                        return;
                    }
                    if (linphone_port <= 0 && getPortTimes == 10) {
                        Log.e(GeTui.VideoLog, "MainActivity==>getPortTimes:" + getPortTimes + " linphone_port:" + linphone_port);
                        errorHandler.sendEmptyMessage(ERROR_1);
                        timer.cancel();
                        timer = null;
                        getPortTimes = 0;
                        return;
                    }
                    if (linphone_port > 0 && isRegisterStatus) {
                        Log.e(GeTui.VideoLog, "MainActivity==>isRegisterStatus:" + isRegisterStatus + " linphone_port:" + linphone_port);
                        timer.cancel();
                        timer = null;
                        getPortTimes = 0;
                        try {
                            //建立udp的服务
                            DatagramSocket datagramSocket = new DatagramSocket();
                            //准备数据，把数据封装到数据包中。
                            //	String data = TestUdp.und_package;
                            //创建了一个数据包
                            InetAddress inetAddress = InetAddress.getLocalHost();
                            String ipaddress = inetAddress.getHostAddress();
                            DatagramPacket packet = new DatagramPacket(sip_pacage_invite1.getBytes(), sip_pacage_invite1.getBytes().length, inetAddress, linphone_port);
                            //调用udp的服务发送数据包
                            datagramSocket.send(packet);
                            //关闭资源 ---实际上就是释放占用的端口号
                            datagramSocket.close();
                        } catch (SocketException e) {
                            e.printStackTrace();
                            Log.e(Tag1, "SocketException:" + e.getMessage());
                            errorHandler.sendEmptyMessage(ERROR_2);
                        }  /* catch (UnknownHostException e) {
								e.printStackTrace();
								Log.e(Tag1,"UnknownHostException"+e.getMessage());
							}  */ catch (IOException e) {
                            e.printStackTrace();
                            Log.e(Tag1, "IOException" + e.getMessage());
                            errorHandler.sendEmptyMessage(ERROR_3);
                        }
                    } else {
                        Log.e(Tag1, "获取端口失败");
                        getPortTimes++;
//                            timer.cancel();
//                            timer = null;
                    }
                    Log.e(Tag1, "获取的端口是:" + linphone_port);
                }
            }, startTime, 500);
            // 2500
        }
    }

    private long mExitTime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(this, R.string.exit, Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
//                MainActivity.this.finish();
                System.exit(0);
                //       moveTaskToBack(true);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //注册网络状态监听广播
    private void registerNetwork() {
        netWorkChangReceiver = new NetWorkChangReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        filter.addAction("android.net.wifi.STATE_CHANGE");
        registerReceiver(netWorkChangReceiver, filter);
        isRegistered = true;
    }

    public static final String[] supportMarket = new String[]{
            "com.xiaomi.market",
            "com.huawei.appmarket",
            "com.oppo.market",
            "com.tencent.android.qqdownloader",
            "com.android.vending",
            "com.bbk.appstore",
    };

    /**
     * 获取APP支持的且手机安装的应用市场列表
     *
     * @param context
     * @return
     */
    public static List<String> initPackages(Context context) {
        ArrayList<String> pkgs = new ArrayList<>();
        if (context == null)
            return pkgs;
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setData(Uri.parse("market://details?id="));
        PackageManager pm = context.getPackageManager();
        // 通过queryIntentActivities获取ResolveInfo对象
        List<ResolveInfo> infos = pm.queryIntentActivities(intent,
                0);
        if (infos == null || infos.size() == 0)
            return pkgs;
        int size = infos.size();
        for (int i = 0; i < size; i++) {
            String pkgName = "";
            try {
                ActivityInfo activityInfo = infos.get(i).activityInfo;
                pkgName = activityInfo.packageName;
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!TextUtils.isEmpty(pkgName))
                pkgs.add(pkgName);
        }

        packages = new ArrayList<>();

        List<String> temp = Arrays.asList(supportMarket);
        for (String s : pkgs) {
            if (temp.contains(s)) {
                packages.add(s);
            }
        }
        return packages;
    }

    //com.xiaomi.market 小米应用商店
    //com.huawei.appmarket 华为应用商店
    //com.oppo.market OPPO应用商店
    //com.tencent.android.qqdownloader 腾讯应用宝
    //com.android.vending    Google player
    //com.bbk.appstore		vivo应用商店

    private String getNameByPackage(String pkg) {
        if (pkg.equals(supportMarket[0])) {
            return getString(R.string.xiaomi_market);
        } else if (pkg.equals(supportMarket[1])) {
            return getString(R.string.huawei_market);

        } else if (pkg.equals(supportMarket[2])) {
            return getString(R.string.oppo_market);
        } else if (pkg.equals(supportMarket[3])) {
            return getString(R.string.tengxun_market);
        } else if (pkg.equals(supportMarket[4])) {
            return getString(R.string.google_market);
        } else if (pkg.equals(supportMarket[5])) {
            return getString(R.string.vivo_market);
        }
        return "";
    }

    /**
     * getToken(String appId, String scope), This method is used to obtain a token required for accessing HUAWEI Push Kit.
     * If there is no local AAID, this method will automatically generate an AAID when it is called because the Huawei Push server needs to generate a token based on the AAID.
     * This method is a synchronous method, and you cannot call it in the main thread. Otherwise, the main thread may be blocked.
     */
    private void getToken() {
        new Thread() {
            @Override
            public void run() {
                try {
                    // read from agconnect-services.json
                    String appId = AGConnectServicesConfig.fromContext(MainActivity.this).getString("client/app_id");
                    String huawei = HmsInstanceId.getInstance(MainActivity.this).getToken(appId, "HCM");
                    if(!TextUtils.isEmpty(huawei)) {
                        uploadToken(3,huawei);
                    }

                } catch (ApiException e) {
                    Log.e(GeTui.VideoLog,"get token failed, " + e);
                }
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRunning = false;
        if (isRegistered) {
            if (netWorkChangReceiver != null) {
                unregisterReceiver(netWorkChangReceiver);
            }
        }
        unResisterWifiReceiver();
    }

    private void registerWifiReceiver(){
        if(mReceiver == null){
            mReceiver = new WifiReceiver();
        }
        IntentFilter filter = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        registerReceiver(mReceiver, filter);
    }

    private void unResisterWifiReceiver(){
        if (mReceiver!=null){
            unregisterReceiver(mReceiver);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        LogUtils.e("shulan ---requestCode-->" + requestCode);
        switch (requestCode){
            case REQUEST_AUDIO_PERMISSION_REQUEST_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){//同意授权

                }else {
                    ToastUtils.showShort("请先获取麦克风权限");
                }
                break;
        }
    }
}
