package com.kaadas.lock.mvp.presenter.wifilock.videolock;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.wifilock.videolock.IWifiVideoLockOTAView;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.CheckOTAResult;
import com.kaadas.lock.publiclibrary.http.result.WifiLockVideoBindResult;
import com.kaadas.lock.publiclibrary.http.util.BaseObserver;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.MqttCommandFactory;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.SetVideoLockAmModeResult;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.SetVideoLockLangResult;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.SetVideoLockSafeModeResult;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;
import com.kaadas.lock.publiclibrary.xm.XMP2PManager;
import com.kaadas.lock.publiclibrary.xm.bean.DeviceInfo;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.SPUtils;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public class WifiVideoLockOTAPresenter<T> extends BasePresenter<IWifiVideoLockOTAView> {

    private Disposable listenActionUpdateDisposable;
    private String wifiSN;
    private Disposable otaDisposable;

    private static  String did ="";//AYIOTCN-000337-FDFTF
    private static  String sn ="";//010000000020500020

    private static  String p2pPassword ="";//ut4D0mvz

    private static  String serviceString=XMP2PManager.serviceString;;

    public void init(String wifiSn) {
        wifiSN = wifiSn;
        listenActionUpdate();
    }

    @Override
    public void attachView(IWifiVideoLockOTAView view) {
        super.attachView(view);
    }

    @Override
    public void detachView() {
        super.detachView();
    }

    public void setNickName(String wifiSN, String lockNickname) {
        XiaokaiNewServiceImp.wifiLockUpdateNickname(wifiSN, MyApplication.getInstance().getUid(), lockNickname)
                .subscribe(new BaseObserver<BaseResult>() {
                    @Override
                    public void onSuccess(BaseResult baseResult) {
                        MyApplication.getInstance().getAllDevicesByMqtt(true);
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

    public void deleteVideoDevice(String wifiSn){
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

    public void connectNotifyGateWayNewVersion(){
        DeviceInfo deviceInfo=new DeviceInfo();
        deviceInfo.setDeviceDid(did);
        deviceInfo.setP2pPassword(p2pPassword);
        deviceInfo.setDeviceSn(sn);
        deviceInfo.setServiceString(serviceString);
        XMP2PManager.getInstance().setOnConnectStatusListener(new XMP2PManager.ConnectStatusListener() {
            @Override
            public void onConnectFailed(int paramInt) {

            }

            @Override
            public void onConnectSuccess() {
                XMP2PManager.getInstance().setOnMqttCtrl(new XMP2PManager.XMP2PMqttCtrlListener() {
                    @Override
                    public void onMqttCtrl(JSONObject jsonObject) {
                        if(isSafe()){
                            try {
                                if (jsonObject.getString("result").equals("ok")){

                                    MyApplication.getInstance().getAllDevicesByMqtt(true);
                                    notifyGateWayNewVersion();
                                }else{
                                    if(isSafe()){
                                        mViewRef.get().onSettingCallBack(false);
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                try{
                    XMP2PManager.getInstance().mqttCtrl(1);
                }catch (Exception e){

                }
            }

            @Override
            public void onStartConnect(String paramString) {

            }

            @Override
            public void onErrorMessage(String message) {

            }

            @Override
            public void onNotifyGateWayNewVersion(String paramString) {

            }

            @Override
            public void onRebootDevice(String paramString) {

            }
        });
        int param = XMP2PManager.getInstance().connectDevice(deviceInfo);
    }

    public void notifyGateWayNewVersion(){

        XMP2PManager.getInstance().notifyGateWayNewVersion();
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
                        if ("200".equals(otaResult.getCode() + "")) {
                            if (isSafe()) {
                                CheckOTAResult.UpdateFileInfo data = otaResult.getData();
                                mViewRef.get().needUpdate(data, SN,data.getFileVersion(), type);
                            }
                        } else if ("401".equals(otaResult.getCode() + "")) { // 数据参数不对
                            if (isSafe()) {
                                mViewRef.get().dataError();
                            }
                        } else if ("102".equals(otaResult.getCode() + "")) { //SN格式不对
                            if (isSafe()) {
                                mViewRef.get().snError();
                            }
                        } else if ("210".equals(otaResult.getCode() + "")) { // 查无结果
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

    public void release(){
        XMP2PManager.getInstance().stopConnect();//
        XMP2PManager.getInstance().stopCodec();
    }

    public void stopConnect(){
        XMP2PManager.getInstance().stopCodec();

    }


    public void settingDevice(WifiLockInfo wifiLockInfo) {
        did = wifiLockInfo.getDevice_did();
        sn = wifiLockInfo.getDevice_sn();
        p2pPassword = wifiLockInfo.getP2p_password();

    }

    public void setMqttCtrl(int ctrl){

        XMP2PManager.getInstance().setOnMqttCtrl(new XMP2PManager.XMP2PMqttCtrlListener() {
            @Override
            public void onMqttCtrl(JSONObject jsonObject) {
                if(isSafe()){

                    try {
                        if (jsonObject.getString("result").equals("ok")){
                            mViewRef.get().onMqttCtrl(true);
                        }else{
                            mViewRef.get().onMqttCtrl(false);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }finally {
                        mViewRef.get().onMqttCtrl(false);
                    }
                }
            }
        });
        try{
            XMP2PManager.getInstance().mqttCtrl(ctrl);
        }catch (Exception e){

        }

    }

    public void setSafeMode(String wifiSN,int safeMode){
        if (mqttService != null && mqttService.getMqttClient() != null && mqttService.getMqttClient().isConnected()) {

            MqttMessage mqttMessage = MqttCommandFactory.setVideoLockSafeMode(wifiSN,safeMode);
            mqttService.mqttPublish(MqttConstant.getCallTopic(MyApplication.getInstance().getUid()),mqttMessage)
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if(MqttConstant.SET_LOCK.equals(mqttData.getFunc())){
                                return true;
                            }
                            return false;
                        }
                    })
                    .timeout(10 * 1000, TimeUnit.MILLISECONDS)
                    .compose(RxjavaHelper.observeOnMainThread())
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            SetVideoLockSafeModeResult setVideoLockSafeMode = new Gson().fromJson(mqttData.getPayload(), SetVideoLockSafeModeResult.class);
                            if(setVideoLockSafeMode != null){
                                if("200".equals(setVideoLockSafeMode.getCode() + "")){
                                    if(isSafe()){

                                        mViewRef.get().onSettingCallBack(true);
                                    }
                                }else{
                                    if(isSafe()){
                                        mViewRef.get().onSettingCallBack(false);
                                    }
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if(isSafe()){
                                mViewRef.get().onSettingCallBack(false);
                            }
                        }
                    });

        }
    }

    public void setAMMode(String wifiSN,int amMode){
        if (mqttService != null && mqttService.getMqttClient() != null && mqttService.getMqttClient().isConnected()) {

            MqttMessage mqttMessage = MqttCommandFactory.setVideoLockAmMode(wifiSN,amMode);
            mqttService.mqttPublish(MqttConstant.getCallTopic(MyApplication.getInstance().getUid()),mqttMessage)
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if(MqttConstant.SET_LOCK.equals(mqttData.getFunc())){
                                return true;
                            }
                            return false;
                        }
                    })
                    .timeout(10 * 1000, TimeUnit.MILLISECONDS)
                    .compose(RxjavaHelper.observeOnMainThread())
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            SetVideoLockAmModeResult setVideoLockAmMode = new Gson().fromJson(mqttData.getPayload(), SetVideoLockAmModeResult.class);
                            if(setVideoLockAmMode != null){
                                if("200".equals(setVideoLockAmMode.getCode())){
                                    if(isSafe()){

                                        mViewRef.get().onSettingCallBack(true);
                                    }
                                }else{
                                    if(isSafe()){
                                        mViewRef.get().onSettingCallBack(false);
                                    }
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if(isSafe()){
                                mViewRef.get().onSettingCallBack(false);
                            }
                        }
                    });

        }
    }


    public void setLanguage(String wifiSN,String language){
        if (mqttService != null && mqttService.getMqttClient() != null && mqttService.getMqttClient().isConnected()) {

            MqttMessage mqttMessage = MqttCommandFactory.setVideoLockLang(wifiSN,language);
            mqttService.mqttPublish(MqttConstant.getCallTopic(MyApplication.getInstance().getUid()),mqttMessage)
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if(MqttConstant.SET_LOCK.equals(mqttData.getFunc())){
                                return true;
                            }
                            return false;
                        }
                    })
                    .timeout(10 * 1000, TimeUnit.MILLISECONDS)
                    .compose(RxjavaHelper.observeOnMainThread())
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            SetVideoLockLangResult setVideoLockLang = new Gson().fromJson(mqttData.getPayload(), SetVideoLockLangResult.class);
                            if(setVideoLockLang != null){
                                if("200".equals(setVideoLockLang.getCode() + "")){
                                    if(isSafe()){

                                        mViewRef.get().onSettingCallBack(true);
                                    }
                                }else{
                                    if(isSafe()){
                                        mViewRef.get().onSettingCallBack(false);
                                    }
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if(isSafe()){
                                mViewRef.get().onSettingCallBack(false);
                            }
                        }
                    });

        }
    }


}
