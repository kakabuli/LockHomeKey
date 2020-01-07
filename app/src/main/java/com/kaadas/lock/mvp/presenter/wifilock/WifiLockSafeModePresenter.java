package com.kaadas.lock.mvp.presenter.wifilock;

import android.text.TextUtils;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockSafeModeView;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.utils.LogUtils;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class WifiLockSafeModePresenter<T> extends BasePresenter<IWifiLockSafeModeView> {
    private String WiFiSn;
    private Disposable listenActionUpdateDisposable;

    public void init(String wiFiSn){
        WiFiSn = wiFiSn;
        listenActionUpdate();
    }

    private void listenActionUpdate(){
        toDisposable(listenActionUpdateDisposable);
        listenActionUpdateDisposable = MyApplication.getInstance().listenerWifiLockAction()
                  .subscribe(new Consumer<WifiLockInfo>() {
                      @Override
                      public void accept(WifiLockInfo wifiLockInfo) throws Exception {
                          if (wifiLockInfo!=null &&!TextUtils.isEmpty( wifiLockInfo.getWifiSN())){
                              if ( wifiLockInfo.getWifiSN().equals(WiFiSn) && isSafe()){
                                  mViewRef.get().onWifiLockActionUpdate();
                              }
                          }

                      }
                  });

        compositeDisposable.add(listenActionUpdateDisposable);

    }

}
