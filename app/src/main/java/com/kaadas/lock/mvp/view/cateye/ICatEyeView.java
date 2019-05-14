package com.kaadas.lock.mvp.view.cateye;

import com.kaadas.lock.mvp.mvpbase.IBaseView;
import com.kaadas.lock.utils.greenDao.bean.CatEyeEvent;

import java.util.List;

public interface ICatEyeView extends IBaseView {
    //监听网关上下线
    void gatewayStatusChange(String gatewayId,String eventStr);

    //监听设备上下线
    void deviceStatusChange(String gatewayId,String deviceId,String eventStr);

    //网关断开的变化
    void networkChangeSuccess();
}
