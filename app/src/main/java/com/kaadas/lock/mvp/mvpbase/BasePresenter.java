package com.kaadas.lock.mvp.mvpbase;

import android.os.Handler;
import android.os.Looper;


import com.kaadas.lock.MyApplication;
import com.kaadas.lock.publiclibrary.ble.BleService;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttService;
import com.kaadas.lock.utils.LogUtils;

import java.lang.ref.WeakReference;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Create By lxj  on 2019/1/7
 * Describe MVP  中的BasePresenter
 */
public class BasePresenter<T extends IBaseView> {
    public Handler handler = new Handler(Looper.getMainLooper());
    protected CompositeDisposable compositeDisposable = new CompositeDisposable();
    public MqttService mqttService;
    protected BleService bleService = MyApplication.getInstance().getBleService();

    public BasePresenter() {
        if (mqttService == null) {
            LogUtils.e("mqttService  为空   异常情况  " + (MyApplication.getInstance().getMqttService() == null));
            mqttService = MyApplication.getInstance().getMqttService();
        }
        if (bleService == null ) {
            LogUtils.e("bleService  为空   异常情况  " + (MyApplication.getInstance().getBleService() == null));
            bleService = MyApplication.getInstance().getBleService();
        }
    }

    /**
     * 弱引用
     * T Activity 中UI操作接口
     */
    protected WeakReference<T> mViewRef;
    protected boolean isAttach = false;

    public void attachView(T view) {
        if (bleService == null) {
            bleService = MyApplication.getInstance().getBleService();
        }
        mViewRef = new WeakReference<T>(view);
        isAttach = true;
    }

    public void detachView() {
        if (mViewRef != null) {
            mViewRef.clear();
            mViewRef = null;
        }
        if (compositeDisposable != null) {
            compositeDisposable.clear();
        }
        isAttach = false;
    }

    public void toDisposable(Disposable disposable) {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }


}
