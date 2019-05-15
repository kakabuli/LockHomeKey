package com.kaadas.lock.mvp.presenter.deviceaddpresenter;

import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.bean.HomeShowBean;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;

import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.MqttCommandFactory;
import com.kaadas.lock.publiclibrary.mqtt.MqttReturnCodeError;

import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.GetBindGatewayStatusResult;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.GwWiFiBaseInfo;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;
import com.kaadas.lock.mvp.view.deviceaddview.DeviceGatewayBindListView;
import com.kaadas.lock.utils.LogUtils;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public class DeviceGatewayBindListPresenter<T> extends BasePresenter<DeviceGatewayBindListView> {

    private Disposable disposable;
    private Disposable getWiFiBasicDisposable;
    private ArrayList<HomeShowBean> showBeansList=new ArrayList<>();
    //获取绑定的网关列表
    public List<HomeShowBean> getGatewayBindList(){
        List<HomeShowBean> homeShowBeans=MyApplication.getInstance().getAllDevices();
        for (HomeShowBean showBean:homeShowBeans){
            if (showBean.getDeviceType()==HomeShowBean.TYPE_GATEWAY){
                if (showBeansList!=null){
                    showBeansList.add(showBean);
                }
            }
        }
        return showBeansList;


    }

    //获取通知
    public void getGatewayState() {
        if (mqttService!=null) {
            disposable = mqttService.listenerNotifyData()
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            if (mqttData != null) {
                                if (MqttConstant.GATEWAY_STATE.equals(mqttData.getFunc())) {
                                    GetBindGatewayStatusResult gatewayStatusResult = new Gson().fromJson(mqttData.getPayload(), GetBindGatewayStatusResult.class);
                                    if (gatewayStatusResult != null) {
                                        if (mViewRef.get() != null) {
                                            mViewRef.get().getGatewayStateSuccess(gatewayStatusResult.getDevuuid(), gatewayStatusResult.getData().getState());
                                        }
                                    }
                                }
                            }
                        }
                    });
            compositeDisposable.add(disposable);
        }
    }

    //获取网关的wifi名称和网关的wifi密码
    public void getGatewayWifiPwd(String gwId) {
        MqttMessage wiFiBasic = MqttCommandFactory.getWiFiBasic(MyApplication.getInstance().getUid(), gwId, gwId);
        if (mqttService != null) {
            getWiFiBasicDisposable = mqttService.mqttPublish(MqttConstant.getCallTopic(MyApplication.getInstance().getUid()), wiFiBasic)
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            LogUtils.e("获取到的数据的messageId是   " + mqttData.getMessageId() + "   发送的messageId是  " + wiFiBasic.getId());
                            return mqttData.isThisRequest(wiFiBasic.getId(),   MqttConstant.GET_WIFI_BASIC );
                        }
                    })
                    .compose(RxjavaHelper.observeOnMainThread())
                    .timeout(10 * 1000, TimeUnit.MILLISECONDS)
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            if ("200".equals(mqttData.getReturnCode() )   ){ //请求成功
                                GwWiFiBaseInfo wiFiBaseInfo = new Gson().fromJson(mqttData.getPayload(), GwWiFiBaseInfo.class);
                                if (mViewRef.get()!=null){
                                    mViewRef.get().onGetWifiInfoSuccess(wiFiBaseInfo);
                                }
                            }else {
                                if (mViewRef.get()!=null){
                                    mViewRef.get().onGetWifiInfoFailed(new MqttReturnCodeError(mqttData.getReturnCode()));
                                }
                            }
                            toDisposable(getWiFiBasicDisposable);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (mViewRef.get()!=null){
                                mViewRef.get().onGetWifiInfoFailed(throwable);
                            }
                        }
                    });
            compositeDisposable.add(getWiFiBasicDisposable);

        }
    }

    //获取


}
