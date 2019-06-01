package com.kaadas.lock.mvp.presenter.gatewaylockpresenter;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.gatewaylockview.GatewayLockAlramView;
import com.kaadas.lock.publiclibrary.mqtt.eventbean.GatewayLockAlarmEventBean;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.greenDao.bean.GatewayLockAlarmEventDao;
import com.kaadas.lock.utils.greenDao.db.CatEyeEventDao;
import com.kaadas.lock.utils.greenDao.db.GatewayLockAlarmEventDaoDao;

import java.util.ArrayList;
import java.util.List;

public class GatewayLockAlarmPresenter<T> extends BasePresenter<GatewayLockAlramView> {
    private List<GatewayLockAlarmEventDao> gatewayLockAlarmList=new ArrayList<>();
    //获取数据库的门锁报警信息
    public void getLockAlarm(int page,int pageNum,String gatewayId,String deviceId){
        LogUtils.e("访问数据库的门锁信息");
        List<GatewayLockAlarmEventDao> alarmEventBeansList = MyApplication.getInstance().getDaoWriteSession().queryBuilder(GatewayLockAlarmEventDao.class).orderDesc(GatewayLockAlarmEventDaoDao.Properties.TimeStamp).offset(page * pageNum).limit(pageNum).list();
        if(alarmEventBeansList!=null&&alarmEventBeansList.size()>0){
                for (GatewayLockAlarmEventDao gatewayLockAlarmEventDao:alarmEventBeansList) {
                    if (gatewayId.equals(gatewayLockAlarmEventDao.getGatewayId())&&deviceId.equals(gatewayLockAlarmEventDao.getDeviceId())){
                        gatewayLockAlarmList.add(gatewayLockAlarmEventDao);
                    }
                }
                if (mViewRef!=null&&mViewRef.get()!=null){
                    mViewRef.get().getLockAlarmSuccess(gatewayLockAlarmList);
                    gatewayLockAlarmList.clear();
                }
        }else{
            if (mViewRef!=null&&mViewRef.get()!=null){
                mViewRef.get().getLockAlarmFail();
                if (gatewayLockAlarmList!=null) {
                    gatewayLockAlarmList.clear();
                }
            }
        }
    }
}
