package com.kaadas.lock.publiclibrary.mqtt.publishbean;

import com.kaadas.lock.publiclibrary.bean.SingleFireSwitchInfo;

import java.io.Serializable;

public class SetSingleFireSwitchBean implements Serializable {


    /**
     * msgtype : request
     * userId :
     * msgId : 1
     * wfId: WF123456789
     * func : setSwitch
     * params : {"switchEn" : 1,                          //驱动使能开关
     *           "macaddr" : "a4c1386ac7d6",
     *           "switchArray" : [
     *             {
     *               "startTime" : 0,                      //开始时间0-1440分
     *               "timeEn" : 0,                         //键位使能
     *               "stopTime" : 0,                       //结束时间0-1440分
     *               "week" : 0,                           // 0xFF 	，周策略 0B 0111 1111 如：bit0->周日 bit1->周一 bit6->周六 无策略时全为0 （就是策励开关全为0）
     *               "type" : 1,                           //键号
     *               "nickname" : "哈哈1"                  //昵称
     *             },
     *             {
     *               "startTime" : 0,
     *               "timeEn" : 0,
     *               "stopTime" : 0,
     *               "week" : 0,
     *               "type" : 2,
     *               "nickname" : "版本2"
     *             }
     *           ],
     *           "switchBind" : 1591846484              //绑定时间
     *           }
     * timestamp : 13433333333
     *
     *
     *
     * "msgtype": "respone",
     * 	"msgId":4,
     * 	"userId": "5cad4509dc938989e2f542c8",//APP下发
     *     "wfId": "WF123456789",
     *     "func": "setSwitch",
     * 	"code": "200",
     *     "params": {
     *     },
     *     "timestamp": "13433333333" ，
     *
     *          /////////////////////
     *
     *   * returnCode : 0
     *      * returnData : {}
     *
     *      /////////////////////////////
     *
     *
     * func = setSwitch;
     *     msgId = 3;
     *     msgtype = request;
     *     params =     {
     *         switchArray =         (
     *                         {
     *                 nickname = "\U54c8\U54c81";
     *                 startTime = 0;
     *                 stopTime = 0;
     *                 timeEn = 0;
     *                 type = 1;
     *                 week = 0;
     *             },
     *                         {
     *                 nickname = "\U7248\U672c2";
     *                 startTime = 0;
     *                 stopTime = 0;
     *                 timeEn = 0;
     *                 type = 2;
     *                 week = 0;
     *             }
     *         );
     *         switchEn = 0;
     *     };
     *     timestamp = 1592036855788;
     *     userId = 5b8cc0d735736f62253379fc;
     *     wfId = S1M0202410005;
     * }
     *
     *
     *      //////////////////
     *
     * /////
     *
     * 2020-06-13 17:14:21.109019+0800 KaadasLock[598:264782] mqtttask--------switch==--addSwitch--topicParams=={
     *     func = addSwitch;
     *     msgId = 2;
     *     msgtype = request;
     *     params =     {
     *     };
     *     timestamp = 1592039661108;
     *     userId = 5b8cc0d735736f62253379fc;
     *     wfId = S1M0202410005;
     * }
     *
     *
     * code = 200;
     *     func = addSwitch;
     *     mac = a4c1386ac7d6;
     *     msgId = 2;
     *     msgtype = respone;
     *     params =     {
     *     };
     *     timestamp = 1592039660;
     *     type = 2;
     *     userId = 5b8cc0d735736f62253379fc;
     *     wfId = S1M0202410005;
     *
     *     ////////////
     *
     *
     */


    private String msgtype;
    private String userId;
    private int msgId;
    private String wfId;
    private String func;
    private SingleFireSwitchInfo params;
    private String timestamp;
    private String code;

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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public SetSingleFireSwitchBean(String msgtype, String userId, int msgId, String wfId, String func, SingleFireSwitchInfo params, String timestamp) {
        this.msgtype = msgtype;
        this.userId = userId;
        this.msgId = msgId;
        this.wfId = wfId;
        this.func = func;
        this.params = params;
        this.timestamp = timestamp;
    }

    public SetSingleFireSwitchBean() {
    }
}
