package com.kaadas.lock.publiclibrary.mqtt;

import com.google.gson.Gson;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.BindGatewayBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.GetBindGatewayListBean;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;

import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttCommandFactory {

    //MessageId
    public static int MESSAGE_ID = 0;

    /**
     * 绑定网关
     *
     * @param uid
     * @param Sn  网关的SN
     * @return
     */
    public static MqttMessage bindGateway(String uid, String Sn) {
        BindGatewayBean bindGatewayBean = new BindGatewayBean(uid, MqttConstant.BIND_GATEWAY, Sn);
        return getMessage(bindGatewayBean);
    }

    /**
     * 绑定网关
     *
     * @param uid
     * @return
     */
    public static MqttMessage getGatewayList(String uid) {
        GetBindGatewayListBean bindGatewayBean = new GetBindGatewayListBean(uid, MqttConstant.GET_BIND_GATEWAY_LIST);
        return getMessage(bindGatewayBean);
    }


    public static MqttMessage getMessage(Object o) {
        String payload = new Gson().toJson(o);
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setQos(2);
        mqttMessage.setRetained(false);
        mqttMessage.setId(MESSAGE_ID++);
        mqttMessage.setPayload(payload.getBytes());
        return mqttMessage;
    }

}
