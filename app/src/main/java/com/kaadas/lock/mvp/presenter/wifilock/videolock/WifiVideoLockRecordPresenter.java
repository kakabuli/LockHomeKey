package com.kaadas.lock.mvp.presenter.wifilock.videolock;

import android.view.SurfaceView;

import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockVideoCallingView;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockVideoRecordView;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.publiclibrary.xm.XMP2PManager;
import com.kaadas.lock.publiclibrary.xm.bean.DeviceInfo;
import com.kaadas.lock.utils.LogUtils;
import com.xm.sdk.struct.stream.AVStreamHeader;
import com.xmitech.sdk.AudioFrame;
import com.xmitech.sdk.H264Frame;
import com.xmitech.sdk.MP4Info;
import com.xmitech.sdk.interfaces.AVFilterListener;
import com.xmitech.sdk.interfaces.VideoPackagedListener;

import io.reactivex.disposables.Disposable;

public class WifiVideoLockRecordPresenter<T> extends BasePresenter<IWifiLockVideoRecordView>  {

    @Override
    public void attachView(IWifiLockVideoRecordView view) {
        super.attachView(view);

    }


    @Override
    public void detachView() {
        super.detachView();
    }



}
