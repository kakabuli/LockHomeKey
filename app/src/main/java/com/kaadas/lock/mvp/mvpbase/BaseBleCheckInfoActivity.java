package com.kaadas.lock.mvp.mvpbase;

import android.content.Intent;
import android.os.Bundle;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.publiclibrary.bean.BleLockInfo;
import com.kaadas.lock.publiclibrary.http.result.CheckOTAResult;
import com.kaadas.lock.publiclibrary.ota.ble.OtaConstants;
import com.kaadas.lock.publiclibrary.ota.ble.p6.P6OtaUpgradeActivity;
import com.kaadas.lock.publiclibrary.ota.ble.ti.Ti2FileOtaUpgradeActivity;
import com.kaadas.lock.publiclibrary.ota.ble.ti.TiOtaUpgradeActivity;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.SPUtils;
import com.blankj.utilcode.util.ToastUtils;

public abstract class BaseBleCheckInfoActivity<T extends ICheckOtaView, V extends BleCheckOTAPresenter<T>> extends BaseBleActivity<T, V> implements ICheckOtaView {

    private BleLockInfo bleLockInfo;
    protected boolean isEnterOta = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bleLockInfo = mPresenter.getBleLockInfo();
    }

    @Override
    public void noNeedUpdate() {
        hiddenLoading();
        //当前已是最新版本
        AlertDialogUtil.getInstance().noEditSingleButtonDialog(this, getString(R.string.hint)
                , getString(R.string.already_newest_version), getString(R.string.confirm), new AlertDialogUtil.ClickListener() {
                    @Override
                    public void left() {

                    }

                    @Override
                    public void right() {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(String toString) {

                    }
                });
    }

    @Override
    public void snError() {
        hiddenLoading();
        ToastUtils.showLong(getString(R.string.sn_error));
    }

    @Override
    protected void onResume() {
        super.onResume();
        isEnterOta = false;
    }

    @Override
    public void dataError() {
        hiddenLoading();
        ToastUtils.showLong(getString(R.string.data_params_error));
    }

    @Override
    public void needUpdate(CheckOTAResult.UpdateFileInfo appInfo, String SN, String version, int type) {
        hiddenLoading();
        LogUtils.e("只有一个固件需要升级");
        if (type == 1) {
            if (bleLockInfo.getBleType() == 1) { //Ti升级

            } else if (bleLockInfo.getBleType() == 2) {  //P6升级

            } else {
                ToastUtils.showLong(getString(R.string.check_update_failed2));
                return;
            }
        }
        String content = getString(R.string.check_new_version);
        if (type == 1) { //蓝牙
            content = getString(R.string.hava_ble_new_version);
        } else if (type == 2) {  // 算法版
            content = getString(R.string.hava_algorithm_new_version);
        } else if (type == 3) { // 摄像头
            content = getString(R.string.hava_camera_new_version);
        }

        AlertDialogUtil.getInstance().noEditTwoButtonDialog(this, getString(R.string.hint)
                , content, getString(R.string.cancel), getString(R.string.confirm), new AlertDialogUtil.ClickListener() {
                    @Override
                    public void left() {

                    }

                    @Override
                    public void right() {
                        if (type == 1) {
                            if (bleLockInfo.getBattery() != -1 && bleLockInfo.getBattery() < 20) {
                                ToastUtils.showLong(R.string.low_power_warring);
                                return;
                            }
                            SPUtils.put(KeyConstants.DEVICE_SN + bleLockInfo.getServerLockInfo().getMacLock(), SN);    //Key
                            SPUtils.put(KeyConstants.BLE_VERSION + bleLockInfo.getServerLockInfo().getMacLock(), version); //Key
                            LogUtils.e("升级的版本信息是   " + SN + "   下载链接是   " + appInfo.getFileUrl());
                            MyApplication.getInstance().getBleService().release();  //进入ota模式之前  需要断开连接
                            isEnterOta = true;
                            Intent intent = new Intent();
                            intent.putExtra(OtaConstants.bindUrl, appInfo.getFileUrl());
                            intent.putExtra(OtaConstants.deviceMac, bleLockInfo.getServerLockInfo().getMacLock()); //升级
                            intent.putExtra(OtaConstants.password1, bleLockInfo.getServerLockInfo().getPassword1());
                            intent.putExtra(OtaConstants.password2, bleLockInfo.getServerLockInfo().getPassword2());
                            intent.putExtra(OtaConstants.version, appInfo.getFileVersion());
                            intent.putExtra(OtaConstants.SN, bleLockInfo.getServerLockInfo().getDeviceSN());
                            if (bleLockInfo.getBleType() == 1) { //Ti升级
                                intent.putExtra(OtaConstants.fileName, "Kaadas_ble" + appInfo.getFileVersion() + "_" + appInfo.getFileMd5() + ".bin");
                                intent.setClass(BaseBleCheckInfoActivity.this, TiOtaUpgradeActivity.class);
                            } else if (bleLockInfo.getBleType() == 2) {  //P6升级
                                intent.putExtra(OtaConstants.fileName, "Kaadas_ble" + appInfo.getFileVersion() + "_" + appInfo.getFileMd5() + ".cyacd2");
                                intent.setClass(BaseBleCheckInfoActivity.this, P6OtaUpgradeActivity.class);
                            }
                            onEnterOta();
                            startActivity(intent);
                        } else if (type == 2 || type == 3) {  // 算法版
                            on3DModuleEnterOta(type, appInfo);
//                            Intent intent = new Intent();
//                            intent.putExtra(OtaConstants.bindUrl, appInfo.getFileUrl());
//                            if (type == 2) {
//                                intent.putExtra(OtaConstants.fileName, "Kaadas_algorithm" + appInfo.getFileVersion() + "_" + appInfo.getFileMd5() + ".bin");
//                            } else if (type == 3) { // 摄像头
//                                intent.putExtra(OtaConstants.fileName, "Kaadas_camera" + appInfo.getFileVersion() + "_" + appInfo.getFileMd5() + ".bin");
//                            }
//                            intent.setClass(BaseBleCheckInfoActivity.this, FaceOtaActivity.class);
//                            startActivity(intent);
                        }
                    }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(String toString) {

                    }
                }
        );
    }

    @Override
    public void needTwoUpdate(CheckOTAResult.UpdateFileInfo stackInfo, CheckOTAResult.UpdateFileInfo appInfo, String SN, String version) {
        hiddenLoading();
        LogUtils.e("有两个固件需要升级");
        if (bleLockInfo.getBleType() != 1) { //Ti升级
            ToastUtils.showLong(getString(R.string.check_update_failed2) + bleLockInfo.getBleType());
            return;
        }
        AlertDialogUtil.getInstance().noEditTwoButtonDialog(this, getString(R.string.hint)
                , getString(R.string.hava_ble_new_version), getString(R.string.cancel), getString(R.string.confirm), new AlertDialogUtil.ClickListener() {
                    @Override
                    public void left() {

                    }

                    @Override
                    public void right() {
                        if (bleLockInfo.getBattery() != -1 && bleLockInfo.getBattery() < 20) {
                            ToastUtils.showLong(R.string.low_power_warring);
                            return;
                        }
                        isEnterOta = true;
                        SPUtils.put(KeyConstants.DEVICE_SN + bleLockInfo.getServerLockInfo().getMacLock(), SN);    //Key
                        SPUtils.put(KeyConstants.BLE_VERSION + bleLockInfo.getServerLockInfo().getMacLock(), version); //Key
                        LogUtils.e("升级的版本信息是   " + SN + "   下载链接是   " + appInfo.getFileUrl());
                        MyApplication.getInstance().getBleService().release();  //进入ota模式之前  需要断开连接
                        Intent intent = new Intent();
                        intent.putExtra(OtaConstants.bindUrl, stackInfo.getFileUrl());
                        intent.putExtra(OtaConstants.bindUrl2, appInfo.getFileUrl());
                        intent.putExtra(OtaConstants.deviceMac, bleLockInfo.getServerLockInfo().getMacLock()); //升级
                        intent.putExtra(OtaConstants.password1, bleLockInfo.getServerLockInfo().getPassword1());
                        intent.putExtra(OtaConstants.password2, bleLockInfo.getServerLockInfo().getPassword2());
                        intent.putExtra(OtaConstants.version, appInfo.getFileVersion());
                        intent.putExtra(OtaConstants.SN, bleLockInfo.getServerLockInfo().getDeviceSN());
                        intent.putExtra(OtaConstants.fileName, "Kaadas_" + stackInfo.getFileVersion() + "_" + stackInfo.getFileMd5() + ".bin");
                        intent.putExtra(OtaConstants.fileName2, "Kaadas_" + appInfo.getFileVersion() + "_" + appInfo.getFileMd5() + ".bin");
                        intent.setClass(BaseBleCheckInfoActivity.this, Ti2FileOtaUpgradeActivity.class);
                        onEnterOta();
                        startActivity(intent);
                    }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(String toString) {

                    }

                }

        );
    }

    @Override
    public void readInfoFailed(Throwable throwable) {
        ToastUtils.showLong(getString(R.string.check_update_failed));
        hiddenLoading();
    }

    @Override
    public void unknowError(String errorCode) {
        ToastUtils.showLong(R.string.unknown_error);
        hiddenLoading();
    }

    @Override
    public void onDeviceStateChange(boolean isConnected) {  //设备连接状态改变   连接成功时提示正在鉴权，连接失败时直接提示用户

    }


    public void onEnterOta() {

    }

    public void on3DModuleEnterOta(int type,CheckOTAResult.UpdateFileInfo appInfo){

    }

}
