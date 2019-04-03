package com.kaadas.lock.publiclibrary.http.temp;

/**
 * Created by Administrator on 2018/3/24.
 */

public class BaseResponse<T> {
    private int code;
    private String msg;
    private T data;

    public BaseResponse() {
    }

    public BaseResponse(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T model) {
        this.data = model;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;

    }

    /**
     * 202 201  查询设备是否绑定的返回
     * @return
     */
    public boolean isSuccess() {
        return code == 200 || code == 201 || code == 202;
    }

    @Override
    public String toString() {
        return "BleBaseResponse{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
