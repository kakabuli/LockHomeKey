package com.kaadas.lock.publiclibrary.mqtt;

import org.eclipse.paho.client.mqttv3.IMqttToken;

public class PublishResult {
    private boolean publishResult;
    private IMqttToken asyncActionToken;


    public PublishResult(boolean publishResult, IMqttToken asyncActionToken) {
        this.publishResult = publishResult;
        this.asyncActionToken = asyncActionToken;
    }

    public PublishResult() {
    }

    @Override
    public String toString() {
        return "PublishResult{" +
                "publishResult=" + publishResult +
                ", asyncActionToken=" + asyncActionToken +
                '}';
    }

    public boolean isPublishResult() {
        return publishResult;
    }

    public void setPublishResult(boolean publishResult) {
        this.publishResult = publishResult;
    }

    public IMqttToken getAsyncActionToken() {
        return asyncActionToken;
    }

    public void setAsyncActionToken(IMqttToken asyncActionToken) {
        this.asyncActionToken = asyncActionToken;
    }
}
