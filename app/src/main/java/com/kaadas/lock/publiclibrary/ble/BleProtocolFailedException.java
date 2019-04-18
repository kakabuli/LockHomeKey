package com.kaadas.lock.publiclibrary.ble;

/**
 * Create By lxj  on 2019/2/25
 * Describe  蓝牙协议错误，发送指令成功，但是蓝牙模块返回的数据是错的
 * err
 */
public class BleProtocolFailedException extends Throwable {
    /**
     * 蓝牙模块返回的非0status值
     */
   private int errorCode = 0;

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public BleProtocolFailedException(int errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String toString() {
        return "错误码是   " + errorCode
               ;
    }
}
