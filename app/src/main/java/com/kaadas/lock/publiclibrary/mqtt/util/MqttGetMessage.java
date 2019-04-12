package com.kaadas.lock.publiclibrary.mqtt.util;

import com.google.gson.Gson;

import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttGetMessage {
    //获取MqttMessage
    public static MqttMessage getMessage(Object o) {
        String payload = new Gson().toJson(o);
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setQos(2);
        mqttMessage.setRetained(false);
        mqttMessage.setId(MqttConstant.MESSAGE_ID++);;
        mqttMessage.setPayload(payload.getBytes());
        return mqttMessage;
    }


}
