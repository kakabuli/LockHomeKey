package com.kaadas.lock.activity.login;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import com.kaadas.lock.MainActivity;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.my.PersonalUpdateVerifyGesturePwd;
import com.kaadas.lock.base.mvpbase.BaseActivity;
import com.kaadas.lock.bean.VersionBean;
import com.kaadas.lock.presenter.SplashPresenter;
import com.kaadas.lock.utils.CheckLanguageUtil;
import com.kaadas.lock.utils.NetUtil;
import com.kaadas.lock.utils.ToastUtil;
import com.kaadas.lock.utils.cachefloder.ACache;
import com.kaadas.lock.utils.cachefloder.CacheFloder;
import com.kaadas.lock.view.ISplashView;


public class SplashActivity extends BaseActivity<ISplashView, SplashPresenter<ISplashView>> implements ISplashView {
    Handler handler = new Handler();
    private PackageInfo packageInfo;
    private int mVersionCode;
    private String mVersionName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        CheckLanguageUtil.getInstance().checkLag();//语言
        initData();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String pwd = CacheFloder.readHandPassword(ACache.get(MyApplication.getInstance()), MyApplication.getInstance().getUid() + "handPassword");
                String fingerPwd = CacheFloder.readPhoneFingerPrint(ACache.get(MyApplication.getInstance()), MyApplication.getInstance().getUid() + "fingerStatus");
                //一秒后判断当前是否登陆   如果登陆 跳转至首页   如果没有登陆  跳转至登陆界面
                if (TextUtils.isEmpty(MyApplication.getInstance().getToken())) {  //没有登陆
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                } else if (!TextUtils.isEmpty(fingerPwd)) {
                    //存在指纹密码
                    startActivity(new Intent(SplashActivity.this, PersonalVerifyFingerPrintActivity.class));
                    finish();
                } else if (!TextUtils.isEmpty(pwd)) {
                    //存在手势密码
                    startActivity(new Intent(SplashActivity.this, PersonalVerifyGesturePasswordActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }

            }
        }, 3 * 1000);
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
}
