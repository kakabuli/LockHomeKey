package com.kaadas.lock.mvp.presenter.wifilock.videolock;

import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.wifilock.videolock.IWifiVideoLockScreenLightLevelView;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.MqttCommandFactory;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.SettingScreenBrightnessResult;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;
import com.kaadas.lock.publiclibrary.xm.XMP2PManager;
import com.kaadas.lock.publiclibrary.xm.bean.DeviceInfo;
import com.kaadas.lock.utils.LogUtils;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public class WifiVideoLockScreenLightLevelPresenter<T> extends BasePresenter<IWifiVideoLockScreenLightLevelView> {
    private Disposable setOpenDirectionDisposable;
    private static  String did ="";//AYIOTCN-000337-FDFTF
    private static  String sn ="";//010000000020500020

    private static  String p2pPassword ="";//ut4D0mvz

    private static  String serviceString= XMP2PManager.serviceString;;

    public void setScreenLightLevel(String wifiSN,int screenLightLevel) {
        if (mqttService != null && mqttService.getMqttClient() != null && mqttService.getMqttClient().isConnected()) {
            MqttMessage mqttMessage = MqttCommandFactory.settingScreenBrightness(wifiSN,screenLightLevel);
            toDisposable(setOpenDirectionDisposable);
            setOpenDirectionDisposable = mqttService.mqttPublish(MqttConstant.getCallTopic(MyApplication.getInstance().getUid()),mqttMessage)
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if(MqttConstant.SET_CAMERA.equals(mqttData.getFunc())){
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
                            SettingScreenBrightnessResult mSettingScreenBrightnessResult = new Gson().fromJson(mqttData.getPayload(), SettingScreenBrightnessResult.class);
                            LogUtils.e("shulan mSettingScreenBrightnessResult-->" + mSettingScreenBrightnessResult.toString());
                            if(mSettingScreenBrightnessResult != null && isSafe()){
                                if("200".equals(mSettingScreenBrightnessResult.getCode() + "")){
                                    mViewRef.get().onSettingCallBack(true);
                                }else if("201".equals(mSettingScreenBrightnessResult.getCode() + "")){
                                    mViewRef.get().onSettingCallBack(false);
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
            compositeDisposable.add(setOpenDirectionDisposable);
        }
    }

    public void setConnectScreenLightLevel(String wifiSN,int screenLightLevel) {
        DeviceInfo deviceInfo=new DeviceInfo();
        deviceInfo.setDeviceDid(did);
        deviceInfo.setP2pPassword(p2pPassword);
        deviceInfo.setDeviceSn(sn);
        deviceInfo.setServiceString(serviceString);
        XMP2PManager.getInstance().setOnConnectStatusListener(new XMP2PManager.ConnectStatusListener() {
            @Override
            public void onConnectFailed(int paramInt) {
                if(isSafe()){
                    mViewRef.get().onSettingCallBack(false);
                }
//                setMqttCtrl(0);
            }

            @Override
            public void onConnectSuccess() {
                XMP2PManager.getInstance().mqttCtrl(1);
                XMP2PManager.getInstance().setOnMqttCtrl(new XMP2PManager.XMP2PMqttCtrlListener() {
                    @Override
                    public void onMqttCtrl(JSONObject jsonObject) {
                        if(isSafe()){
                            try {
                                if (jsonObject.getString("result").equals("ok")){
                                    setScreenLightLevel(wifiSN,screenLightLevel);
                                }else{
                                    mViewRef.get().onSettingCallBack(false);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }

            @Override
            public void onStartConnect(String paramString) {

            }

            @Override
            public void onErrorMessage(String message) {
                if(isSafe()){
                    mViewRef.get().onSettingCallBack(false);
                }
//                setMqttCtrl(0);
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

    public void release(){
        XMP2PManager.getInstance().stopConnect();//
        XMP2PManager.getInstance().stopCodec();
    }

    public void stopConnect(){
        XMP2PManager.getInstance().stopCodec();

    }

    public void setMqttCtrl(int ctrl){
        XMP2PManager.getInstance().mqttCtrl(ctrl);
    }

    public void settingDevice(WifiLockInfo wifiLockInfo) {
        did = wifiLockInfo.getDevice_did();
        sn = wifiLockInfo.getDevice_sn();
        p2pPassword = wifiLockInfo.getP2p_password();

    }
}
