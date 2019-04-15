package com.kaadas.lock.publiclibrary.mqtt.publishresultbean;

public class BindGatewayBeanResult extends BaseBeanResult{


    /**
     * code : 200
     * msg : 成功
     * func : bindGatewayByUser
     * data : {}
     */

    private String code;
    private String msg;
    private String func;
    private DataBean data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getFunc() {
        return func;
    }

    public void setFunc(String func) {
        this.func = func;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
    }
}
