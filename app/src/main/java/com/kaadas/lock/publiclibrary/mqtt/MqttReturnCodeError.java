package com.kaadas.lock.publiclibrary.mqtt;

public class MqttReturnCodeError extends Throwable {

    String returnCode ;


    public MqttReturnCodeError(  String returnCode) {
        super(""+returnCode);
        this.returnCode = returnCode;
    }

    public String getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }
}
