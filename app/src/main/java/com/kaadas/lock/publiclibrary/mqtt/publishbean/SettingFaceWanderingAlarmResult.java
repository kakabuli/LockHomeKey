package com.kaadas.lock.publiclibrary.mqtt.publishbean;

public class SettingFaceWanderingAlarmResult {
    /**
     * {
     *         "msgtype": "request",
     *             "msgId":4,
     *             "userId": "5cad4509dc9542c8",//APP下发
     *             "wfId": "WF123456789",
     *             "func": "setLock",
     *             "code": 200,
     *             "params": {
     *         KEY:VALUE
     *     },
     *         "timestamp": "13433333333",
     *     }
     */
    private String msgtype;
    private String userId;
    private int msgId;
    private String wfId;
    private String func;
    private int code;
    private ParamsBean params;
    private String timestamp;

    public SettingFaceWanderingAlarmResult(String msgtype, int msgId, String userId, String wfId, String func, ParamsBean params, String timestamp ,int code) {
        this.msgtype = msgtype;
        this.userId = userId;
        this.msgId = msgId;
        this.wfId = wfId;
        this.func = func;
        this.code = code;
        this.params = params;
        this.timestamp = timestamp;
    }

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

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public ParamsBean getParams() {
        return params;
    }

    public void setParams(ParamsBean params) {
        this.params = params;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public static class ParamsBean {

        private int hoverAlarm;
        private int hoverAlarmLevel;

        public ParamsBean(int hoverAlarm,int hoverAlarmLevel){
            this.hoverAlarm = hoverAlarm;
            this.hoverAlarmLevel = hoverAlarmLevel;
        }

        public int getHoverAlarm() {
            return hoverAlarm;
        }

        public void setHoverAlarm(int hoverAlarm) {
            this.hoverAlarm = hoverAlarm;
        }

        public int getHoverAlarmLevel() {
            return hoverAlarmLevel;
        }

        public void setHoverAlarmLevel(int hoverAlarmLevel) {
            this.hoverAlarmLevel = hoverAlarmLevel;
        }

        @Override
        public String toString() {
            return "ParamsBean{" +
                    "hoverAlarm=" + hoverAlarm +
                    ", hoverAlarmLevel=" + hoverAlarmLevel +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "SettingFaceWanderingAlarmResult{" +
                "msgtype='" + msgtype + '\'' +
                ", userId='" + userId + '\'' +
                ", msgId=" + msgId +
                ", wfId='" + wfId + '\'' +
                ", func='" + func + '\'' +
                ", code=" + code +
                ", params=" + params +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
