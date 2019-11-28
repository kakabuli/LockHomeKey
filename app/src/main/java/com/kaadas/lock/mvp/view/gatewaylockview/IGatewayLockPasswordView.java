package com.kaadas.lock.mvp.view.gatewaylockview;

import com.kaadas.lock.mvp.mvpbase.IBaseView;
import com.kaadas.lock.utils.greenDao.bean.GatewayPasswordPlanBean;


import java.util.Map;

public interface IGatewayLockPasswordView extends IBaseView {

    /**
     * 获取设备信息成功
     * @param maxPwd 最大密码个数
     */
    void getLockInfoSuccess(int maxPwd);
    /**
     * 获取设备信息失败
     * @param
     */
    void getLockInfoFail();
    /**
     * 获取设备信息失败
     * @param throwable
     */
    void getLockInfoThrowable(Throwable throwable);

    /**
     * 同步密码完成
     */
    void syncPasswordComplete(Map<Integer, GatewayPasswordPlanBean> passwordPlanBeans);

    /**
     * 获取秘钥策略
     */
    void onLoadPasswordPlan(Map<Integer, GatewayPasswordPlanBean> passwordPlanBeans);

    /**
     * 获取秘钥策略
     */
    void onLoadPasswordPlanFailed( Throwable throwable);
    /**
     * 获取秘钥策略
     */
    void onLoadPasswordPlanComplete(Map<Integer, GatewayPasswordPlanBean> passwordPlanBeans);
    /**
     * 同步密码失败
     */
    void syncPasswordFailed(Throwable throwable);

    //添加密码异常
    void addLockPwdFail(Throwable throwable);

    //添加密码成功
    void addLockPwdSuccess(GatewayPasswordPlanBean gatewayPasswordPlanBean,String pwdValue);

    /**
     * 设置用户类型成功
     */
    void setUserTypeSuccess(String passwordValue, GatewayPasswordPlanBean gatewayPasswordPlanBean);
    /**
     * 设置用户类型成功
     */
    void setUserTypeFailed(Throwable typeFailed);

    /**
     * 设置用户计划成功
     */
    void setPlanSuccess(String passwordValue,GatewayPasswordPlanBean gatewayPasswordPlanBean);

    /**
     * 设置用户计划失败
     */
    void setPlanFailed(Throwable throwable);

    /**
     * 删除密码成功
     */
    void deletePasswordSuccess();

    /**
     * 删除密码失败
     * @param throwable
     */
    void deletePasswordFailed(Throwable throwable);



}
