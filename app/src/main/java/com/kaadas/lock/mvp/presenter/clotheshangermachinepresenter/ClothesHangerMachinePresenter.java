package com.kaadas.lock.mvp.presenter.clotheshangermachinepresenter;

import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.clotheshangermachineview.IClothesHangerMachineAddThirdView;
import com.kaadas.lock.mvp.view.clotheshangermachineview.IClothesHangerMachineView;
import com.kaadas.lock.publiclibrary.bean.ClothesHangerMachineAllStatusBean;
import com.kaadas.lock.publiclibrary.bean.GatewayInfo;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.MqttCommandFactory;
import com.kaadas.lock.publiclibrary.mqtt.eventbean.DeviceOnLineBean;
import com.kaadas.lock.publiclibrary.mqtt.eventbean.GatewayResetBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.ClothesHangerMachineBean.ClothesHangerMachineOnlineBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.ClothesHangerMachineBean.ClothesHangerMachineStatusBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.ClothesHangerMachineBean.GetHangerAllStatus;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.GetLockLang;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.ClothesHangerMachineBean.SetHangerAirDryResult;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.ClothesHangerMachineBean.SetHangerChildLockResult;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.ClothesHangerMachineBean.SetHangerMotorResult;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;
import com.kaadas.lock.utils.LogUtils;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public class ClothesHangerMachinePresenter<T> extends BasePresenter<IClothesHangerMachineView> {

    private Disposable setBakingDisposable;
    private Disposable setAirDryDisposable;
    private Disposable setVoiceDisposable;
    private Disposable setChildLockDisposable;
    private Disposable setUVDisposable;
    private Disposable setLightingDisposable;
    private Disposable setMotorDisposable;
    private Disposable getHangerAllStatus;
    private Disposable listenerHangerStatus;
    private Disposable listenerHangerOnline;

    @Override
    public void attachView(IClothesHangerMachineView view) {
        super.attachView(view);
        listenerHangerOnline();
        listenerHangerAllStatus();
    }

    private void listenerHangerOnline() {
        if(mqttService != null){
            toDisposable(listenerHangerOnline);
            listenerHangerOnline = mqttService.listenerDataBack()
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if(mqttData.getFunc().equals(MqttConstant.FUNC_WFEVENT)){
                                return true;
                            }
                            return false;
                        }
                    })
                    .compose(RxjavaHelper.observeOnMainThread())
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            ClothesHangerMachineOnlineBean onlineBean = new Gson().fromJson(mqttData.getPayload(), ClothesHangerMachineOnlineBean.class);
                            LogUtils.e("shulan CLOTHES_HANGER_MACHINE_ONLINE-->" + onlineBean.toString());
                            if(onlineBean.getDevtype().equals(MqttConstant.CLOTHES_HANGER_MACHINE_MX_CHIP)
                                && onlineBean.getEventtype().equals(MqttConstant.CLOTHES_HANGER_MACHINE_ONLINE)){

                                if(onlineBean.getOnlineState() == 1){

                                }else{

                                }
                            }
                        }
                    });
            compositeDisposable.add(listenerHangerOnline);
        }
    }

    private void listenerHangerAllStatus() {
        if (mqttService != null) {
            toDisposable(listenerHangerStatus);
            listenerHangerStatus = mqttService.listenerDataBack()
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if (mqttData.getFunc().equals(MqttConstant.FUNC_WFEVENT)) {
                                return true;
                            }
                            return false;
                        }
                    })
                    .compose(RxjavaHelper.observeOnMainThread())
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            ClothesHangerMachineStatusBean statusBean = new Gson().fromJson(mqttData.getPayload(), ClothesHangerMachineStatusBean.class);
                            if(statusBean.getDevtype().equals(MqttConstant.CLOTHES_HANGER_MACHINE_MX_CHIP)
                                    && statusBean.getEventtype().equals(MqttConstant.CLOTHES_HANGER_MACHINE_ALL_STATUS)){
                                if(statusBean.getEventparams().getLight() != null){
                                    if(isSafe()){
                                        mViewRef.get().setLightingSuccess(statusBean.getEventparams().getLight().getSingle(),
                                                statusBean.getEventparams().getLight().getCountdown());
                                    }
                                }
                                if(statusBean.getEventparams().getAirDry() != null){
                                    if(isSafe()){
                                        mViewRef.get().setAirDryTimeSuccess(statusBean.getEventparams().getAirDry().getSingle(),
                                                statusBean.getEventparams().getAirDry().getCountdown());
                                    }
                                }
                                if(statusBean.getEventparams().getBaking() != null){
                                    if(isSafe()){
                                        mViewRef.get().setBakingTimeSuccess(statusBean.getEventparams().getBaking().getSingle(),
                                                statusBean.getEventparams().getBaking().getCountdown());
                                    }
                                }
                                if(statusBean.getEventparams().getUV() != null){
                                    if(isSafe()){
                                        mViewRef.get().setUVSuccess(statusBean.getEventparams().getUV().getSingle(),
                                                statusBean.getEventparams().getUV().getCountdown());
                                    }
                                }
                                if(statusBean.getEventparams().getMotor() != null){
                                    if(isSafe()){
                                        mViewRef.get().setMotorSuccess(statusBean.getEventparams().getMotor().getAction(),
                                                statusBean.getEventparams().getMotor().getStatus());
                                    }
                                }
                                try {
                                    if(isSafe()){
                                        mViewRef.get().setChildLockSuccess(statusBean.getEventparams().getChildLock());
                                    }
                                }catch (Exception e){
                                }
                                try {
                                    if(isSafe()){
                                        mViewRef.get().setVoiceSuccess(statusBean.getEventparams().getLoudspeaker());
                                    }
                                }catch (Exception e){
                                }
                                if(isSafe()){
                                    mViewRef.get().setOverload(statusBean.getEventparams().getOverload());
                                }
                            }
                        }

                    });
            compositeDisposable.add(listenerHangerStatus);
        }
    }

    public void getHangerAllStatus(String wifiID) {
        if (mqttService != null) {
            toDisposable(getHangerAllStatus);
            getHangerAllStatus = mqttService.mqttPublish(MqttConstant.getCallTopic(MyApplication.getInstance().getUid()),
                    MqttCommandFactory.getHangerAllStatus(wifiID))
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if(MqttConstant.CLOTHES_HANGER_MACHINE_GET_ALL_STATUS.equals(mqttData.getFunc())){
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
                            ClothesHangerMachineAllStatusBean result = new Gson().fromJson(mqttData.getPayload(), ClothesHangerMachineAllStatusBean.class);
                            LogUtils.e("shulan CLOTHES_HANGER_MACHINE_GET_ALL_STATUS-->" + result.toString());
                            if(result.getCode() == 200){
                                if(result.getParams().getLight() != null){
                                    if(isSafe()){
                                        mViewRef.get().setLightingSuccess(result.getParams().getLight().getSingle(),result.getParams().getLight().getCountdown());
                                    }
                                }
                                if(result.getParams().getAirDry() != null){
                                    if(isSafe()){
                                        mViewRef.get().setAirDryTimeSuccess(result.getParams().getAirDry().getSingle(),result.getParams().getAirDry().getCountdown());
                                    }
                                }
                                if(result.getParams().getBaking() != null){
                                    if(isSafe()){
                                        mViewRef.get().setBakingTimeSuccess(result.getParams().getBaking().getSingle(),result.getParams().getBaking().getCountdown());
                                    }
                                }
                                if(result.getParams().getUV() != null){
                                    if(isSafe()){
                                        mViewRef.get().setUVSuccess(result.getParams().getUV().getSingle(),result.getParams().getUV().getCountdown());
                                    }
                                }
                                if(result.getParams().getMotor() != null){
                                    if(isSafe()){
                                        mViewRef.get().setMotorSuccess(result.getParams().getMotor().getAction(),result.getParams().getMotor().getStatus());
                                    }
                                }
                                try {
                                    if(isSafe()){
                                        mViewRef.get().setChildLockSuccess(result.getParams().getChildLock());
                                    }
                                }catch (Exception e){
                                }
                                try {
                                    if(isSafe()){
                                        mViewRef.get().setVoiceSuccess(result.getParams().getLoudspeaker());
                                    }
                                }catch (Exception e){
                                }
                                if(isSafe()){
                                    mViewRef.get().setOverload(result.getParams().getOverload());
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {

                        }
                    });
            compositeDisposable.add(getHangerAllStatus);
        }
    }

    @Override
    public void detachView() {
        super.detachView();
    }

    public void setBakingTime(String wifiID,int time){
        int bakingSwitch = -1;
        if(time == 120){
            bakingSwitch = 1;
        }else if(time == 240){
            bakingSwitch = 2;
        }else{
            bakingSwitch = 0;
        }
        if(mqttService != null){
            toDisposable(setBakingDisposable);
            setBakingDisposable = mqttService.mqttPublish(MqttConstant.getCallTopic(MyApplication.getInstance().getUid()),
                    MqttCommandFactory.setClothesHangerMachineBaking(wifiID,bakingSwitch))
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if(MqttConstant.CLOTHES_HANGER_MACHINE_SET_BACKING.equals(mqttData.getFunc())){
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
                            SetHangerAirDryResult result = new Gson().fromJson(mqttData.getPayload(), SetHangerAirDryResult.class);
                            LogUtils.e("shulan CLOTHES_HANGER_MACHINE_SET_BACKING-->" + result.toString());
                            if ("200".equals(result.getCode() + "")) { //请求成功
                                if(isSafe()){
                                    mViewRef.get().setBakingTimeSuccess(result.getParams().getSingle(),result.getParams().getCountdown());
                                }
                            }else{
                                if(isSafe()){
                                    mViewRef.get().setBakingTimeFailed();
                                }
                            }

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if(isSafe()){
                                mViewRef.get().setBakingTimeThrowable(throwable);
                            }
                        }
                    });
            compositeDisposable.add(setBakingDisposable);
        }

    }

    public void setAirDryTime(String wifiID,int time){
        int airDrySwitch = -1;
        if(time == 120){
            airDrySwitch = 1;
        }else if(time == 240){
            airDrySwitch = 2;
        }else{
            airDrySwitch = 0;
        }
        if(mqttService != null){
            toDisposable(setAirDryDisposable);
            setAirDryDisposable = mqttService.mqttPublish(MqttConstant.getCallTopic(MyApplication.getInstance().getUid()),
                    MqttCommandFactory.setClothesHangerMachineAirDry(wifiID,airDrySwitch))
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if(MqttConstant.CLOTHES_HANGER_MACHINE_SET_AIR_DRY.equals(mqttData.getFunc())){
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
                            SetHangerAirDryResult result = new Gson().fromJson(mqttData.getPayload(), SetHangerAirDryResult.class);
                            LogUtils.e("shulan CLOTHES_HANGER_MACHINE_SET_AIR_DRY-->" + result.toString());
                            if ("200".equals(result.getCode() + "")) { //请求成功
                                if(isSafe()){
                                    mViewRef.get().setAirDryTimeSuccess(result.getParams().getSingle(),result.getParams().getCountdown());
                                }
                            }else{
                                if(isSafe()){
                                    mViewRef.get().setAirDryTimeFailed();
                                }
                            }

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if(isSafe()){
                                mViewRef.get().setAirDryTimeThrowable(throwable);
                            }
                        }
                    });
            compositeDisposable.add(setAirDryDisposable);
        }


    }

    public void setHangerChildLock(String wifiID,int action) {
        if(mqttService != null){
            toDisposable(setChildLockDisposable);
            setChildLockDisposable = mqttService.mqttPublish(MqttConstant.getCallTopic(MyApplication.getInstance().getUid()),
                    MqttCommandFactory.setClothesHangerMachineChildLock(wifiID,action))
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if(MqttConstant.CLOTHES_HANGER_MACHINE_SET_CHILD_LOCK.equals(mqttData.getFunc())){
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
                            SetHangerChildLockResult result = new Gson().fromJson(mqttData.getPayload(), SetHangerChildLockResult.class);
                            LogUtils.e("shulan CLOTHES_HANGER_MACHINE_SET_CHILD_LOCK-->" + result.toString());
                            if ("200".equals(result.getCode() + "")) { //请求成功
                                if(isSafe()){
                                    mViewRef.get().setChildLockSuccess(result.getParams().getSingle());
                                }
                            }else{
                                if(isSafe()){
                                    mViewRef.get().setChildLockFailed();
                                }
                            }

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if(isSafe()){
                                mViewRef.get().setChildLockThrowable(throwable);
                            }
                        }
                    });
            compositeDisposable.add(setChildLockDisposable);
        }
    }

    public void setHangerVoice(String wifiID,int action) {
        if(mqttService != null){
            toDisposable(setVoiceDisposable);
            setVoiceDisposable = mqttService.mqttPublish(MqttConstant.getCallTopic(MyApplication.getInstance().getUid()),
                    MqttCommandFactory.setClothesHangerMachineLoudspeaker(wifiID,action))
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if(MqttConstant.CLOTHES_HANGER_MACHINE_SET_LOUD_SPEAKER.equals(mqttData.getFunc())){
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
                            SetHangerChildLockResult result = new Gson().fromJson(mqttData.getPayload(), SetHangerChildLockResult.class);
                            LogUtils.e("shulan CLOTHES_HANGER_MACHINE_SET_LOUD_SPEAKER-->" + result.toString());
                            if ("200".equals(result.getCode() + "")) { //请求成功
                                if(isSafe()){
                                    mViewRef.get().setVoiceSuccess(result.getParams().getSingle());
                                }
                            }else{
                                if(isSafe()){
                                    mViewRef.get().setVoiceFailed();
                                }
                            }

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if(isSafe()){
                                mViewRef.get().setVoiceThrowable(throwable);
                            }
                        }
                    });
            compositeDisposable.add(setVoiceDisposable);
        }

    }

    public void setHangerUV(String wifiID, int action) {
        if(mqttService != null){
            toDisposable(setUVDisposable);
            setUVDisposable = mqttService.mqttPublish(MqttConstant.getCallTopic(MyApplication.getInstance().getUid()),
                    MqttCommandFactory.setClothesHangerMachineUV(wifiID,action))
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if(MqttConstant.CLOTHES_HANGER_MACHINE_SET_UV.equals(mqttData.getFunc())){
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
                            SetHangerAirDryResult result = new Gson().fromJson(mqttData.getPayload(), SetHangerAirDryResult.class);
                            LogUtils.e("shulan CLOTHES_HANGER_MACHINE_SET_UV-->" + result.toString());
                            if ("200".equals(result.getCode() + "")) { //请求成功
                                if(isSafe()){
                                    mViewRef.get().setUVSuccess(result.getParams().getSingle(),result.getParams().getCountdown());
                                }
                            }else{
                                if(isSafe()){
                                    mViewRef.get().setUVFailed();
                                }
                            }

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if(isSafe()){
                                mViewRef.get().setUVThrowable(throwable);
                            }
                        }
                    });
            compositeDisposable.add(setUVDisposable);
        }

    }

    public void setHangerLighting(String wifiID, int action) {
        if(mqttService != null){
            toDisposable(setLightingDisposable);
            setLightingDisposable = mqttService.mqttPublish(MqttConstant.getCallTopic(MyApplication.getInstance().getUid()),
                    MqttCommandFactory.setClothesHangerMachineLighting(wifiID,action))
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if(MqttConstant.CLOTHES_HANGER_MACHINE_SET_LIGHTING.equals(mqttData.getFunc())){
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
                            SetHangerAirDryResult result = new Gson().fromJson(mqttData.getPayload(), SetHangerAirDryResult.class);
                            LogUtils.e("shulan CLOTHES_HANGER_MACHINE_SET_LIGHTING-->" + result.toString());
                            if ("200".equals(result.getCode() + "")) { //请求成功
                                if(isSafe()){
                                    mViewRef.get().setLightingSuccess(result.getParams().getSingle(),result.getParams().getCountdown());
                                }
                            }else{
                                if(isSafe()){
                                    mViewRef.get().setLightingFailed();
                                }
                            }

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if(isSafe()){
                                mViewRef.get().setLightingThrowable(throwable);
                            }
                        }
                    });
            compositeDisposable.add(setLightingDisposable);
        }
    }

    public void setHangerMotor(String wifiID, int action) {
        if(mqttService != null){
            toDisposable(setMotorDisposable);
            setMotorDisposable = mqttService.mqttPublish(MqttConstant.getCallTopic(MyApplication.getInstance().getUid()),
                    MqttCommandFactory.setClothesHangerMachineMotor(wifiID,action))
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if(MqttConstant.CLOTHES_HANGER_MACHINE_SET_MOTOR.equals(mqttData.getFunc())){
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
                            SetHangerMotorResult result = new Gson().fromJson(mqttData.getPayload(), SetHangerMotorResult.class);
                            LogUtils.e("shulan CLOTHES_HANGER_MACHINE_SET_MOTOR-->" + result.toString());
                            if ("200".equals(result.getCode() + "")) { //请求成功
                                if(isSafe()){
                                    mViewRef.get().setMotorSuccess(result.getParams().getAction(),result.getParams().getStatus());
                                }
                            }else{
                                if(isSafe()){
                                    mViewRef.get().setMotorFailed(action);
                                }
                            }

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if(isSafe()){
                                mViewRef.get().setMotorThrowable(throwable);
                            }
                        }
                    });
            compositeDisposable.add(setMotorDisposable);
        }
    }
}

