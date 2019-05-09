package com.kaadas.lock.mvp.presenter.gatewaypresenter;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.bean.GatewayDeviceDetailBean;
import com.kaadas.lock.bean.HomeShowBean;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.gatewayView.GatewayView;
import com.kaadas.lock.publiclibrary.bean.CateEyeInfo;
import com.kaadas.lock.publiclibrary.bean.GwLockInfo;


import java.util.ArrayList;
import java.util.List;

public class GatewayPresenter<T> extends BasePresenter<GatewayView> {
   private List<HomeShowBean> gatewayBindList=new ArrayList<>();
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

}
