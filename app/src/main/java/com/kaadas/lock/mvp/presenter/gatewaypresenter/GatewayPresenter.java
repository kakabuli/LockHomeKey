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
import com.kaadas.lock.publiclibrary.mqtt.publishbean.GetDevicePowerBean;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttService;
import com.kaadas.lock.utils.LogUtils;


import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public class GatewayPresenter<T> extends BasePresenter<GatewayView> {
   private List<HomeShowBean> gatewayBindList=new ArrayList<>();
   private Disposable getPowerDataDisposable;

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
                              mViewRef.get().getPowerDataFail();
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

}
