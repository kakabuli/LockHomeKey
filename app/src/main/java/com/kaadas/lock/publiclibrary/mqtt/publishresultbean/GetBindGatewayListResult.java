package com.kaadas.lock.publiclibrary.mqtt.publishresultbean;

import android.telecom.GatewayInfo;

import com.kaadas.lock.publiclibrary.bean.ServerGatewayInfo;

import java.util.List;

public class GetBindGatewayListResult extends BaseBeanResult {

    /**
     * code : 200
     * msg : 成功
     * func : gatewayBindList
     * data : [{"deviceSN":"GW01182510033","deviceNickName":"GW01182510033","adminuid":"5c3d370f35736f6e8f9ba676","adminName":"8617512018193","adminNickname":"8617512018193","isAdmin":1,"meUsername":"cf85c146123741c58109ebb3ee786c59","mePwd":"369f70f1b67c481e964004e7ade40f34","meBindState":1}]
     */

    private String code;
    private String msg;
    private String func;
    private List<ServerGatewayInfo> data;

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

    public List<ServerGatewayInfo> getData() {
        return data;
    }

    public void setData(List<ServerGatewayInfo> data) {
        this.data = data;
    }




}
