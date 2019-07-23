package com.kaidishi.lock;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.kaadas.lock.activity.MainActivity;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.login.GuidePageActivity;
import com.kaadas.lock.activity.login.LoginActivity;
import com.kaadas.lock.activity.login.PersonalVerifyFingerPrintActivity;
import com.kaadas.lock.activity.login.PersonalVerifyGesturePasswordActivity;
import com.kaadas.lock.bean.UpgradePresenter;
import com.kaadas.lock.publiclibrary.linphone.linphonenew.LinphoneService;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.bean.VersionBean;
import com.kaadas.lock.mvp.presenter.SplashPresenter;
import com.kaadas.lock.publiclibrary.ble.BleService;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttService;
import com.kaadas.lock.utils.CheckLanguageUtil;
import com.kaadas.lock.utils.Constants;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.MyLog;
import com.kaadas.lock.utils.NetUtil;
import com.kaadas.lock.utils.Rom;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.SPUtils2;
import com.kaadas.lock.utils.ToastUtil;
import com.kaadas.lock.utils.cachefloder.ACache;
import com.kaadas.lock.utils.cachefloder.CacheFloder;
import com.kaadas.lock.mvp.view.ISplashView;
import com.kaadas.lock.utils.ftp.GeTui;

import org.json.JSONException;
import org.json.JSONObject;


public class WelcomeActivity extends BaseActivity<ISplashView, SplashPresenter<ISplashView>> implements ISplashView {
    Handler handler = new Handler();
    private PackageInfo packageInfo;
    private int mVersionCode;
    private String mVersionName;
    private long currentTime=0;
    private long remainTime=0;
    Intent mainIntent=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
//        CheckLanguageUtil.getInstance().checkLag();//语言
        mainIntent=new Intent(WelcomeActivity.this, MainActivity.class);
        MyLog.getInstance().save("onCreate..............:"+MainActivity.isRunning);
        executeGeTui();
        initData();
        MqttService mqttService=MyApplication.getInstance().getMqttService();
        BleService bleService=MyApplication.getInstance().getBleService();
        if (mqttService!=null&&bleService!=null){
            LogUtils.e("蓝牙和mqttService不为空");
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    String pwd = CacheFloder.readHandPassword(ACache.get(MyApplication.getInstance()), MyApplication.getInstance().getUid() + "handPassword");
                    String fingerPwd = CacheFloder.readPhoneFingerPrint(ACache.get(MyApplication.getInstance()), MyApplication.getInstance().getUid() + "fingerStatus");
                    //一秒后判断当前是否登陆   如果登陆 跳转至首页   如果没有登陆  跳转至登陆界面
                    if (TextUtils.isEmpty(MyApplication.getInstance().getToken())) {  //没有登陆
                        boolean showGuidePage = (boolean) SPUtils.getProtect(KeyConstants.SHOW_GUIDE_PAGE, false);
                        if (!showGuidePage) {
                            //第一次
                            SPUtils.putProtect(KeyConstants.SHOW_GUIDE_PAGE, true);
                            startActivity(new Intent(WelcomeActivity.this, GuidePageActivity.class));
                            finish();
                        } else {
                            startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                            finish();
                        }
                    } else if (!TextUtils.isEmpty(fingerPwd)) {
                        //存在指纹密码
                        startActivity(new Intent(WelcomeActivity.this, PersonalVerifyFingerPrintActivity.class));
                        finish();
                    } else if (!TextUtils.isEmpty(pwd)) {
                        //存在手势密码
                        Intent intent = new Intent(WelcomeActivity.this, PersonalVerifyGesturePasswordActivity.class);
                        intent.putExtra(KeyConstants.SOURCE,"WelcomeActivity");
                        startActivity(intent);
                        finish();
                    } else {
                      //  startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                        startActivity(mainIntent);
                        MyLog.getInstance().save("................1............");
                        finish();
                    }

                }
            }, 3 * 1000);
        }else{
            LogUtils.e("监听蓝牙和mqtt服务");
            mPresenter.listenerServiceConnect();
            currentTime=System.currentTimeMillis();
        }
    }

    @Override
    protected SplashPresenter<ISplashView> createPresent() {
        return new SplashPresenter<>();
    }

    private void initData() {
        getCheckVersion();
        if (NetUtil.isNetworkAvailable()) {
            mPresenter.getAppVersion();
        } else {
            ToastUtil.getInstance().showShort(R.string.noNet);
        }
    }

    //获取版本号
    public void getCheckVersion() {
        try {
            PackageManager manager = this.getPackageManager();
            packageInfo = manager.getPackageInfo(this.getPackageName(), 0);
            // 获取版本号
            mVersionCode = packageInfo.versionCode;
            // 获取版本名
            mVersionName = packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void getVersionSuccess(VersionBean versionBean) {
        if (versionBean.getVersionCode() > mVersionCode && versionBean.getIsPrompt()) {
            // SPUtils.put(SPUtils.APPUPDATE,true);
        }
    }

    @Override
    public void getVersionFail() {

    }

    @Override
    public void serviceConnectSuccess() {
        currentTime=System.currentTimeMillis()-currentTime;
        remainTime=3*1000-currentTime;
        if (remainTime<0){
            remainTime=0;
        }
        LogUtils.e("当前是多少时间   "+currentTime+"剩余多少时间   "+remainTime);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String pwd = CacheFloder.readHandPassword(ACache.get(MyApplication.getInstance()), MyApplication.getInstance().getUid() + "handPassword");
                String fingerPwd = CacheFloder.readPhoneFingerPrint(ACache.get(MyApplication.getInstance()), MyApplication.getInstance().getUid() + "fingerStatus");
                //一秒后判断当前是否登陆   如果登陆 跳转至首页   如果没有登陆  跳转至登陆界面
                if (TextUtils.isEmpty(MyApplication.getInstance().getToken())) {  //没有登陆
                    boolean showGuidePage = (boolean) SPUtils.getProtect(KeyConstants.SHOW_GUIDE_PAGE, false);
                    if (!showGuidePage) {
                        //第一次
                        SPUtils.putProtect(KeyConstants.SHOW_GUIDE_PAGE, true);
                        startActivity(new Intent(WelcomeActivity.this, GuidePageActivity.class));
                        finish();
                    } else {
                        startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                        finish();
                    }
                } else if (!TextUtils.isEmpty(fingerPwd)) {
                    //存在指纹密码
                    startActivity(new Intent(WelcomeActivity.this, PersonalVerifyFingerPrintActivity.class));
                    finish();
                } else if (!TextUtils.isEmpty(pwd)) {
                    //存在手势密码
                    Intent intent = new Intent(WelcomeActivity.this, PersonalVerifyGesturePasswordActivity.class);
                    intent.putExtra(KeyConstants.SOURCE,"WelcomeActivity");
                    startActivity(intent);
                    finish();
                } else {
                 //   startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                    startActivity(mainIntent);
                    MyLog.getInstance().save("................2............");
                    finish();
                }


            }
        }, remainTime);
    }

    @Override
    public void serviceConnectThrowable() {
        String pwd = CacheFloder.readHandPassword(ACache.get(MyApplication.getInstance()), MyApplication.getInstance().getUid() + "handPassword");
        String fingerPwd = CacheFloder.readPhoneFingerPrint(ACache.get(MyApplication.getInstance()), MyApplication.getInstance().getUid() + "fingerStatus");
        //一秒后判断当前是否登陆   如果登陆 跳转至首页   如果没有登陆  跳转至登陆界面
        if (TextUtils.isEmpty(MyApplication.getInstance().getToken())) {  //没有登陆
            boolean showGuidePage = (boolean) SPUtils.getProtect(KeyConstants.SHOW_GUIDE_PAGE, false);
            if (!showGuidePage) {
                //第一次
                SPUtils.putProtect(KeyConstants.SHOW_GUIDE_PAGE, true);
                startActivity(new Intent(WelcomeActivity.this, GuidePageActivity.class));
                finish();
            } else {
                startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                finish();
            }
        } else if (!TextUtils.isEmpty(fingerPwd)) {
            //存在指纹密码
            startActivity(new Intent(WelcomeActivity.this, PersonalVerifyFingerPrintActivity.class));
            finish();
        } else if (!TextUtils.isEmpty(pwd)) {
            //存在手势密码
            Intent intent = new Intent(WelcomeActivity.this, PersonalVerifyGesturePasswordActivity.class);
            intent.putExtra(KeyConstants.SOURCE,"WelcomeActivity");
            startActivity(intent);
            finish();
        } else {
         //   startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
            startActivity(mainIntent);
            MyLog.getInstance().save("................3............");
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyLog.getInstance().save("onResume..............:"+MainActivity.isRunning);
        //启动mqttservice
        Intent intent = new Intent(this, MqttService.class);
        startService(intent);
        //启动bleService
        Intent bleServiceIntent = new Intent(this, BleService.class);
        startService(bleServiceIntent);
        //启动linphoneService
        Intent linphoneServiceIntent = new Intent(this, LinphoneService.class);
        startService(linphoneServiceIntent);
    }
    final int timeout=20;

    public void  executeGeTui(){

        String sip_package_json =getIntent().getStringExtra("stringType");
        //String sip_package_json="eyJmdW5jIjoiY2F0RXllQ2FsbCIsImd3SWQiOiJHVzAxMTgzODEwMTI3IiwiZGV2aWNlSWQiOiJBQzM1RUU4ODQwMUEiLCJkYXRhIjoiSU5WSVRFIHNpcDpBQzM1RUU4ODQwMUFAMTkyLjE2OC4zLjkxIFNJUC8yLjBcclxuVmlhOiBTSVAvMi4wL1VEUCAxOTIuMTY4LjMuOTE6NTA2MDticmFuY2g9ejloRzRiS2E3ZTRkZTJlZTc1MTU5NjU1MDFhNjY2Yzg5NmQ0ODAyO3JlY2VpdmVkPTEyMC43Ni43My4xOTM7cnBvcnQ9NTA2MSxTSVAvMi4wL1VEUCAxOTIuMTY4LjE2OC4xMDQ6NTA2MDtycG9ydD01MDYxO2JyYW5jaD16OWhHNGJLOTg2ODA3MjM2O3JlY2VpdmVkPTEyMC43Ni43My4xOTNcclxuRnJvbTogPHNpcDpBQzM1RUU4ODQwMUFAc2lwLWthYWRhcy5qdXppd3VsaWFuLmNvbT47dGFnPTk1NDE5ODcwXHJcblRvOiA8c2lwOjViZjY1MzY5MzU3MzZmMGFmODQ2N2E0ZkBzaXAta2FhZGFzLmp1eml3dWxpYW4uY29tPlxyXG5DYWxsLUlEOiAxMDM5Nzg4MTkyXHJcbkNTZXE6IDIwIElOVklURVxyXG5Db250ZW50LVR5cGU6IGFwcGxpY2F0aW9uL3NkcFxyXG5BbGxvdzogSU5WSVRFLE9QVElPTlMsSU5GTyxCWUUsQ0FOQ0VMLEFDSyxQUkFDSyxVUERBVEVcclxuTWF4LUZvcndhcmRzOiA2OVxyXG5Vc2VyLUFnZW50OiBPUkFOR0UtSU9UIHYxLjBcclxuU3ViamVjdDogRG9vckJlbGxcclxuU3VwcG9ydGVkOiAxMDByZWxcclxuQ29udGFjdDogPHNpcDpzaXBTZXJ2ZXJAMTIwLjc2LjczLjE5Mzo1MDYxPjtleHBpcmVzPTM2MDBcclxuQ29udGVudC1MZW5ndGg6IDMxMFxyXG5cclxudj0wXHJcbm89am9zdWEgMCAwIElOIElQNCAxOTguMTguNDYuMTVcclxucz1jb252ZXJzYXRpb25cclxuYz1JTiBJUDQgMTk4LjE4LjQ2LjE1XHJcbnQ9MCAwXHJcbm09YXVkaW8gNzA4NiBSVFAvQVZQIDBcclxuYT1ydHBtYXA6MCBQQ01VLzgwMDAvMVxyXG5tPXZpZGVvIDcwODggUlRQL0FWUCA5NlxyXG5hPXJ0cG1hcDo5NiBIMjY0LzkwMDAwXHJcbmE9Zm10cDo5NiBwcm9maWxlLWxldmVsLWlkPTQyMDAxZlxyXG5hPWZtdHA6OTYgcGFja2V0aXphdGlvbi1tb2RlPTFcclxuYT1mbXRwOjk2IHNwcm9wLXBhcmFtZXRlci1zZXRzPVowSUFINTJvUEFSZnViZ0lDQWdRLGFNNDhnQT09XHJcbiJ9";
        long sip_time_json =getIntent().getLongExtra("longType",0);
        //Log.e("denganzhi1","sip_package_json11:"+sip_package_json);

        if(Rom.isOppo()){
            String sip_time_json_str =getIntent().getStringExtra("longType");
            if(!TextUtils.isEmpty(sip_time_json_str)){
                sip_time_json= Long.valueOf(sip_time_json_str);
            }
            Log.e(GeTui.VideoLog,"WelcomeActivity==>oppo===>sip_time_json_str:"+sip_time_json);
        }
        if(!TextUtils.isEmpty(sip_package_json)){
            String sip_package= new String(Base64.decode(sip_package_json,Base64.DEFAULT));
            Log.e(GeTui.VideoLog,"WelcomeActivity==>sip_package_json:"+sip_package);
            if(sip_package.equals("openLock")){
                Log.e(GeTui.VideoLog,"WelcomeActivity======>普通密码开锁");
            }else if(sip_package.equals("alarmOpenLockRisk")){
                Log.e(GeTui.VideoLog,"WelcomeActivity======>胁迫密码开锁");
            }else{
                long diff_time = (System.currentTimeMillis()-sip_time_json)/1000;
                Log.e(GeTui.VideoLog,"WelcomeActivity==>sip_time_json:"+sip_time_json+" diff_time:"+diff_time);
                MyLog.getInstance().save("sip_time_json:"+sip_time_json+" diff_time:"+diff_time);
                if(diff_time>=timeout && sip_time_json!=0){
                    Toast.makeText(WelcomeActivity.this,getResources().getString(R.string.cate_sleep),Toast.LENGTH_LONG).show();
                }else if(diff_time<timeout && sip_time_json!=0){
                    if(!TextUtils.isEmpty(sip_package_json)){
                        //String sip_package= new String(Base64.decode(sip_package_json,Base64.DEFAULT));
                        JSONObject jsonObject = null;
                        String sip_invite=null;
                        String gwid= null;
                        String deviceid=null;
                        try {
                            jsonObject = new JSONObject(sip_package);
                            Log.e(GeTui.VideoLog,"WelcomActivity.."+jsonObject);
                            sip_invite=jsonObject.optString("data");
                            mainIntent.putExtra(Constants.SIP_INVERT_PKG_INTENT,sip_invite);
                            SPUtils.put(Constants.SIP_INVERT_PKG_SP,sip_invite);
                     //       gwid= jsonObject.optString("gwId");
                            deviceid= jsonObject.optString("deviceId");
                            if(TextUtils.isEmpty(deviceid)){
                                String startstr="From: <sip:";
                                int start=sip_invite.indexOf(startstr);
                                //	System.out.println(start);
                                int end=sip_invite.indexOf(">", start);
                                //	System.out.println(end);
                                String[] deviceIdstr=sip_invite.substring(start+startstr.length(), end).split("\\@");
                                deviceid= deviceIdstr[0];
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(GeTui.VideoLog,"WelcomeActivity==>出异常了..........");
                        }

                        if(!TextUtils.isEmpty(sip_invite)){
                          //  String me= (String) SPUtils.get(gwid,"");
                            gwid= (String) SPUtils.get(deviceid,"");
                            String me= (String) SPUtils.get(gwid,"");
                            String meUserName=null;
                            String mePwd=null;
                            if(!TextUtils.isEmpty(me) && me.contains("&")){
                                 meUserName= me.split("&")[0];
                                 mePwd= me.split("&")[1];
                            }
                          //  mainIntent.putExtra(Constants.SIP_INVERT_PKG_INTENT,true);
                            mainIntent.putExtra(Constants.IS_FROM_WEL_INTENT,true);
                            SPUtils.put(Constants.IS_FROM_WEL_SP,true);
                            MyApplication.getInstance().setFromWel(true);
                            MyApplication.getInstance().setSip_package_invite(sip_invite);
                            MyApplication.getInstance().setCurrentGeTuiMimiUserName(meUserName);
                            MyApplication.getInstance().setCurrentGeTuiMImiPwd(mePwd);
                            Log.e(GeTui.VideoLog,"WelcomeActivity==>sip的invite包:"+sip_invite);
                            MyLog.getInstance().save("sip的invite包:"+sip_invite);
                            Log.e(GeTui.VideoLog,"WelcomeActivity==>获取的deviceId:"+deviceid+"  gwid:"+gwid+" meUserName:"+meUserName+" mePwd:"+mePwd);
                            MyLog.getInstance().save("获取的deviceId:"+deviceid+"  gwid:"+gwid+" meUserName:"+meUserName+" mePwd:"+mePwd);
                        }else{
                            Log.e(GeTui.VideoLog,  "WelcomeActivity==>sip package是空");
                        }
                    }
                    //Toast.makeText(WelcomeActivity.this,"收到的是:"+sip_invite,Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
