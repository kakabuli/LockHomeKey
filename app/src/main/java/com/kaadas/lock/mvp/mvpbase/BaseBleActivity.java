package com.kaadas.lock.mvp.mvpbase;

import android.Manifest;

import com.kaadas.lock.R;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.GetPasswordResult;
import com.kaadas.lock.utils.PermissionUtil;
import com.blankj.utilcode.util.ToastUtils;

/**
 * Create By lxj  on 2019/3/5
 * Describe
 */
public abstract class BaseBleActivity<T extends IBleView, V extends BlePresenter<T>> extends
        BaseActivity<T,V>  implements IBleView {


    @Override
    public void onBleOpenStateChange(boolean isOpen) {  //锁中蓝牙没有打开  提示用户打开蓝牙
        if (!isOpen){
            ToastUtils.showLong(R.string.ble_is_close);
        }
    }

    @Override
    public void onDeviceStateChange(boolean isConnected) {  //设备连接状态改变   连接成功时提示正在鉴权，连接失败时直接提示用户
        if (isConnected) {
            showLoading(getString(R.string.is_authing));
        } else {
            hiddenLoading();
            ToastUtils.showLong(R.string.connet_failed_please_near);
        }
    }

    @Override
    public void onSearchDeviceFailed(Throwable throwable) {
        ToastUtils.showLong(R.string.no_fond_device);
        hiddenLoading();
    }

    @Override
    public void onNeedRebind(int errorCode) {
        ToastUtils.showLong(R.string.rebind);
        hiddenLoading();
    }

    @Override
    public void authResult(boolean isSuccess) {
        hiddenLoading();
        if (isSuccess){
            ToastUtils.showLong(R.string.device_coneected_can_control_device);
        }else {
            ToastUtils.showLong(R.string.rebind);
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
        PermissionUtil.getInstance().requestPermission(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, this);
        ToastUtils.showLong(R.string.please_allow_ble_permission);
    }

    /**
     * 没有打开GPS 提示
     */
    @Override
    public void noOpenGps(){
        ToastUtils.showLong(R.string.check_phone_not_open_gps_please_open);
    }


//////////////////////////////////***************          *************************//////////////////////////////////////////
    /**
     * 非管理员用户必须联网才能开锁
     */
    public void notAdminMustHaveNet(){

    };


    /**
     * 输入密码
     */
    public void inputPwd() {}

    /**
     * 鉴权失败
     */
    public void authServerFailed(BaseResult baseResult){}

    /**
     * 开锁失败
     */
    public void openLockFailed(Throwable throwable){}

    /**
     * 开锁成功
     */
    public void openLockSuccess(){}

    /**
     * 锁关闭
     */
    public void onLockLock(){}


    /**
     * 鉴权失败
     */
    public void authFailed(Throwable throwable){}


    /**
     * 正在开锁
     */
    public  void isOpeningLock(){}
}
