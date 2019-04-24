package com.kaadas.lock.publiclibrary.mqtt.util;

import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttData {

    private String func;
    private String topic;
    private String payload;
    private MqttMessage mqttMessage;
    private int messageId = -1;
    private int returnCode = -1;

    public MqttData(String func, String topic, String payload, MqttMessage mqttMessage,int messageId) {
        this.func = func;
        this.topic = topic;
        this.payload = payload;
        this.mqttMessage = mqttMessage;
        this.messageId = messageId;
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

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public int getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(int returnCode) {
        this.returnCode = returnCode;
    }
}
