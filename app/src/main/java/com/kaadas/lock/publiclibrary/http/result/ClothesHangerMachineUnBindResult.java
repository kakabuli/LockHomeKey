package com.kaadas.lock.publiclibrary.http.result;

public class ClothesHangerMachineUnBindResult extends BaseResult {
    /**
     *     "code": "200",
     *     "msg": "成功",
     *     "nowTime": 1609221599,
     *     "data": null
     */
    private String code;
    private String msg;
    private long nowTime;
    private Params data;

    private class Params{
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getMsg() {
        return msg;
    }

    @Override
    public void setMsg(String msg) {
        this.msg = msg;
    }

    public long getNowTime() {
        return nowTime;
    }

    public void setNowTime(long nowTime) {
        this.nowTime = nowTime;
    }

    public Params getData() {
        return data;
    }

    public void setData(Params data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ClothesHangerMachineUnBindResult{" +
                "code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                ", nowTime=" + nowTime +
                ", data=" + data +
                '}';
    }
}
