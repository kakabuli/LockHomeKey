package com.kaadas.lock.mvp.view.gatewaylockview;

import com.kaadas.lock.mvp.mvpbase.IBaseView;
import com.kaadas.lock.utils.greenDao.bean.GatewayLockAlarmEventDao;

import java.util.List;

public interface GatewayLockAlramView extends IBaseView {

    //读取数据库的预警表成功
    void getLockAlarmSuccess(List<GatewayLockAlarmEventDao> alarmEventDaoList);

    //读取数据库的预警表失败
    void getLockAlarmFail();


}
