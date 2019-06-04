package com.kaadas.lock.mvp.presenter.gatewaypresenter;

import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.mvpbase.IBaseView;
import com.kaadas.lock.mvp.view.gatewayView.GatewaySettingView;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.MqttCommandFactory;
import com.kaadas.lock.publiclibrary.mqtt.MqttReturnCodeError;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.GetNetBasicBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.GetZbChannelBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.SetNetBasicBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.SetWiFiBasic;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.SetZBChannel;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.UnBindGatewayBean;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.GwWiFiBaseInfo;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;
import com.kaadas.lock.utils.LogUtils;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public class GatewaySettingPresenter<T> extends BasePresenter<GatewaySettingView> {
    private Disposable getNetBasicDisposable;
    private Disposable getWiFiBasicDisposable;
    private Disposable getZbChannelDisposable;
    private Disposable unbindGatewayDisposable;
    private Disposable wifiSettingDisposable;
    private Disposable setNetBasicDisposable;
    private Disposable setZBChannelDisposable;

    // 获取网络设置基本信息
    public void getNetBasic(String uid,String gatewayId,String deviceId){
        MqttMessage netBasic = MqttCommandFactory.getNetBasic(uid, gatewayId, deviceId);
        if (mqttService != null) {
            toDisposable(getNetBasicDisposable);
            getNetBasicDisposable = mqttService.mqttPublish(MqttConstant.getCallTopic(MyApplication.getInstance().getUid()), netBasic)
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if (MqttConstant.GET_NET_BASIC.equals(mqttData.getFunc())){
                                return true;
                            }else{
                                return false;
                            }
                        }
                    })
                    .timeout(10 * 1000, TimeUnit.MILLISECONDS)
                    .compose(RxjavaHelper.observeOnMainThread())
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            if ("200".equals(mqttData.getReturnCode())){ //请求成功
                                GetNetBasicBean netBasicBean = new Gson().fromJson(mqttData.getPayload(), GetNetBasicBean.class);
                                if (mViewRef.get()!=null){
                                    mViewRef.get().getNetBasicSuccess(netBasicBean);
                                }
                            }else {
                                if (mViewRef.get()!=null){
                                    mViewRef.get().getNetBasicFail();
                                }
                            }
                            toDisposable(getNetBasicDisposable);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (mViewRef.get()!=null){
                                mViewRef.get().getNetBasicThrowable(throwable);
                            }
                        }
                    });
            compositeDisposable.add(getNetBasicDisposable);

        }
    }


    //获取网关的wifi名称和网关的wifi密码
    public void getGatewayWifiPwd(String gwId) {
        MqttMessage wiFiBasic = MqttCommandFactory.getWiFiBasic(MyApplication.getInstance().getUid(), gwId, gwId);
        if (mqttService != null) {
            toDisposable(getWiFiBasicDisposable);
            getWiFiBasicDisposable = mqttService.mqttPublish(MqttConstant.getCallTopic(MyApplication.getInstance().getUid()), wiFiBasic)
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            LogUtils.e("获取到的数据的messageId是   " + mqttData.getMessageId() + "   发送的messageId是  " + wiFiBasic.getId());
                            return mqttData.isThisRequest(wiFiBasic.getId(),   MqttConstant.GET_WIFI_BASIC );
                        }
                    })
                    .timeout(10 * 1000, TimeUnit.MILLISECONDS)
                    .compose(RxjavaHelper.observeOnMainThread())
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
                                    mViewRef.get().onGetWifiInfoFailed();
                                }
                            }
                            toDisposable(getWiFiBasicDisposable);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (mViewRef.get()!=null){
                                mViewRef.get().onGetWifiInfoThrowable(throwable);
                            }
                        }
                    });
            compositeDisposable.add(getWiFiBasicDisposable);

        }
    }

    //网关协调器信道获取
    public void getZbChannel(String uid,String gatewayId,String deviceId){
        MqttMessage zbChannel = MqttCommandFactory.getZbChannel(uid,gatewayId, deviceId);
        if (mqttService != null) {
            toDisposable(getZbChannelDisposable);
            getZbChannelDisposable = mqttService.mqttPublish(MqttConstant.getCallTopic(MyApplication.getInstance().getUid()), zbChannel)
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if (mqttData.getFunc().equals(MqttConstant.GET_ZB_Channel)){
                                return true;
                            }else{
                                return false;
                            }
                        }
                    })
                    .timeout(10 * 1000, TimeUnit.MILLISECONDS)
                    .compose(RxjavaHelper.observeOnMainThread())
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            if ("200".equals(mqttData.getReturnCode() )   ){ //请求成功
                                GetZbChannelBean getZbChannelBean = new Gson().fromJson(mqttData.getPayload(), GetZbChannelBean.class);
                                if (mViewRef.get()!=null){
                                    mViewRef.get().getZbChannelSuccess(getZbChannelBean);
                                }
                            }else {
                                if (mViewRef.get()!=null){
                                    mViewRef.get().getZbChannelFail();
                                }
                            }
                            toDisposable(getZbChannelDisposable);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (mViewRef.get()!=null){
                                mViewRef.get().getZbChannelThrowable(throwable);
                            }
                        }
                    });
            compositeDisposable.add(getZbChannelDisposable);
        }
    }


    //解绑网关
    public void  unBindGateway(String uid,String gatewayId){
        if (mqttService!=null){
            toDisposable(unbindGatewayDisposable);
            unbindGatewayDisposable=mqttService.mqttPublish(MqttConstant.MQTT_REQUEST_APP, MqttCommandFactory.unBindGateway(uid,gatewayId))
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if (MqttConstant.UNBIND_GATEWAY.equals(mqttData.getFunc())){
                                return true;
                            }
                            return false;
                        }
                    })
                    .timeout(10*1000, TimeUnit.MILLISECONDS)
                    .compose(RxjavaHelper.observeOnMainThread())
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            toDisposable(unbindGatewayDisposable);
                            UnBindGatewayBean unBindGatewayBean=new Gson().fromJson(mqttData.getPayload(),UnBindGatewayBean.class);
                            if ("200".equals(unBindGatewayBean.getCode())){
                                if (mViewRef.get()!=null){
                                    mViewRef.get().unbindGatewaySuccess();
                                    MyApplication.getInstance().getAllDevicesByMqtt(true);
                                }
                            }else{
                                if (mViewRef.get()!=null){
                                    mViewRef.get().unbindGatewayFail();
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (mViewRef.get()!=null){
                                mViewRef.get().unbindGatewayThrowable(throwable);
                            }
                        }
                    });
            compositeDisposable.add(unbindGatewayDisposable);
        }
    }

    //设置wifi名称
    public void setWiFi(String uid,String gatewayId,String deviceId,String encryption,String name,String pwd){
        if (mqttService!=null){
            toDisposable(wifiSettingDisposable);
            wifiSettingDisposable=mqttService.mqttPublish(MqttConstant.getCallTopic(uid), MqttCommandFactory.setWiFiBasic(uid,gatewayId,deviceId,name,pwd,encryption))
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if (MqttConstant.SET_WIFI_BASIC.equals(mqttData.getFunc())){
                                return true;
                            }
                            return false;
                        }
                    })
                    .timeout(10*1000, TimeUnit.MILLISECONDS)
                    .compose(RxjavaHelper.observeOnMainThread())
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            toDisposable(wifiSettingDisposable);
                            SetWiFiBasic setWiFiBasic=new Gson().fromJson(mqttData.getPayload(),SetWiFiBasic.class);
                            if ("200".equals(setWiFiBasic.getReturnCode())){
                                if (mViewRef.get()!=null){
                                    mViewRef.get().setWifiSuccess(setWiFiBasic.getParams().getSsid(),setWiFiBasic.getParams().getPwd());

                                }
                            }else{
                                if (mViewRef.get()!=null){
                                    mViewRef.get().setWifiFail();
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (mViewRef.get()!=null){
                                mViewRef.get().setWifiThrowable(throwable);
                            }
                        }
                    });
            compositeDisposable.add(wifiSettingDisposable);
        }
    }

    //配置局域网
    public void setNetBasic(String uid,String gatewayId,String deviceId,String lanIp,String lanNetmask){
        if (mqttService!=null){
            toDisposable(setNetBasicDisposable);
            setNetBasicDisposable=mqttService.mqttPublish(MqttConstant.getCallTopic(uid), MqttCommandFactory.setNetBasic(uid,gatewayId,deviceId,lanIp,lanNetmask))
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if (MqttConstant.SET_NET_BASIC.equals(mqttData.getFunc())){
                                return true;
                            }
                            return false;
                        }
                    })
                    .compose(RxjavaHelper.observeOnMainThread())
                    .timeout(10*1000, TimeUnit.MILLISECONDS)
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            toDisposable(setNetBasicDisposable);
                            SetNetBasicBean setWiFiBasic=new Gson().fromJson(mqttData.getPayload(),SetNetBasicBean.class);
                            if ("200".equals(setWiFiBasic.getReturnCode())){
                                if (mViewRef.get()!=null){
                                    mViewRef.get().setNetLanSuccess(setWiFiBasic.getParams().getLanIp(),setWiFiBasic.getParams().getLanNetmask());

                                }
                            }else{
                                if (mViewRef.get()!=null){
                                    mViewRef.get().setNetLanFail();
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (mViewRef.get()!=null){
                                mViewRef.get().setNetLanThrowable(throwable);
                            }
                        }
                    });
            compositeDisposable.add(setNetBasicDisposable);
        }
    }

    //配置局域网
    public void setZbChannel(String uid,String gatewayId,String deviceId,String channel){
        if (mqttService!=null){
            toDisposable(setZBChannelDisposable);
            setZBChannelDisposable=mqttService.mqttPublish(MqttConstant.getCallTopic(uid), MqttCommandFactory.setZBChannel(uid,gatewayId,deviceId,channel))
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if (MqttConstant.SET_ZB_CHANNEL.equals(mqttData.getFunc())){
                                return true;
                            }
                            return false;
                        }
                    })
                    .compose(RxjavaHelper.observeOnMainThread())
                    .timeout(10*1000, TimeUnit.MILLISECONDS)
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            toDisposable(setZBChannelDisposable);
                            SetZBChannel setZBChannel=new Gson().fromJson(mqttData.getPayload(),SetZBChannel.class);
                            if ("200".equals(setZBChannel.getReturnCode())){
                                if (mViewRef.get()!=null){
                                    mViewRef.get().setZbChannelSuccess(setZBChannel.getParams().getChannel());
                                }
                            }else{
                                if (mViewRef.get()!=null){
                                    mViewRef.get().setZbChannelFail();
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (mViewRef.get()!=null){
                                mViewRef.get().setZbChannelThrowable(throwable);
                            }
                        }
                    });
            compositeDisposable.add(setZBChannelDisposable);
        }
    }





}
