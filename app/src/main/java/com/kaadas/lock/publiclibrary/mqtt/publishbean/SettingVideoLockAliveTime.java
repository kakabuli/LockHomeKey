package com.kaadas.lock.publiclibrary.mqtt.publishbean;

import java.io.Serializable;
import java.util.Arrays;

public class SettingVideoLockAliveTime implements Serializable {


    /**
     * {
     * "msgtype": "request", "msgId":4, "userId": "5cad4509dc938989e2f542c8",//AP P 下发
     * "wfId": "WF123456789",
     * "func":"setCamera",
     * "params": {
     * " keep_alive_status":0/1,   //0 关闭 , 1 开启
     * "alive_time": {
     * "keep_alive_snooze":[1,2,3,4,5,6,7],  //数字代表星期
     * "snooze_start_time":0,          //开始时间
     * "snooze_end_time":86400      //结束时间
     * }
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
    private int code;

    public SettingVideoLockAliveTime(String msgtype, int msgId, String userId, String wfId, String func, ParamsBean params, String timestamp,int code) {
        this.msgtype = msgtype;
        this.userId = userId;
        this.msgId = msgId;
        this.wfId = wfId;
        this.func = func;
        this.params = params;
        this.timestamp = timestamp;
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
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
         "params": { " keep_alive_status":0/1,   //0 关闭 , 1 开启 "alive_time": { "keep_alive_snooze":[1,2,3,4,5,6,7],  //数字代表星期 "snooze_start_time":0,          //开始时间 "snooze_end_time":86400      //结束时间 }
         */


        private int keep_alive_status;
        private AliveTimeBean alive_time;

        public ParamsBean(int keep_alive_status, AliveTimeBean alive_time) {
            this.keep_alive_status = keep_alive_status;
            this.alive_time = alive_time;
        }

        public int getKeep_alive_status() {
            return keep_alive_status;
        }

        public void setKeep_alive_status(int keep_alive_status) {
            this.keep_alive_status = keep_alive_status;
        }

        public AliveTimeBean getAlive_time() {
            return alive_time;
        }

        public void setAlive_time(AliveTimeBean alive_time) {
            this.alive_time = alive_time;
        }

        @Override
        public String toString() {
            return "ParamsBean{" +
                    "keep_alive_status=" + keep_alive_status +
                    ", alive_time=" + alive_time +
                    '}';
        }
    }

    public static class AliveTimeBean {
        private int[] keep_alive_snooze;
        private int snooze_start_time;
        private int snooze_end_time;

        public AliveTimeBean(int[] keep_alive_snooze, int snooze_start_time, int snooze_end_time) {
            this.keep_alive_snooze = keep_alive_snooze;
            this.snooze_start_time = snooze_start_time;
            this.snooze_end_time = snooze_end_time;
        }

        public int[] getKeep_alive_snooze() {
            return keep_alive_snooze;
        }

        public void setKeep_alive_snooze(int[] keep_alive_snooze) {
            this.keep_alive_snooze = keep_alive_snooze;
        }

        public int getSnooze_start_time() {
            return snooze_start_time;
        }

        public void setSnooze_start_time(int snooze_start_time) {
            this.snooze_start_time = snooze_start_time;
        }

        public int getSnooze_end_time() {
            return snooze_end_time;
        }

        public void setSnooze_end_time(int snooze_end_time) {
            this.snooze_end_time = snooze_end_time;
        }

        @Override
        public String toString() {
            return "AliveTimeBean{" +
                    "keep_alive_snooze=" + Arrays.toString(keep_alive_snooze) +
                    ", snooze_start_time=" + snooze_start_time +
                    ", snooze_end_time=" + snooze_end_time +
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
