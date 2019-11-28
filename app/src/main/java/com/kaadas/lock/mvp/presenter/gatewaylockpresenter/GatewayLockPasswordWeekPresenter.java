package com.kaadas.lock.mvp.presenter.gatewaylockpresenter;

import com.kaadas.lock.mvp.view.gatewaylockview.IGatewayLockPasswordWeekView;

public class GatewayLockPasswordWeekPresenter<T> extends GatewayLockPasswordPresenter<IGatewayLockPasswordWeekView> {


    @Override
    void onSyncComplete(String gatewayId, String deviceId, int currentIndex,
                        int pwdId, String pwdValue, int pwdType, long zLocalEndT, long zLocalStartT,
                        int dayMaskBits, int endHour, int endMinute, int startHour, int startMinute) {
        int number = getNumber(NORMAL_PASSWORD);
        if (number == -1){
            if (isSafe()){
                mViewRef.get().gatewayPasswordFull();
            }
            return;
        }


        addLockPwd(gatewayId, deviceId, number, pwdValue, pwdType,
                zLocalEndT, zLocalStartT, dayMaskBits, endHour, endMinute,
                startHour, startMinute);
    }
}
