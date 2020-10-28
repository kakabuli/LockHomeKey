package com.kaadas.lock.publiclibrary.mqtt.publishbean;

import java.io.Serializable;
import java.util.Arrays;

public class SettingVideoLockPir implements Serializable {


    /**
     * {
     * "msgtype": "request", "msgId":4, "userId": "5cad4509dc938989e2f542c8",//AP P 下发
     * "wfId": "WF123456789",
     * "func":"setCamera",
     * //徘徊报警设置
     * "params": {
     *  "stay_status":0/1,//徘徊检测开关 状态
     *  "setPir":{
     *      "stay_time":10,//徘徊时间检测, 范围 10-60 秒
     *      "pir_sen":100  //PIR 灵敏度,范 围 0-100
     *      }
     *   }
     * },
     * "timestamp": "13433333333",
     * }
     * //
     */

    private String msgtype;
    private String userId;
    private int msgId;
    private String wfId;
    private String func;
    private ParamsBean params;
    private String timestamp;

    public SettingVideoLockPir(String msgtype, int msgId, String userId, String wfId, String func, ParamsBean params, String timestamp,int code) {
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
        /**
         * "params": {
         *      *  "stay_status":0/1,//徘徊检测开关 状态
         *      *  "setPir":{
         *      *      "stay_time":10,//徘徊时间检测, 范围 10-60 秒
         *      *      "pir_sen":100  //PIR 灵敏度,范 围 0-100
         *      *      }
         *      *   }
         */

        private int stay_status;
        private PIRBean setPir;

        public ParamsBean(int stay_status, PIRBean setPir) {
            this.stay_status = stay_status;
            this.setPir = setPir;
        }

        public int getStay_status() {
            return stay_status;
        }

        public void setStay_status(int stay_status) {
            this.stay_status = stay_status;
        }

        public PIRBean getSetPir() {
            return setPir;
        }

        public void setSetPir(PIRBean setPir) {
            this.setPir = setPir;
        }

        @Override
        public String toString() {
            return "ParamsBean{" +
                    "stay_status=" + stay_status +
                    ", setPir=" + setPir +
                    '}';
        }
    }

    public static class PIRBean{
        private int stay_time;
        private int pir_sen;

        public PIRBean(int stay_time, int pir_sen) {
            this.stay_time = stay_time;
            this.pir_sen = pir_sen;
        }

        public int getStay_time() {
            return stay_time;
        }

        public void setStay_time(int stay_time) {
            this.stay_time = stay_time;
        }

        public int getPir_sen() {
            return pir_sen;
        }

        public void setPir_sen(int pir_sen) {
            this.pir_sen = pir_sen;
        }

        @Override
        public String toString() {
            return "PIRBean{" +
                    "stay_time=" + stay_time +
                    ", pir_sen=" + pir_sen +
                    '}';
        }
    }


    @Override
    public String toString() {
        return "SettingVideoModuleFuncBean{" +
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
