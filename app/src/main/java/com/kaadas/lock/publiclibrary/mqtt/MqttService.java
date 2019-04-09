package com.kaadas.lock.publiclibrary.mqtt;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.NetUtil;
import com.kaadas.lock.utils.ToastUtil;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class MqttService extends Service {

    //请勿添加static
    private MqttAndroidClient mqttClient;

    private Handler mHandler = new Handler();

    //重连次数10
    private int reconnectionNum = 10;

    /**
     * 判断是否订阅成功
     */
    private PublishSubject<Boolean> mSubscribe = PublishSubject.create();
    private PublishSubject<String> onReceiverDataObservable = PublishSubject.create();
    private PublishSubject<Boolean> connectStateObservable = PublishSubject.create();
    private PublishSubject<PublishResult> publishObservable = PublishSubject.create();
    private PublishSubject<Boolean> disconnectObservable = PublishSubject.create();

    /**
     * 订阅状态
     */
    public PublishSubject<Boolean> subscribeStatus() {
        return mSubscribe;
    }

    public class MyBinder extends Binder {
        public MqttService getService() {
            return MqttService.this;
        }
    }


    public Observable<String> listenerDataBack() {
        return onReceiverDataObservable;
    }


    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }


    /**
     * mqtt连接参数设置
     *
     * @param user_id
     * @param user_token
     * @return
     */
    private MqttConnectOptions connectOption(String user_id, String user_token) {
        //连接
        MqttConnectOptions connOpts = new MqttConnectOptions();
        //断开后，是否自动连接
        connOpts.setAutomaticReconnect(false);
        //是否清空客户端的连接记录。若为true，则断开后，broker将自动清除该客户端连接信息
        connOpts.setCleanSession(MqttConstant.MQTT_CLEANSE_SSION);
        //设置超时时间，单位为秒 10
        connOpts.setConnectionTimeout(MqttConstant.MQTT_CONNECTION_TIMEOUT);
        //设置心跳时间，单位为秒 20
        connOpts.setKeepAliveInterval(MqttConstant.MQTT_KEEP_ALIVE_INTERVAL);
        //允许同时发送几条消息（未收到broker确认信息）
        connOpts.setMaxInflight(MqttConstant.MQTT_MAX_INFLIGHT);
        //用户的id,和token
        connOpts.setUserName(user_id);
        connOpts.setPassword(user_token.toCharArray());
        return connOpts;
    }


    public MqttAndroidClient getMqttClient() {
        return mqttClient;
    }

    //连接
    public void mqttConnection() {
        String userId = MyApplication.getInstance().getUid();
        String token = MyApplication.getInstance().getToken();
        if (TextUtils.isEmpty(userId) || TextUtils.isEmpty(token)) {
            LogUtils.e("token  或者 userID  为空");
            return;
        }
        if (!NetUtil.isNetworkAvailable()) {
            ToastUtil.getInstance().showShort(getString(R.string.network_exception));
            return;
        }
        if (TextUtils.isEmpty(userId) || TextUtils.isEmpty(token)) {
            LogUtils.e("mqttConnection", "用户为空无法连接");
            return;
        }
        if (mqttClient==null){
            mqttClient = new MqttAndroidClient(MyApplication.getInstance(), MqttUrlConstant.MQTT_BASE_URL, "app:" + userId);
        }
        //已经连接
        if (mqttClient.isConnected()) {
            LogUtils.e("mqttConnection", "mqtt已连接");
            return;
        }
        //设置mqtt参数
        LogUtils.e("设置参数  userId  " + userId + "  token  " +token);
        MqttConnectOptions mqttConnectOptions = connectOption(userId, token);
        //设置回调
        mqttClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                //连接完成

            }

            @Override
            public void connectionLost(Throwable cause) {
                //连接丢失
                if (null == cause) {
                    return;
                }
                //连接丢失--需要进行重连
                LogUtils.e("connectionLost", "连接丢失需要重连");
                String userId = MyApplication.getInstance().getUid();
                String userToken = MyApplication.getInstance().getToken();
                if (TextUtils.isEmpty(userId) || TextUtils.isEmpty(userToken)) {
                    LogUtils.e("connectionLost", "用户id或者token为空无法重连");
                    return;
                }
                MyApplication.getInstance().getMqttService().mqttConnection();
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                //收到消息
                String mMqttMessage = new String(message.getPayload());
                LogUtils.e("messageArrived", mMqttMessage + "---topic" + topic);
               onReceiverDataObservable.onNext(mMqttMessage);

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                //交互完成

            }
        });
        try {
            mqttClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    mqttClient.setBufferOpts(disconnectedBufferOptions);
                    //连接成功之后订阅主题
                    mqttSubscribe(mqttClient, MqttConstant.getSubscribeTopic(userId), 2);
                    LogUtils.e("mqttConnection", "连接成功");
                    reconnectionNum = 10;
                    connectStateObservable.onNext(true);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    //可能出现无权连接（5）---用户在其他手机登录
                    if (reconnectionNum > 0) {
                        LogUtils.e("mqttConnection", "连接失败1     " + exception.toString());
                        MqttExceptionHandle.onFail(MqttExceptionHandle.ConnectException, asyncActionToken, exception);
                        if (exception.toString().equals("无权连接 (5)")) {
                            // TODO: 2019/4/1  该用户在其他手机登录(清除所有数据）---暂时未处理
                            if (mqttClient != null  ) {
                                mqttDisconnect();


                            }
                            return;
                        }
                        if ("错误的用户名或密码 (4)".equals(exception.toString())) {
                            ToastUtil.getInstance().showShort("mqtt的用户名或密码错误");
                            return;
                        }
                        //两秒后进行重连
                        Runnable reconncetRunnable = new Runnable() {
                            @Override
                            public void run() {
                                reconnectionNum--;
                                mqttConnection();
                            }
                        };
                        mHandler.postDelayed(reconncetRunnable, 2000);

                    } else {
                        ToastUtil.getInstance().showShort(R.string.mqtt_connection_fail);
                        connectStateObservable.onNext(false);
                    }

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    //订阅
    private void mqttSubscribe(MqttAndroidClient mqttClient, String topic, int qos) {
        if (!NetUtil.isNetworkAvailable()) {
            ToastUtil.getInstance().showShort(getString(R.string.network_exception));
            return;
        }
        try {
            if (mqttClient != null) {
                if (!TextUtils.isEmpty(topic) && mqttClient.isConnected()) {
                    mqttClient.subscribe(topic, qos, null, new
                            IMqttActionListener() {
                                @Override
                                public void onSuccess(IMqttToken asyncActionToken) {
                                    mSubscribe.onNext(true);
                                    LogUtils.e("mqttSubscribe", "订阅成功");
                                }

                                @Override
                                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                                    mSubscribe.onNext(false);
                                    LogUtils.e("mqttSubscribe", "订阅失败");
                                    MqttExceptionHandle.onFail(MqttExceptionHandle.SubscribeException, asyncActionToken, exception);
                                }
                            });
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //发布
    public Observable<PublishResult> mqttPublish(String topic, MqttMessage mqttMessage) {
        mqttMessage.setQos(2);
        mqttMessage.setRetained(false);
        LogUtils.e("发布消息   " + mqttMessage.getId(), new String(mqttMessage.getPayload()));
        try {
            mqttClient.publish(topic, mqttMessage, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
//                    LogUtils.e("发布消息成功  ", topic + "success---" + payload);
                    publishObservable.onNext(new PublishResult(true, asyncActionToken));
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    LogUtils.e("mqttPublish", topic + "fail");
                    MqttExceptionHandle.onFail(MqttExceptionHandle.PublishException, asyncActionToken, exception);
                    publishObservable.onNext(new PublishResult(false, asyncActionToken));
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
        return publishObservable;
    }

    //断开连接
    public void mqttDisconnect() {
        LogUtils.e("mqttDisconnect", "正在断开连接");
        if (mqttClient == null) {
            LogUtils.e("mqttClient为空");
            return;
        }
        if (mqttClient.isConnected()){
            try {
                //退出登录
                mqttClient.disconnect(null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        mqttClient.unregisterResources();
                        mqttClient=null;
                        MyApplication.getInstance().tokenInvalid(false);
                        LogUtils.e("mqttDisconnect", "断开连接成功");
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }else{
            //被挤出
            mqttClient.unregisterResources();
            mqttClient=null;
            MyApplication.getInstance().tokenInvalid(false);
        }

    }
}
