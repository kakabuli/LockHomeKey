package com.kaadas.lock.publiclibrary.mqtt;

public class MqttBackCodeException extends Throwable {
    /**
     * 蓝牙模块返回的非0status值
     */
    private String errorCode = "-1";

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public MqttBackCodeException(String errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String toString() {
        return "错误码是   " + errorCode
                ;
    }
}
