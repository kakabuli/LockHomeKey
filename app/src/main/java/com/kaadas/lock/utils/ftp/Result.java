package com.kaadas.lock.utils.ftp;

/**
 * Created by mopangyou on 2016/11/28.
 *
 * @创建者 mopangyou
 * @创建时间 2016/11/28 14:22
 * @描述 执行每个动作后响应的结果，包括成功和失败
 */

public class Result {
    private String response; //响应的内容
    private boolean succeed; //响应的结果
    private String time; //响应的时间
    public Result(){

    }
    public Result(String response) {
        this.response = response;
    }


    public Result(boolean succeed, String time, String response) {
        this.succeed = succeed;
        this.time = time;
        this.response = response;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public boolean isSucceed() {
        return succeed;
    }

    public void setSucceed(boolean succeed) {
        this.succeed = succeed;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
