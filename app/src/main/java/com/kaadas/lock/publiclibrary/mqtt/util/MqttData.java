package com.kaadas.lock.publiclibrary.mqtt.util;

import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttData {

    private String func;
    private String topic;
    private String payload;
    private MqttMessage mqttMessage;

    public MqttData(String func, String topic, String payload, MqttMessage mqttMessage) {
        this.func = func;
        this.topic = topic;
        this.payload = payload;
        this.mqttMessage = mqttMessage;
    }

    public MqttData() {
    }

    public String getFunc() {
        return func;
    }

    public void setFunc(String func) {
        this.func = func;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public MqttMessage getMqttMessage() {
        return mqttMessage;
    }

    public void setMqttMessage(MqttMessage mqttMessage) {
        this.mqttMessage = mqttMessage;
    }


}
