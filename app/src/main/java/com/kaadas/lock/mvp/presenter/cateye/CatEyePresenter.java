package com.kaadas.lock.mvp.presenter.cateye;


import android.os.Handler;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.cateye.ICatEyeView;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.eventbean.CatEyeEventBean;
import com.kaadas.lock.publiclibrary.mqtt.eventbean.DeviceOnLineBean;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.GetBindGatewayStatusResult;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.greenDao.bean.CatEyeEvent;
import com.kaadas.lock.utils.greenDao.db.CatEyeEventDao;
import com.kaadas.lock.utils.networkListenerutil.NetWorkChangReceiver;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public class CatEyePresenter<T> extends BasePresenter<ICatEyeView> {
    private Disposable  listenerGatewayOnLine;
    private Disposable  listenerDeviceOnLineDisposable;
    private Disposable  networkCatEyeDisposable;
    private Disposable catEyeEventDisposable;
    private Handler mHandler = new Handler();
    //获取网关状态通知
    public void getPublishNotify() {

        if (mqttService != null) {
            toDisposable(listenerGatewayOnLine);
            listenerGatewayOnLine = mqttService.listenerNotifyData()
                    .compose(RxjavaHelper.observeOnMainThread())
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            if (mqttData != null) {
                                GetBindGatewayStatusResult gatewayStatusResult = new Gson().fromJson(mqttData.getPayload(), GetBindGatewayStatusResult.class);
                                LogUtils.e("监听网关GatewayActivity" + gatewayStatusResult.getDevuuid());
                                if (gatewayStatusResult != null&&gatewayStatusResult.getData().getState()!=null) {
                                    if (mViewRef.get() != null) {
                                        mViewRef.get().gatewayStatusChange(gatewayStatusResult.getDevuuid(),gatewayStatusResult.getData().getState());
                                    }
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            //网关状态发生异常
                        }
                    });
            compositeDisposable.add(listenerGatewayOnLine);
        }
    }

    /**
     * 监听设备上线下线
     */
    public void listenerDeviceOnline() {
        if (mqttService != null) {
            toDisposable(listenerDeviceOnLineDisposable);
            listenerDeviceOnLineDisposable = mqttService.listenerDataBack()
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            return mqttData.getFunc().equals(MqttConstant.GW_EVENT);
                        }
                    })
                    .compose(RxjavaHelper.observeOnMainThread())
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            DeviceOnLineBean deviceOnLineBean = new Gson().fromJson(mqttData.getPayload(), DeviceOnLineBean.class);
                            if (deviceOnLineBean!=null){
                                if (mViewRef.get()!=null&&deviceOnLineBean.getEventparams().getEvent_str()!=null){
                                    mViewRef.get().deviceStatusChange(deviceOnLineBean.getGwId(),deviceOnLineBean.getDeviceId(),deviceOnLineBean.getEventparams().getEvent_str());
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                        }
                    });
            compositeDisposable.add(listenerDeviceOnLineDisposable);
        }

    }

    //读取数据库数据
    private List<CatEyeEvent> catEyeInfo=new ArrayList<>();
    //获取猫眼动态信息
    public List<CatEyeEvent> getCatEyeDynamicInfo(int page,int pageNum,String gatewayId,String deviceId){
        //获取数据库的门锁报警信息
        LogUtils.e("访问数据库的猫眼信息");
        catEyeInfo.clear();
        List<CatEyeEvent> catEyeEventsList=MyApplication.getInstance().getDaoWriteSession().queryBuilder(CatEyeEvent.class).orderDesc(CatEyeEventDao.Properties.EventTime).offset(page * pageNum).limit(pageNum).list();
        if(catEyeEventsList!=null&&catEyeEventsList.size()>0){
            for (CatEyeEvent catEyeEvent:catEyeEventsList) {
                if (gatewayId.equals(catEyeEvent.getGatewayId())&&deviceId.equals(catEyeEvent.getDeviceId())){
                    catEyeInfo.add(catEyeEvent);
                }
            }
            return catEyeInfo;
        }
        return null;
}

    //网络变化通知
    public void listenerNetworkChange(){
        toDisposable(networkCatEyeDisposable);
        networkCatEyeDisposable= NetWorkChangReceiver.notifyNetworkChange().subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if (aBoolean){
                    if (mViewRef.get()!=null){
                        mViewRef.get().networkChangeSuccess();
                    }
                }
            }
        });
        compositeDisposable.add(networkCatEyeDisposable);
    }


    public void listenCatEyeEvent() {
        toDisposable(catEyeEventDisposable);
        catEyeEventDisposable = mqttService.listenerDataBack()
                .filter(new Predicate<MqttData>() {
                    @Override
                    public boolean test(MqttData mqttData) throws Exception {
                        if (MqttConstant.EVENT.equals(mqttData.getMsgtype())
                                && MqttConstant.GW_EVENT.equals(mqttData.getFunc())) {
                            return true;
                        }
                        return false;
                    }
                })
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<MqttData>() {
                    @Override
                    public void accept(MqttData mqttData) throws Exception {
                        JSONObject jsonObject = new JSONObject(mqttData.getPayload());
                        String devtype = jsonObject.getString("devtype");
                        if (TextUtils.isEmpty(devtype)) { //devtype为空   无法处理数据
                            return;
                        }
                        // TODO: 2019/5/14 处理猫眼动态和锁上报的信息分别放在不同的表中
                        /**
                         * 猫眼信息上报
                         */
                        if (devtype.equals(KeyConstants.DEV_TYPE_CAT_EYE)) {
                            if (mViewRef.get()!=null){
                                //两秒后进行重连
                                Runnable reconncetRunnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        if (mViewRef!=null&&mViewRef.get()!=null){
                                            mViewRef.get().catEyeEventSuccess();
                                            LogUtils.e("访问数据猫眼信息");
                                        }
                                    }
                                };
                                mHandler.postDelayed(reconncetRunnable, 2000);
                            }
                        }
                    }
                });
        compositeDisposable.add(catEyeEventDisposable);

    }

}
