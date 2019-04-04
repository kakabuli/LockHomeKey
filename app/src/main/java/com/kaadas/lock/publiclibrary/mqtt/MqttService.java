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
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import io.reactivex.subjects.PublishSubject;

public class MqttService extends Service {

    //请勿添加static
    private MqttAndroidClient mqttAndroidClient;

    private Handler mHandler=new Handler();

    //重连次数10
    private int reconnectionNum = 10;

    /**
     * 判断是否订阅成功
     */
    private PublishSubject<Boolean> mSubscribe = PublishSubject.create();

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

    public MqttAndroidClient getMqttAndroidClient() {
        /**
         * 单例获取实例,必须要在有userId的时候才可以获取的到
         */
        String userId = MyApplication.getInstance().getUid();
        if (userId == null) {
            return null;
        }
        if (mqttAndroidClient == null) {
            synchronized (MqttService.class) {
                if (mqttAndroidClient == null) {
                    mqttAndroidClient = new MqttAndroidClient(MyApplication.getInstance(), MqttUrlConstant.MQTT_BASE_URL, "app:" + userId);
                }
            }
        }
        return mqttAndroidClient;
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
        connOpts.setAutomaticReconnect(Constant.MQTT_AUTOMATIC_RECONNECT);
        //是否清空客户端的连接记录。若为true，则断开后，broker将自动清除该客户端连接信息
        connOpts.setCleanSession(Constant.MQTT_CLEANSE_SSION);
        //设置超时时间，单位为秒 10
        connOpts.setConnectionTimeout(Constant.MQTT_CONNECTION_TIMEOUT);
        //设置心跳时间，单位为秒 20
        connOpts.setKeepAliveInterval(Constant.MQTT_KEEP_ALIVE_INTERVAL);
        //允许同时发送几条消息（未收到broker确认信息）
        connOpts.setMaxInflight(Constant.MQTT_MAX_INFLIGHT);
        //用户的id,和token
        connOpts.setUserName(user_id);
        connOpts.setPassword(user_token.toCharArray());
        return connOpts;
    }

    //连接
    public void mqttConnection(MqttAndroidClient mMqttClient, String userId, String token) {
        if (!NetUtil.isNetworkAvailable()) {
            ToastUtil.getInstance().showShort(getString(R.string.network_exception));
            return;
        }
        if (TextUtils.isEmpty(userId) || TextUtils.isEmpty(token)) {
            LogUtils.e("mqttConnection", "用户为空无法连接");
            return;
        }
        //已经连接
        if (mMqttClient.isConnected()) {
            LogUtils.e("mqttConnection", "mqtt已连接");
            return;
        }
        //设置mqtt参数
        MqttConnectOptions mqttConnectOptions = connectOption(userId, token);
        //设置回调
        mMqttClient.setCallback(new PhaoMqttCallBack());
        try {
            mMqttClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    //连接成功之后订阅主题
                    mqttSubscribe(mMqttClient,Constant.getSubscribeTopic(userId),2);
                    LogUtils.e("mqttConnection","连接成功");
                    reconnectionNum=10;
                }
                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    //可能出现无权连接（5）---用户在其他手机登录
                    if (reconnectionNum > 0) {
                        LogUtils.e("mqttConnection","连接失败"+exception.toString());
                        MqttExceptionHandle.onFail(MqttExceptionHandle.ConnectException, asyncActionToken, exception);
                        if (exception.toString().equals("无权连接 (5)")) {
                            // TODO: 2019/4/1  该用户在其他手机登录(清除所有数据）---暂时未处理
                            mqttDisconnect(mMqttClient);
                            MyApplication.getInstance().tokenInvalid(false);
                            return;
                        }
                        if ("错误的用户名或密码 (4)".equals(exception.toString())){
                            ToastUtil.getInstance().showShort("mqtt的用户名或密码错误");
                            return;
                        }
                        //两秒后进行重连
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                reconnectionNum--;
                                mqttConnection(mMqttClient, userId, token);
                            }
                        },2000);
                    }else{
                        ToastUtil.getInstance().showShort(R.string.mqtt_connection_fail);
                    }
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    //订阅
    private void mqttSubscribe(MqttAndroidClient mqttClient,String topic,int qos) {
        if (!NetUtil.isNetworkAvailable()){
            ToastUtil.getInstance().showShort(getString(R.string.network_exception));
            return;
        }
        try {
            if(mqttClient!=null){
                if(!TextUtils.isEmpty(topic)&&mqttClient.isConnected()){
                    mqttClient.subscribe(topic, qos, null, new
                            IMqttActionListener() {
                                @Override
                                public void onSuccess(IMqttToken asyncActionToken) {
                                    mSubscribe.onNext(true);
                                    LogUtils.e("mqttSubscribe","订阅成功");
                                }
                                @Override
                                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                                    mSubscribe.onNext(false);
                                    LogUtils.e("mqttSubscribe","订阅失败");
                                    MqttExceptionHandle.onFail(MqttExceptionHandle.SubscribeException,asyncActionToken,exception);
                                }
                            });
                    }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //发布
    public void mqttPublish(MqttAndroidClient mClient, String topic,Object o) {
        if (!NetUtil.isNetworkAvailable()){
            ToastUtil.getInstance().showShort(getString(R.string.network_exception));
            return;
        }
        if (!mClient.isConnected()){
            LogUtils.e("mqttPublish","mqtt未连接");
            return;
        }
        String  payload=new Gson().toJson(o);
        LogUtils.e("fjh",payload);
        MqttMessage mqttMessage=new MqttMessage();
        mqttMessage.setQos(2);
        mqttMessage.setRetained(false);
        mqttMessage.setPayload(payload.getBytes());
        try {
            mClient.publish(topic, mqttMessage, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {

                    LogUtils.e("mqttPublish",topic+"success---"+payload);
                }
                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    LogUtils.e("mqttPublish",topic+"fail");
                    MqttExceptionHandle.onFail(MqttExceptionHandle.PublishException,asyncActionToken,exception);
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }


    //断开连接
    public void mqttDisconnect(MqttAndroidClient mqttClient) {
        LogUtils.e("mqttDisconnect","正在断开连接");
        try {
            if (mqttClient.isConnected()){
                mqttClient.disconnect(null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        mqttClient.close();
                        LogUtils.e("mqttDisconnect","mqtt连接断开成功");
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        MqttExceptionHandle.onFail(MqttExceptionHandle.ConnectException,asyncActionToken,exception);
                        LogUtils.e("mqttDisconnect","mqtt连接断失败");
                    }
                });
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


}
