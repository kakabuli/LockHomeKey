package com.kaadas.lock.mvp.presenter.deviceaddpresenter;


import com.kaadas.lock.MyApplication;
import com.kaadas.lock.bean.HomeShowBean;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.deviceaddview.DeviceZigBeeDetailView;

import java.util.ArrayList;
import java.util.List;



public class DeviceZigBeeDetailPresenter<T> extends BasePresenter<DeviceZigBeeDetailView> {
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


}
