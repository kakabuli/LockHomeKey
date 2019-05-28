package com.kaadas.lock.mvp.presenter.gatewaypresenter;

import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.bean.GatewayDeviceDetailBean;
import com.kaadas.lock.bean.HomeShowBean;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.gatewayView.GatewayView;
import com.kaadas.lock.publiclibrary.bean.CateEyeInfo;
import com.kaadas.lock.publiclibrary.bean.GwLockInfo;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.MqttCommandFactory;
import com.kaadas.lock.publiclibrary.mqtt.eventbean.DeviceOnLineBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.GetDevicePowerBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.UnBindGatewayBean;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.GetBindGatewayStatusResult;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttService;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.networkListenerutil.NetWorkChangReceiver;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public class GatewayPresenter<T> extends BasePresenter<GatewayView> {
   private List<HomeShowBean> gatewayBindList=new ArrayList<>();
   private Disposable getPowerDataDisposable;
   private Disposable listenerGatewayOnLine;
   private Disposable listenerDeviceOnLineDisposable;
   private Disposable unbindGatewayDisposable;
   private Disposable unbindTestGatewayDisposable;
   private Disposable networkChangeDisposable;

    //遍历绑定的网关设备
    public List<HomeShowBean> getGatewayBindList(String gatewayID){
      List<HomeShowBean> homeShowBeans= MyApplication.getInstance().getAllDevices();
      for (HomeShowBean homeShowBean:homeShowBeans) {
          if (homeShowBean.getDeviceType() == HomeShowBean.TYPE_GATEWAY_LOCK) {
              GwLockInfo gwLockInfo = (GwLockInfo) homeShowBean.getObject();
              if (gwLockInfo.getGwID().equals(gatewayID)) {
                  gatewayBindList.add(homeShowBean);
              }
          } else if (homeShowBean.getDeviceType() == HomeShowBean.TYPE_CAT_EYE) {
              CateEyeInfo cateEyeInfo = (CateEyeInfo) homeShowBean.getObject();
              if (cateEyeInfo.getGwID().equals(gatewayID)) {
                  gatewayBindList.add(homeShowBean);
              }
          }
      }
      return gatewayBindList;
    }

    //监听电量情况
    public void getPowerData(String gatewayId){
        LogUtils.e("进入获取电量。。。");
        if (mqttService!=null){
            getPowerDataDisposable=mqttService.getPowerData()
                    .filter(new Predicate<MqttData>() {
                @Override
                public boolean test(MqttData mqttData) throws Exception {
                    if (mqttData!=null){
                        //过滤
                        LogUtils.e("过滤电量的值");
                        GetDevicePowerBean powerBean = new Gson().fromJson(mqttData.getPayload(), GetDevicePowerBean.class);
                        if (gatewayId.equals(powerBean.getGwId())){
                            LogUtils.e("过滤成功值");

                            return true;
                        }
                    }
                    return false;
                }
            })
              .compose(RxjavaHelper.observeOnMainThread())
              .subscribe(new Consumer<MqttData>() {
                  @Override
                  public void accept(MqttData mqttData) throws Exception {
                      GetDevicePowerBean powerBean = new Gson().fromJson(mqttData.getPayload(), GetDevicePowerBean.class);
                      if ("200".equals(mqttData.getReturnCode())){
                            if (mViewRef.get()!=null){
                                mViewRef.get().getPowerDataSuccess(powerBean.getDeviceId(),powerBean.getReturnData().getPower());
                            }
                      }else{
                          if (mViewRef.get()!=null){
                              mViewRef.get().getPowerDataFail(powerBean.getGwId(),powerBean.getDeviceId());
                          }
                      }
                  }
              }, new Consumer<Throwable>() {
                  @Override
                  public void accept(Throwable throwable) throws Exception {
                      if (mViewRef.get()!=null){
                          mViewRef.get().getPowerThrowable();
                      }
                  }
              });
            compositeDisposable.add(getPowerDataDisposable);
        }
    }



    //获取网关状态通知
    public void getPublishNotify() {
        if (mqttService != null) {
            toDisposable(listenerGatewayOnLine);
            listenerGatewayOnLine = mqttService.listenerNotifyData()
                    .compose(RxjavaHelper.observeOnMainThread())
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            if (mqttData != null) {
                                GetBindGatewayStatusResult gatewayStatusResult = new Gson().fromJson(mqttData.getPayload(), GetBindGatewayStatusResult.class);
                                LogUtils.e("监听网关GatewayActivity" + gatewayStatusResult.getDevuuid());
                                if (gatewayStatusResult != null&&gatewayStatusResult.getData().getState()!=null) {
                                    if (mViewRef.get() != null) {
                                        mViewRef.get().gatewayStatusChange(gatewayStatusResult.getDevuuid(),gatewayStatusResult.getData().getState());
                                    }
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            //网关状态发生异常
                        }
                    });
            compositeDisposable.add(listenerGatewayOnLine);
        }
    }

    /**
     * 监听设备上线下线
     */
    public void listenerDeviceOnline() {
        if (mqttService != null) {
            toDisposable(listenerDeviceOnLineDisposable);
            listenerDeviceOnLineDisposable = mqttService.listenerDataBack()
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            return mqttData.getFunc().equals(MqttConstant.GW_EVENT);
                        }
                    })
                    .compose(RxjavaHelper.observeOnMainThread())
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            DeviceOnLineBean deviceOnLineBean = new Gson().fromJson(mqttData.getPayload(), DeviceOnLineBean.class);
                            if (deviceOnLineBean!=null){
                                if (mViewRef.get()!=null&&deviceOnLineBean.getEventparams().getEvent_str()!=null){
                                    mViewRef.get().deviceStatusChange(deviceOnLineBean.getGwId(),deviceOnLineBean.getDeviceId(),deviceOnLineBean.getEventparams().getEvent_str());
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                        }
                    });
            compositeDisposable.add(listenerDeviceOnLineDisposable);
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
                                    .compose(RxjavaHelper.observeOnMainThread())
                                    .timeout(10*1000, TimeUnit.MILLISECONDS)
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

    //测试解绑网关
    public void testUnbindGateway(String uid,String gatewayId,String devuuid){
        if (mqttService!=null){
            toDisposable(unbindTestGatewayDisposable);
            unbindTestGatewayDisposable=mqttService.mqttPublish(MqttConstant.MQTT_REQUEST_APP,MqttCommandFactory.unBindTestGateway(uid,gatewayId,devuuid))
                                        .filter(new Predicate<MqttData>() {
                                            @Override
                                            public boolean test(MqttData mqttData) throws Exception {
                                                if (MqttConstant.UNBIND_TEST_GATEWAY.equals(mqttData.getFunc())){
                                                    return true;
                                                }
                                                return false;
                                            }
                                        })
                                        .compose(RxjavaHelper.observeOnMainThread())
                                        .timeout(10*1000,TimeUnit.MILLISECONDS)
                                        .subscribe(new Consumer<MqttData>() {
                                            @Override
                                            public void accept(MqttData mqttData) throws Exception {
                                                toDisposable(unbindTestGatewayDisposable);
                                                UnBindGatewayBean unBindGatewayBean=new Gson().fromJson(mqttData.getPayload(),UnBindGatewayBean.class);
                                                if ("200".equals(unBindGatewayBean.getCode())){
                                                    if (mViewRef.get()!=null){
                                                        mViewRef.get().unbindTestGatewaySuccess();
                                                        MyApplication.getInstance().getAllDevicesByMqtt(true);
                                                    }
                                                }else{
                                                    if (mViewRef.get()!=null){
                                                        mViewRef.get().unbindTestGatewayFail();
                                                    }
                                                }
                                            }
                                        }, new Consumer<Throwable>() {
                                            @Override
                                            public void accept(Throwable throwable) throws Exception {
                                                    if (mViewRef.get()!=null){
                                                        mViewRef.get().unbindTestGatewayThrowable(throwable);
                                                    }
                                            }
                                        });

        }
        compositeDisposable.add(unbindTestGatewayDisposable);
    }

    //网络变化通知
    public void listenerNetworkChange(){
        toDisposable(networkChangeDisposable);
        networkChangeDisposable= NetWorkChangReceiver.notifyNetworkChange().subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if (aBoolean){
                    if (mViewRef!=null&&mViewRef.get()!=null){
                        mViewRef.get().networkChangeSuccess();
                    }
                }
            }
        });
        compositeDisposable.add(networkChangeDisposable);
    }




}
