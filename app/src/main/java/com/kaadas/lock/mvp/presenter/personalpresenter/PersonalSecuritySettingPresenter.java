package com.kaadas.lock.mvp.presenter.personalpresenter;

import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.utils.cachefloder.ACache;
import com.kaadas.lock.utils.cachefloder.CacheFloder;
import com.kaadas.lock.mvp.view.personalview.IPersonalSecuritySettingView;


public class PersonalSecuritySettingPresenter<T> extends BasePresenter<IPersonalSecuritySettingView> {
    private FingerprintManager mFingerprintManager;

    //设置手势开关状态
    public void setHandPwdSwitchFlag() {

        String pwd = CacheFloder.readHandPassword(ACache.get(MyApplication.getInstance()), MyApplication.getInstance().getUid() + "handPassword");
        if (TextUtils.isEmpty(pwd)) {
            mViewRef.get().closeHandPwdSuccess();
        } else {
            mViewRef.get().openHandPwdSuccess();
        }

    }

    //设置手机指纹开启状态
    public void setFingerPrintFlag() {
        String flag = CacheFloder.readPhoneFingerPrint(ACache.get(MyApplication.getInstance()), MyApplication.getInstance().getUid() + "fingerStatus");
        if (isSafe()) {
            if (TextUtils.isEmpty(flag)) {
                mViewRef.get().closeFingerPrintSuccess();
            } else {

                mViewRef.get().openFingerPrintSuccess();
            }
        }
    }


    //判断是否支持指纹识别
    public Boolean isSupportFinger() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mFingerprintManager = getFingerprintManagerOrNull();
            if (mFingerprintManager != null) {
                Boolean flag = mFingerprintManager.isHardwareDetected();
                if (flag == true) {
                    return true;
                }
            }
        }
        return false;
    }

    //判断是否开启了指纹识别
    public void isOpenFingerPrint() {
        if (mFingerprintManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Boolean openFlag = mFingerprintManager.hasEnrolledFingerprints();
                if (openFlag == true) {
                    if (isSafe()) {
                        mViewRef.get().phoneFigerprintOpen();
                    }
                } else {
                    if (isSafe()) {
                        mViewRef.get().phoneFigerprintClose();
                    }

                }

            }
        }

    }


    /**
     * 获取FingerprintManager
     *
     * @return FingerprintManager
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public FingerprintManager getFingerprintManagerOrNull() {
        if (MyApplication.getInstance().getPackageManager().hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) {
            return MyApplication.getInstance().getSystemService(FingerprintManager.class);
        } else {
            return null;
        }
    }


}






