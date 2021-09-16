package com.kaadas.lock.mvp.presenter.wifilock;

import android.text.TextUtils;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockMoreView;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.postbean.MultiOTABean;
import com.kaadas.lock.publiclibrary.http.postbean.UpgradeMultiOTABean;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.CheckOTAResult;
import com.kaadas.lock.publiclibrary.http.result.MultiCheckOTAResult;
import com.kaadas.lock.publiclibrary.http.result.WifiLockVideoBindResult;
import com.kaadas.lock.publiclibrary.http.util.BaseObserver;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.SPUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class WifiLockMorePresenter<T> extends BasePresenter<IWifiLockMoreView> {

    private Disposable listenActionUpdateDisposable;
    private String wifiSN;
    private Disposable otaDisposable;

    public void init(String wifiSn) {
        wifiSN = wifiSn;
        listenActionUpdate();
    }

    public void setNickName(String wifiSN, String lockNickname) {
        XiaokaiNewServiceImp.wifiLockUpdateNickname(wifiSN, MyApplication.getInstance().getUid(), lockNickname)
                .subscribe(new BaseObserver<BaseResult>() {
                    @Override
                    public void onSuccess(BaseResult baseResult) {
                        MyApplication.getInstance().getWifiLockInfoBySn(wifiSN)
                                .setLockNickname(lockNickname);
                        if (isSafe()) {
                            mViewRef.get().modifyDeviceNicknameSuccess();
                        }
                    }

                    @Override
                    public void onAckErrorCode(BaseResult baseResult) {
                        if (isSafe()) {
                            mViewRef.get().modifyDeviceNicknameFail(baseResult);
                        }
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        if (isSafe()) {
                            mViewRef.get().modifyDeviceNicknameError(throwable);
                        }
                    }

                    @Override
                    public void onSubscribe1(Disposable d) {
                        compositeDisposable.add(d);
                    }
                })
        ;
    }

    public void deleteVideDevice(String wifiSn){
        XiaokaiNewServiceImp.wifiVideoLockUnbind(wifiSn, MyApplication.getInstance().getUid())
                .subscribe(new BaseObserver<WifiLockVideoBindResult>() {
            @Override
            public void onSuccess(WifiLockVideoBindResult wifiLockVideoBindResult) {
                MyApplication.getInstance().getAllDevicesByMqtt(true);
                SPUtils.remove(KeyConstants.WIFI_LOCK_ALARM_RECORD + wifiSn);
                SPUtils.remove(KeyConstants.WIFI_LOCK_OPERATION_RECORD + wifiSn);
                SPUtils.remove(KeyConstants.WIFI_LOCK_OPEN_COUNT + wifiSn);
                SPUtils.remove(KeyConstants.WIFI_LOCK_SHARE_USER_LIST + wifiSn);
                SPUtils.remove(KeyConstants.WIFI_LOCK_PASSWORD_LIST + wifiSn);
                SPUtils.remove(KeyConstants.WIFI_VIDEO_LOCK_VISITOR_RECORD + wifiSn);
                if (isSafe()) {
                    mViewRef.get().onDeleteDeviceSuccess();
                }
            }

            @Override
            public void onAckErrorCode(BaseResult baseResult) {
                if (isSafe()) {
                    mViewRef.get().onDeleteDeviceFailedServer(baseResult);
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                if (isSafe()) {
                    mViewRef.get().onDeleteDeviceFailed(throwable);
                }
            }

            @Override
            public void onSubscribe1(Disposable d) {
                compositeDisposable.add(d);
            }
        });
    }

    public void deleteDevice(String wifiSn) {
        XiaokaiNewServiceImp.wifiLockUnbind(wifiSn, MyApplication.getInstance().getUid())
                .subscribe(new BaseObserver<BaseResult>() {
                    @Override
                    public void onSuccess(BaseResult baseResult) {
                        MyApplication.getInstance().getAllDevicesByMqtt(true);
                        SPUtils.remove(KeyConstants.WIFI_LOCK_ALARM_RECORD + wifiSn);
                        SPUtils.remove(KeyConstants.WIFI_LOCK_OPERATION_RECORD + wifiSn);
                        SPUtils.remove(KeyConstants.WIFI_LOCK_OPEN_COUNT + wifiSn);
                        SPUtils.remove(KeyConstants.WIFI_LOCK_SHARE_USER_LIST + wifiSn);
                        SPUtils.remove(KeyConstants.WIFI_LOCK_PASSWORD_LIST + wifiSn);
                        if (isSafe()) {
                            mViewRef.get().onDeleteDeviceSuccess();
                        }
                    }

                    @Override
                    public void onAckErrorCode(BaseResult baseResult) {
                        if (isSafe()) {
                            mViewRef.get().onDeleteDeviceFailedServer(baseResult);
                        }
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        if (isSafe()) {
                            mViewRef.get().onDeleteDeviceFailed(throwable);
                        }
                    }

                    @Override
                    public void onSubscribe1(Disposable d) {
                        compositeDisposable.add(d);
                    }
                });
    }

    public void updateSwitchStatus(int switchStatus, String wifiSn) {
        XiaokaiNewServiceImp.wifiLockUpdatePush(wifiSn, MyApplication.getInstance().getUid(), switchStatus)
                .subscribe(new BaseObserver<BaseResult>() {
                    @Override
                    public void onSuccess(BaseResult baseResult) {
                        MyApplication.getInstance().getWifiLockInfoBySn(wifiSn).setPushSwitch(switchStatus);
                        if (isSafe()) {
                            mViewRef.get().onUpdatePushStatusSuccess(switchStatus);
                        }
                    }

                    @Override
                    public void onAckErrorCode(BaseResult baseResult) {
                        if (isSafe()) {
                            mViewRef.get().onUpdatePushStatusFailed(baseResult);
                        }
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        if (isSafe()) {
                            mViewRef.get().onUpdatePushStatusThrowable(throwable);
                        }
                    }

                    @Override
                    public void onSubscribe1(Disposable d) {
                        compositeDisposable.add(d);
                    }
                });
    }

    private void listenActionUpdate() {
        toDisposable(listenActionUpdateDisposable);
        listenActionUpdateDisposable = MyApplication.getInstance().listenerWifiLockAction()
                .subscribe(new Consumer<WifiLockInfo>() {
                    @Override
                    public void accept(WifiLockInfo wifiLockInfo) throws Exception {
                        if (wifiLockInfo != null && !TextUtils.isEmpty(wifiLockInfo.getWifiSN())) {
                            if (wifiLockInfo.getWifiSN().equals(wifiSN) && isSafe()) {
                                mViewRef.get().onWifiLockActionUpdate();
                            }
                        }

                    }
                });

        compositeDisposable.add(listenActionUpdateDisposable);
    }

    public void checkMultiOTAInfo(String wifiSN,String frontPanelVersion,String backPanelVersion) {
        List<MultiOTABean.OTAParams> params = new ArrayList<>();
        params.add(new MultiOTABean.OTAParams(6,frontPanelVersion));
        params.add(new MultiOTABean.OTAParams(7,backPanelVersion));

        XiaokaiNewServiceImp.getOtaMultiInfo(1,wifiSN,params)
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new BaseObserver<MultiCheckOTAResult>() {
                    @Override
                    public void onSuccess(MultiCheckOTAResult multiCheckOTAResult) {
                        if("200".equals(multiCheckOTAResult.getCode() + "")){
                            if(isSafe()){
                                mViewRef.get().needMultiUpdate(multiCheckOTAResult.getData().getUpgradeTask());
                            }
                        }else if ("401".equals(multiCheckOTAResult.getCode())) { // 数据参数不对
                            if (isSafe()) {
                                mViewRef.get().dataError();
                            }
                        } else if ("102".equals(multiCheckOTAResult.getCode())) { //SN格式不对
                            if (isSafe()) {
                                mViewRef.get().snError();
                            }
                        }else if("210".equals(multiCheckOTAResult.getCode() + "")){
                            if(isSafe()){
                                mViewRef.get().noNeedUpdate();
                            }
                        }else{
                            if (isSafe()) {
                                mViewRef.get().unknowError(multiCheckOTAResult.getCode());
                            }
                        }
                    }

                    @Override
                    public void onAckErrorCode(BaseResult baseResult) {
                        if("210".equals(baseResult.getCode() + "")){
                            if(isSafe()){
                                mViewRef.get().noNeedUpdate();
                            }
                        }else{
                            mViewRef.get().unknowError(baseResult.getCode());
                        }
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        if (isSafe()) {
                            mViewRef.get().readInfoFailed(throwable);
                        }
                    }

                    @Override
                    public void onSubscribe1(Disposable d) {

                    }
                });
    }


    /**
     * @param SN
     * @param version
     * @param type    1模块  2锁
     */
    public void checkOtaInfo(String SN, String version, int type) {
        //请求成功
        otaDisposable = XiaokaiNewServiceImp.getOtaInfo(1, SN, version, type)
                .subscribe(new Consumer<CheckOTAResult>() {
                    @Override
                    public void accept(CheckOTAResult otaResult) throws Exception {
                        LogUtils.e("检查OTA升级数据   " + otaResult.toString());
                        //200  成功  401  数据参数不对  102 SN格式不对  210 查无结果
                        if ("200".equals(otaResult.getCode())) {
                            if (isSafe()) {
                                CheckOTAResult.UpdateFileInfo data = otaResult.getData();
                                mViewRef.get().needUpdate(data, SN, type);
                            }
                        } else if ("401".equals(otaResult.getCode())) { // 数据参数不对
                            if (isSafe()) {
                                mViewRef.get().dataError();
                            }
                        } else if ("102".equals(otaResult.getCode())) { //SN格式不对
                            if (isSafe()) {
                                mViewRef.get().snError();
                            }
                        } else if ("210".equals(otaResult.getCode())) { // 查无结果
                            if (isSafe()) {
                                mViewRef.get().noNeedUpdate();
                            }
                        } else {
                            if (isSafe()) {
                                mViewRef.get().unknowError(otaResult.getCode());
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (isSafe()) {
                            mViewRef.get().readInfoFailed(throwable);
                        }
                        LogUtils.e("检查OTA升级数据 失败  " + throwable.getMessage());
                    }
                });
        compositeDisposable.add(otaDisposable);
    }

    public void updateOTA(String wifiSN,List<MultiCheckOTAResult.UpgradeTask> upgradeTasks) {
        List<UpgradeMultiOTABean.UpgradeTaskBean> data = new ArrayList<>();
        for(int i = 0;i < upgradeTasks.size();i++){
            data.add(new UpgradeMultiOTABean.UpgradeTaskBean(upgradeTasks.get(i).getDevNum(),upgradeTasks.get(i).getFileLen(),
                    upgradeTasks.get(i).getFileUrl(),upgradeTasks.get(i).getFileMd5(),upgradeTasks.get(i).getFileVersion()));
        }
        XiaokaiNewServiceImp.wifiDeviceUploadMultiOta(wifiSN,data)
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new BaseObserver<BaseResult>() {
                    @Override
                    public void onSuccess(BaseResult baseResult) {

                    }

                    @Override
                    public void onAckErrorCode(BaseResult baseResult) {
                        if (isSafe()) {
                            mViewRef.get().uploadFailed();
                        }
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        if (isSafe()) {
                            mViewRef.get().uploadFailed();
                        }
                    }

                    @Override
                    public void onSubscribe1(Disposable d) {

                    }
                });
    }


    public void uploadOta(CheckOTAResult.UpdateFileInfo updateFileInfo, String wifiSN) {
        XiaokaiNewServiceImp.wifiLockUploadOta(updateFileInfo, wifiSN)
                .subscribe(new BaseObserver<BaseResult>() {
                    @Override
                    public void onSuccess(BaseResult result) {
                        if (isSafe()) {
                            mViewRef.get().uploadSuccess(updateFileInfo.getDevNum());
                        }
                    }

                    @Override
                    public void onAckErrorCode(BaseResult baseResult) {
                        if (isSafe()) {
                            mViewRef.get().uploadFailed();
                        }
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        if (isSafe()) {
                            mViewRef.get().uploadFailed();
                        }
                    }

                    @Override
                    public void onSubscribe1(Disposable d) {
                        compositeDisposable.add(d);
                    }
                });

    }


    public void deleteWifiVideoDevice(String wifiSn){
        XiaokaiNewServiceImp.wifiVideoLockUnbind(wifiSn,MyApplication.getInstance().getUid()).subscribe(new BaseObserver<WifiLockVideoBindResult>() {
            @Override
            public void onSuccess(WifiLockVideoBindResult wifiLockVideoBindResult) {
                MyApplication.getInstance().getAllDevicesByMqtt(true);
                SPUtils.remove(KeyConstants.WIFI_LOCK_ALARM_RECORD + wifiSn);
                SPUtils.remove(KeyConstants.WIFI_LOCK_OPERATION_RECORD + wifiSn);
                SPUtils.remove(KeyConstants.WIFI_LOCK_OPEN_COUNT + wifiSn);
                SPUtils.remove(KeyConstants.WIFI_LOCK_SHARE_USER_LIST + wifiSn);
                SPUtils.remove(KeyConstants.WIFI_LOCK_PASSWORD_LIST + wifiSn);
                if (isSafe()) {
                    mViewRef.get().onDeleteDeviceSuccess();
                }
            }

            @Override
            public void onAckErrorCode(BaseResult baseResult) {
                if (isSafe()) {
                    mViewRef.get().onDeleteDeviceFailedServer(baseResult);
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                if (isSafe()) {
                    mViewRef.get().onDeleteDeviceFailed(throwable);
                }
            }

            @Override
            public void onSubscribe1(Disposable d) {
                compositeDisposable.add(d);
            }
        });
    }

}
