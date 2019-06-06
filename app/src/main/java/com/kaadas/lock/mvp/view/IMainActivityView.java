package com.kaadas.lock.mvp.view;

import com.kaadas.lock.mvp.mvpbase.IBleView;
import com.kaadas.lock.publiclibrary.bean.BleLockInfo;
import com.kaadas.lock.publiclibrary.bean.CateEyeInfo;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;


/**
 * Create By lxj  on 2019/3/18
 * Describe
 */
public interface IMainActivityView extends IBleView {

    void onWarringUp(String warringContent);

    void onDeviceInBoot(BleLockInfo bleLockInfo);

    /**
     * 猫眼呼叫进来  且米米网登录成功
     */
    void onCatEyeCallIn(CateEyeInfo cateEyeInfo);


    /**
     * 网关上报事件
     * @param eventType   事件类型
     * @param deviceId      设备Id
     */
    void onGwEvent(int eventType,String deviceId,String gatewayId);


    void uploadpush(BaseResult baseResult);

    void onGwLockEvent(int alarmCode,int clusterID,String deviceId,String gatewayId);


    void callError();

}
