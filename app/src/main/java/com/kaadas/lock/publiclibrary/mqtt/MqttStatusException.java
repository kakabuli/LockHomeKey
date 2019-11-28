package com.kaadas.lock.publiclibrary.mqtt;

public class MqttStatusException extends Throwable {
    /**
     * 蓝牙模块返回的非0status值
     */
    private int  errorCode = 0;

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public MqttStatusException(int errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String toString() {
        return "错误码是   " + errorCode
                ;
    }
}
