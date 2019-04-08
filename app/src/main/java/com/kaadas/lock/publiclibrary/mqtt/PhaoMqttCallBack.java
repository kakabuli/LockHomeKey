package com.kaadas.lock.publiclibrary.mqtt;
import android.text.TextUtils;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.utils.LogUtils;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class PhaoMqttCallBack implements MqttCallbackExtended {
    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        //连接完成

    }

    @Override
    public void connectionLost(Throwable cause) {

        if (null==cause){
            return;
        }
        //连接丢失--需要进行重连
        LogUtils.e("connectionLost","连接丢失需要重连");
        String userId= MyApplication.getInstance().getUid();
        String userToken=MyApplication.getInstance().getToken();
        if (TextUtils.isEmpty(userId)||TextUtils.isEmpty(userToken)){
            LogUtils.e("connectionLost","用户id或者token为空无法重连");
            return;
        }
        MyApplication.getInstance().getMqttService().mqttConnection(MyApplication.getInstance().getMqttService().getMqttAndroidClient(),userId,userToken);
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        //消息到达
        String mMqttMessage= new String(message.getPayload());
        LogUtils.e("messageArrived",mMqttMessage+"---topic"+topic);
        RxBus.getDefault().post(mMqttMessage);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        //交互完成

    }
}
