package com.kaadas.lock.publiclibrary.ota.ble;

import android.text.TextUtils;

import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.mvpbase.BleCheckOTAPresenter;
import com.kaadas.lock.mvp.mvpbase.BlePresenter;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.result.OTAResult;
import com.kaadas.lock.utils.LogUtils;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class OtaPresenter<T> extends BleCheckOTAPresenter<IOtaView> {
    @Override
    public void authSuccess() {

    }
}
