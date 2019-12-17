package com.kaadas.lock.mvp.presenter.gatewaylockpresenter;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.view.gatewaylockview.IGatewayLockPasswordManagerView;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.greenDao.bean.GatewayPasswordPlanBean;

import java.util.ArrayList;
import java.util.List;

public class GatewayLockPasswordManagerPresenter<T> extends GatewayLockPasswordPresenter<IGatewayLockPasswordManagerView> {


    @Override
    void onSyncComplete(String gatewayId, String deviceId, int currentIndex, int pwdId, String pwdValue,
                        int pwdType, long zLocalEndT, long zLocalStartT, int dayMaskBits,
                        int endHour, int endMinute, int startHour, int startMinute) {
        planPasswordIndex.clear();
        for (Integer key : pwds.keySet()) {
            if (pwds.get(key) != 0) {
                planPasswordIndex.add(key);
            }
        }
        LogUtils.e("策略密码的个数是   " + planPasswordIndex.size());
        if (planPasswordIndex.size() > 0) {
            getPlan(deviceId, gatewayId, planPasswordIndex.get(0), planPasswordIndex.get(0), "year");
            if (isSafe()) {
                mViewRef.get().isSyncPlan();
            }
        } else {
            List<GatewayPasswordPlanBean> list = new ArrayList<>();
            for (Integer key : passwordPlanBeans.keySet()) {
                list.add(passwordPlanBeans.get(key));
            }
            managerDao.insertAfterDelete(deviceId, MyApplication.getInstance().getUid(), gatewayId, list);
        }
    }
}
