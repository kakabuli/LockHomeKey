package com.kaadas.lock.mvp.presenter.deviceaddpresenter;

import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.deviceaddview.WiFiLockChooseToAddView;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.WiFiLockUtils;
import io.reactivex.disposables.Disposable;


public class WiFiLockChooseToAddPresenter<T> extends BasePresenter<WiFiLockChooseToAddView> {

    private Disposable chooseToAddDisposable;

    public void searchLockProduct(String Product) {

        LogUtils.e("--kaadas--WiFiLockUtils.pairMode(Product)=="+WiFiLockUtils.pairMode(Product));

        //equalsIgnoreCase不区分大小写
        if (WiFiLockUtils.pairMode(Product).equals("WiFi")) {
        //searchLockProductSuccessForWiFi
            mViewRef.get().searchLockProductSuccessForWiFi("WiFi");
        }
        else if(WiFiLockUtils.pairMode(Product).equals("WiFi&BLE")){
        //searchLockProductSuccessForWiFiAndBLE
            mViewRef.get().searchLockProductSuccessForWiFiAndBLE("WiFi&BLE");
        }
        else {
            //searchLockProductThrowable
            mViewRef.get().searchLockProductThrowable();
        }
    }
}
