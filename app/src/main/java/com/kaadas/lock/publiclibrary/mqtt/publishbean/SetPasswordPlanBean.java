package com.kaadas.lock.publiclibrary.mqtt.publishbean;

public class SetPasswordPlanBean {


    /**
     * deviceId : devuuid
     * func : schedule
     * gwId :
     * msgId : 1
     * msgtype : request
     * params : {"action":"set","dayMaskBits":1,"endHour":3,"endMinute":4,"scheduleId":0,"startHour":1,"startMinute":2,"type":"weekr","userId":0,"zLocalEndT":"","zLocalStartT":""}
     * returnCode : 0
     * returnData : {}
     * timestamp : 13433333333
     * userId :
     */

    private String deviceId;
    private String func;
    private String gwId;
    private int msgId;
    private String msgtype;
    private ParamsBean params;
    private int returnCode;
    private ReturnDataBean returnData;
    private String timestamp;
    private String userId;

    public SetPasswordPlanBean(String deviceId, String func, String gwId, int msgId, String msgtype, ParamsBean params, int returnCode, ReturnDataBean returnData, String timestamp, String userId) {
        this.deviceId = deviceId;
        this.func = func;
        this.gwId = gwId;
        this.msgId = msgId;
        this.msgtype = msgtype;
        this.params = params;
        this.returnCode = returnCode;
        this.returnData = returnData;
        this.timestamp = timestamp;
        this.userId = userId;
    }

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

    public int getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(int returnCode) {
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

//        年计划的构造方法


        public ParamsBean(String action, int scheduleId, String type, int userId, int zLocalEndT, int  zLocalStartT) {
            this.action = action;
            this.scheduleId = scheduleId;
            this.type = type;
            this.userId = userId;
            this.zLocalEndT = zLocalEndT;
            this.zLocalStartT = zLocalStartT;
        }



        //全部
        public ParamsBean(String action, int dayMaskBits, int endHour, int endMinute, int scheduleId,
                          int startHour, int startMinute, String type, int userId, long zLocalEndT, long zLocalStartT) {
            this.action = action;
            this.dayMaskBits = dayMaskBits;
            this.endHour = endHour;
            this.endMinute = endMinute;
            this.scheduleId = scheduleId;
            this.startHour = startHour;
            this.startMinute = startMinute;
            this.type = type;
            this.userId = userId;
            this.zLocalEndT = zLocalEndT;
            this.zLocalStartT = zLocalStartT;
        }

        /**
         * action : set
         * dayMaskBits : 1
         * endHour : 3
         * endMinute : 4
         * scheduleId : 0
         * startHour : 1
         * startMinute : 2
         * type : weekr
         * userId : 0
         * zLocalEndT :
         * zLocalStartT :
         */



        private String action;
        private int dayMaskBits;
        private int endHour;
        private int endMinute;
        private int scheduleId;  //计划ID
        private int startHour;
        private int startMinute;
        private String type; //点计划  或者周计划
        private int userId;
        private long zLocalEndT;
        private long zLocalStartT;

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

        public long getZLocalEndT() {
            return zLocalEndT;
        }

        public void setZLocalEndT(long zLocalEndT) {
            this.zLocalEndT = zLocalEndT;
        }

        public long getZLocalStartT() {
            return zLocalStartT;
        }

        public void setZLocalStartT(long zLocalStartT) {
            this.zLocalStartT = zLocalStartT;
        }
    }

    public static class ReturnDataBean {
    }
}
