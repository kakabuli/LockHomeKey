package com.kaadas.lock.mvp.presenter;


import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.BluetoothSharedDeviceBean;
import com.kaadas.lock.publiclibrary.http.util.BaseObserver;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.mvp.view.IBluetoothSharedDeviceManagementView;


import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class BluetoothSharedDeviceManagementPresenter<T> extends BasePresenter<IBluetoothSharedDeviceManagementView> {

    /**
     * 查询用户列表
     */
    public void queryUserList(String user_id, String devname) {

        XiaokaiNewServiceImp.getDeviceGeneralAdministrator(user_id, devname).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onNext(String s) {
                Gson gson = new Gson();
                BluetoothSharedDeviceBean bluetoothSharedDeviceBean = gson.fromJson(s, BluetoothSharedDeviceBean.class);
                if (bluetoothSharedDeviceBean.isSuccess()) { //如果请求成功
                    if (mViewRef != null) {
                        mViewRef.get().queryCommonUserListSuccess(bluetoothSharedDeviceBean);
                    }
                } else {
                    if ("444".equals(bluetoothSharedDeviceBean.getCode())) { //Token过期
                        LogUtils.e("token过期   " + Thread.currentThread().getName());
                        //退出mqtt
                        if (mqttService!=null){
                            mqttService.httpMqttDisconnect();
                        }
                        MyApplication.getInstance().tokenInvalid(true);
                    } else {
                        if (mViewRef != null) {
                            mViewRef.get().queryCommonUserListFail(bluetoothSharedDeviceBean);
                        }
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                if (mViewRef != null) {
                    mViewRef.get().queryCommonUserListError(e);
                }
            }

            @Override
            public void onComplete() {

            }
        });
    }

    /**
     * 添加用户
     */
    public void addCommonUser(String admin_id, String device_username, String devicemac, String devname, String end_time, String lockNickName,
                              String open_purview, String start_time, List<String> items) {

        XiaokaiNewServiceImp.addUser(admin_id, device_username, devicemac, devname, end_time, lockNickName,
                open_purview, start_time, items).subscribe(new BaseObserver<BaseResult>() {
            @Override
            public void onSuccess(BaseResult baseResult) {
                if (mViewRef != null) {
                    mViewRef.get().addCommonUserSuccess(baseResult);
                }
            }

            @Override
            public void onAckErrorCode(BaseResult baseResult) {
                if (mViewRef != null) {
                    mViewRef.get().addCommonUserFail(baseResult);
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                if (mViewRef != null) {
                    mViewRef.get().addCommonUserError(throwable);
                }
            }

            @Override
            public void onSubscribe1(Disposable d) {
                compositeDisposable.add(d);
            }
        });
    }


}
