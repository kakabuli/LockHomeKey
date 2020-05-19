package com.kaadas.lock.mvp.view.gatewaylockview;

import com.kaadas.lock.utils.greenDao.bean.GatewayPasswordPlanBean;

import java.util.List;

public interface IGatewayLockPasswordManagerView extends IGatewayLockPasswordView  {
    /**
     * 正在同步密码计划
     */
    void isSyncPlan();


}
