package com.kaadas.lock.publiclibrary.ble.responsebean;

/**
 * Create By lxj  on 2019/1/10
 * Describe
 */
public class BleBaseResponse<T> {
    private int cmd;
    private int state;
    private T data;
    private byte[] originalData;

    public BleBaseResponse(int cmd, int state, T data) {
        this.cmd = cmd;
        this.state = state;
        this.data = data;
    }

    public BleBaseResponse() {
    }

    public byte[] getOriginalData() {
        return originalData;
    }

    public void setOriginalData(byte[] originalData) {
        this.originalData = originalData;
    }

    public int getCmd() {
        return cmd;
    }

    public void setCmd(int cmd) {
        this.cmd = cmd;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "BleBaseResponse{" +
                "cmd=" + cmd +
                ", state=" + state +
                ", data=" + data +
                '}';
    }
}
