package com.kaadas.lock.publiclibrary.mqtt.publishresultbean;

public class SetPlanResultBean {

    /**
     * deviceId : TEST190100004
     * func : schedule
     * gwId : GW03193510012
     * msgId : 24
     * msgtype : respone
     * params : {"action":"set","dayMaskBits":30,"endHour":20,"endMinute":39,"scheduleId":2,"startHour":17,"startMinute":39,"type":"week;","userId":2,"zLocalEndT":0,"zLocalStartT":0}
     * returnCode : 200
     * returnData : {"status":0}
     * timestamp : 1574768422143
     * userId : 5beac35235736f25e9fe2375
     */

    private String deviceId;
    private String func;
    private String gwId;
    private int msgId;
    private String msgtype;
    private ParamsBean params;
    private String returnCode;
    private ReturnDataBean returnData;
    private String timestamp;
    private String userId;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getFunc() {
        return func;
    }

    public void setFunc(String func) {
        this.func = func;
    }

    public String getGwId() {
        return gwId;
    }

    public void setGwId(String gwId) {
        this.gwId = gwId;
    }

    public int getMsgId() {
        return msgId;
    }

    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public ParamsBean getParams() {
        return params;
    }

    public void setParams(ParamsBean params) {
        this.params = params;
    }

    public String getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }

    public ReturnDataBean getReturnData() {
        return returnData;
    }

    public void setReturnData(ReturnDataBean returnData) {
        this.returnData = returnData;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public static class ParamsBean {
        /**
         * action : set
         * dayMaskBits : 30
         * endHour : 20
         * endMinute : 39
         * scheduleId : 2
         * startHour : 17
         * startMinute : 39
         * type : week;
         * userId : 2
         * zLocalEndT : 0
         * zLocalStartT : 0
         */

        private String action;
        private int dayMaskBits;
        private int endHour;
        private int endMinute;
        private int scheduleId;
        private int startHour;
        private int startMinute;
        private String type;
        private int userId;
        private int zLocalEndT;
        private int zLocalStartT;

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public int getDayMaskBits() {
            return dayMaskBits;
        }

        public void setDayMaskBits(int dayMaskBits) {
            this.dayMaskBits = dayMaskBits;
        }

        public int getEndHour() {
            return endHour;
        }

        public void setEndHour(int endHour) {
            this.endHour = endHour;
        }

        public int getEndMinute() {
            return endMinute;
        }

        public void setEndMinute(int endMinute) {
            this.endMinute = endMinute;
        }

        public int getScheduleId() {
            return scheduleId;
        }

        public void setScheduleId(int scheduleId) {
            this.scheduleId = scheduleId;
        }

        public int getStartHour() {
            return startHour;
        }

        public void setStartHour(int startHour) {
            this.startHour = startHour;
        }

        public int getStartMinute() {
            return startMinute;
        }

        public void setStartMinute(int startMinute) {
            this.startMinute = startMinute;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public int getZLocalEndT() {
            return zLocalEndT;
        }

        public void setZLocalEndT(int zLocalEndT) {
            this.zLocalEndT = zLocalEndT;
        }

        public int getZLocalStartT() {
            return zLocalStartT;
        }

        public void setZLocalStartT(int zLocalStartT) {
            this.zLocalStartT = zLocalStartT;
        }
    }

    public static class ReturnDataBean {
        /**
         * status : 0
         */

        private int status;

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }
}
