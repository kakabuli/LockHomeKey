package com.kaadas.lock.publiclibrary.mqtt.publishbean;

import com.kaadas.lock.publiclibrary.bean.SingleFireSwitchInfo;

import java.io.Serializable;

public class AddSingleFireSwitchBean implements Serializable {


    /**
     * func : addSwitch
     * msgId
     * msgtype : request
     * params : {}
     * userId : 5b8cc0d735736f62253379fc
     * wfId : S1M0202410005
     * timestamp : 1592039661108
     *
     * return
     *
     * code : 200
     * func : addSwitch
     * msgId :
     * msgtype : respone
     * params : {}
     * timestamp : 1592039660
     * type : 2
     * userId : 5b8cc0d735736f62253379fc
     * wfId : S1M0202410005
     * mac :
     */

    private String msgtype;
    private String userId;
    private int msgId;
    private String wfId;
    private String func;
    private SingleFireSwitchInfo params;
    private long timestamp;
    private String code;
    private int type;
    private String mac;


    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getMsgId() {
        return msgId;
    }

    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }

    public String getWfId() {
        return wfId;
    }

    public void setWfId(String wfId) {
        this.wfId = wfId;
    }


    public String getFunc() {
        return func;
    }

    public void setFunc(String func) {
        this.func = func;
    }

    public SingleFireSwitchInfo getParams() {
        return params;
    }

    public void setParams(SingleFireSwitchInfo params) {
        this.params = params;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMac() {
        return mac;
    }

    public void setMc(String mac) {
        this.mac = mac;
    }

    public AddSingleFireSwitchBean(String msgtype, String userId, int msgId, String wfId, String func, SingleFireSwitchInfo params, long timestamp) {
        this.msgtype = msgtype;
        this.userId = userId;
        this.msgId = msgId;
        this.wfId = wfId;
        this.func = func;
        this.params = params;
        this.timestamp = timestamp;
    }

    public AddSingleFireSwitchBean() {
    }
}
