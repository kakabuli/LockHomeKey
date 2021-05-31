package com.kaadas.lock.publiclibrary.mqtt.publishbean;

public class SettingVoiceQuality {
    /**
     * {
     *         "msgtype": "request",
     *             "msgId":4,
     *             "userId": "5cad4509dc9542c8",//APP下发
     *             "wfId": "WF123456789",
     *             "func": "setLock",
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
    private ParamsBean params;
    private String timestamp;

    public SettingVoiceQuality(String msgtype, int msgId, String userId, String wfId, String func, ParamsBean params, String timestamp) {
        this.msgtype = msgtype;
        this.userId = userId;
        this.msgId = msgId;
        this.wfId = wfId;
        this.func = func;
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

        private int volLevel;

        public ParamsBean(int volLevel) {
            this.volLevel = volLevel;
        }

        public int getVolLevel() {
            return volLevel;
        }

        public void setVolLevel(int volLevel) {
            this.volLevel = volLevel;
        }

        @Override
        public String toString() {
            return "ParamsBean{" +
                    "volLevel=" + volLevel +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "SettingFaceWanderingAlarm{" +
                "msgtype='" + msgtype + '\'' +
                ", userId='" + userId + '\'' +
                ", msgId=" + msgId +
                ", wfId='" + wfId + '\'' +
                ", func='" + func + '\'' +
                ", params=" + params +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
