package com.kaadas.lock.mvp.mvpbase;

import android.Manifest;
import android.content.Context;

import com.kaadas.lock.R;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.GetPasswordResult;
import com.kaadas.lock.utils.PermissionUtil;
import com.kaadas.lock.utils.ToastUtil;

/**
 * Create By lxj  on 2019/3/5
 * Describe
 */
public abstract class BaseBleFragment<T extends IBleView, V extends BlePresenter<T>> extends
        BaseFragment<T,V>  implements IBleView {

    @Override
    public void onBleOpenStateChange(boolean isOpen) {  //锁中蓝牙没有打开  提示用户打开蓝牙
        if(!isOpen){
            ToastUtil.getInstance().showLong(R.string.ble_is_close);
        }
    }

    @Override
    public void onDeviceStateChange(boolean isConnected) {  //设备连接状态改变   连接成功时提示正在鉴权，连接失败时直接提示用户
        if (isConnected) {
            showLoading(getString(R.string.connecting_lock));
        } else {
            hiddenLoading();
            ToastUtil.getInstance().showLong(R.string.connet_failed_please_near);
        }
    }

    @Override
    public void onSearchDeviceFailed(Throwable throwable) {
        ToastUtil.getInstance().showLong(R.string.no_fond_device);
        hiddenLoading();
    }

    @Override
    public void onNeedRebind(int errorCode) {
        ToastUtil.getInstance().showLong(R.string.rebind);
        hiddenLoading();
    }

    @Override
    public void authResult(boolean isSuccess) {
        hiddenLoading();
        if (isSuccess){
            ToastUtil.getInstance().showLong(R.string.device_coneected_can_control_device);
        }else {
            ToastUtil.getInstance().showLong(R.string.rebind);
        }

    }

    @Override
    public void onStartConnectDevice() {

    }

    @Override
    public void onStartSearchDevice() {
        showLoading(getString(R.string.device_disconnect_searchDevice));
    }

    @Override
    public void onEndConnectDevice(boolean isSuccess) {
        hiddenLoading();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onGetPasswordSuccess(GetPasswordResult result) {

    }

    @Override
    public void onGetPasswordFailedServer(BaseResult result) {

    }

    @Override
    public void onGetPasswordFailed(Throwable throwable) {

    }

    @Override
    public void onSearchDeviceSuccess() {

    }

    /**
     * 没有权限
     */
    @Override
    public void noPermissions(){
        PermissionUtil.getInstance().requestPermission(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, getActivity());
        ToastUtil.getInstance().showLong(R.string.please_allow_ble_permission);
    };
}
