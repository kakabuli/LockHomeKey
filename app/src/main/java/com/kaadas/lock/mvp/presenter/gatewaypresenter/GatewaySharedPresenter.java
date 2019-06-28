package com.kaadas.lock.mvp.presenter.gatewaypresenter;


import com.google.gson.Gson;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.gatewayView.IGatewaySharedView;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.MqttCommandFactory;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.DeviceShareResultBean;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.DeviceShareUserResultBean;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;
import com.kaadas.lock.utils.KeyConstants;

import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public class GatewaySharedPresenter<T> extends BasePresenter<IGatewaySharedView> {
    private Disposable shareDisposable;
    private Disposable shareUserDispoable;


 //添加分享用户,删除分享用户
    public void  shareDevice(int type,String gatewayId,String deviceId,String uid,String shareUser,String userName,int shareFlag){
        if (mqttService!=null){
            toDisposable(shareDisposable);
            shareDisposable= mqttService.mqttPublish(MqttConstant.PUBLISH_TO_SERVER, MqttCommandFactory.shareDevice(type,gatewayId,deviceId,uid,shareUser,userName,shareFlag))
                       .filter(new Predicate<MqttData>() {
                           @Override
                           public boolean test(MqttData mqttData) throws Exception {
                               if (mqttData.getFunc().equals(MqttConstant.SHARE_DEVICE)){
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
                              toDisposable(shareDisposable);
                              DeviceShareResultBean shareResultBean=new Gson().fromJson(mqttData.getPayload(),DeviceShareResultBean.class);
                              if ("200".equals(shareResultBean.getCode())){
                                   if (mViewRef!=null&&mViewRef.get()!=null&&gatewayId.equals(shareResultBean.getGwId())&&deviceId.equals(shareResultBean.getDeviceId())){
                                       mViewRef.get().shareDeviceSuccess(shareResultBean);
                                   }
                              }else{
                                  if (mViewRef!=null&&mViewRef.get()!=null){
                                      mViewRef.get().shareDeviceFail();
                                  }
                              }

                          }
                      }, new Consumer<Throwable>() {
                          @Override
                          public void accept(Throwable throwable) throws Exception {
                              if (mViewRef!=null&&mViewRef.get()!=null){
                                 mViewRef.get().shareDeviceThrowable();
                              }
                          }
                      });
            compositeDisposable.add(shareDisposable);
        }
    }

    //查找分享用户
    public void getShareDeviceUser(String gatewayId,String deviceId,String uid){
        if (mqttService!=null){
            toDisposable(shareUserDispoable);
            shareUserDispoable=mqttService.mqttPublish(MqttConstant.PUBLISH_TO_SERVER,MqttCommandFactory.getShareUser(gatewayId,deviceId,uid))
                               .filter(new Predicate<MqttData>() {
                                   @Override
                                   public boolean test(MqttData mqttData) throws Exception {
                                       if (mqttData.getFunc().equals(MqttConstant.SHARE_USER_LIST)){
                                           return true;
                                       }
                                       return false;
                                   }
                               })
                               .timeout(10*1000,TimeUnit.MILLISECONDS)
                               .compose(RxjavaHelper.observeOnMainThread())
                               .subscribe(new Consumer<MqttData>() {
                                   @Override
                                   public void accept(MqttData mqttData) throws Exception {
                                       toDisposable(shareUserDispoable);
                                       DeviceShareUserResultBean deviceShareUserResultBean=new Gson().fromJson(mqttData.getPayload(),DeviceShareUserResultBean.class);
                                       if (deviceShareUserResultBean.getDeviceId().equals(deviceId)&&deviceShareUserResultBean.getGwId().equals(gatewayId)&&"200".equals(deviceShareUserResultBean.getCode())){
                                            if (mViewRef!=null&&mViewRef.get()!=null){
                                                mViewRef.get().shareUserListSuccess(deviceShareUserResultBean.getData());
                                            }
                                       }else{
                                           if (mViewRef!=null&&mViewRef.get()!=null){
                                               mViewRef.get().shareUserListFail();
                                           }
                                       }

                                   }
                               }, new Consumer<Throwable>() {
                                   @Override
                                   public void accept(Throwable throwable) throws Exception {
                                       if (mViewRef!=null&&mViewRef.get()!=null){
                                           mViewRef.get().shareUserListThrowable();
                                       }
                                   }
                               });
            compositeDisposable.add(shareUserDispoable);
        }

    }


}
