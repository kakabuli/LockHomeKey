package com.kaadas.lock.mvp.presenter.deviceaddpresenter;


import com.kaadas.lock.MyApplication;
import com.kaadas.lock.bean.HomeShowBean;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.deviceaddview.DeviceZigBeeDetailView;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.AllBindDevices;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class DeviceZigBeeDetailPresenter<T> extends BasePresenter<DeviceZigBeeDetailView> {
    private Disposable listenerAllDevicesDisposable;

    @Override
    public void attachView(DeviceZigBeeDetailView view) {
        super.attachView(view);
        toDisposable(listenerAllDevicesDisposable);
        listenerAllDevicesDisposable = MyApplication.getInstance().listenerAllDevices()
                .subscribe(new Consumer<AllBindDevices>() {
                    @Override
                    public void accept(AllBindDevices allBindDevices) throws Exception {
                        if (isSafe()) {
                            mViewRef.get().onDeviceRefresh(allBindDevices);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        compositeDisposable.add(listenerAllDevicesDisposable);

    }

    private ArrayList<HomeShowBean> showBeansList = new ArrayList<>();

    //获取绑定的网关列表
    public List<HomeShowBean> getGatewayBindList() {
        List<HomeShowBean> homeShowBeans = MyApplication.getInstance().getAllDevices();
        if (homeShowBeans != null && homeShowBeans.size() > 0) {
            for (HomeShowBean showBean : homeShowBeans) {
                if (showBean.getDeviceType() == HomeShowBean.TYPE_GATEWAY) {
                    if (showBeansList != null) {
                        showBeansList.add(showBean);
                    }
                }
            }
        }
        return showBeansList;
    }


}
