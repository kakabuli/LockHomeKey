package com.kaadas.lock.publiclibrary.mqtt.publishresultbean;

public class GatewayPasswordResultBean {

    /**
     * deviceId : devuuid
     * func : schedule
     * gwId :
     * msgId : 1
     * msgtype : request
     * params : {"action":"set/clear/get","dayMaskBits":1,"endHour":0,"endMinute":0,"scheduleId":0,"startHour":12,"startMinute":0,"type":"week/year ","userId":0,"zLocalEndT":0,"zLocalStartT":0}
     * returnCode : 0
     * returnData : {"daysMask":0,"endHour":1,"endMinute":1,"scheduleID":0,"scheduleStatus":0,"startHour":1,"status":0,"zigBeeLocalEndTime":1,"zigBeeLocalStartTime":1}
     * timestamp : 13433333333
     * userId :
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
         * action : set/clear/get
         * dayMaskBits : 1
         * endHour : 0
         * endMinute : 0
         * scheduleId : 0
         * startHour : 12
         * startMinute : 0
         * type : week/year
         * userId : 0
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
        /**
         * daysMask : 0
         * endHour : 1
         * endMinute : 1
         * scheduleID : 0
         * scheduleStatus : 0
         * startHour : 1
         * status : 0
         * zigBeeLocalEndTime : 1
         * zigBeeLocalStartTime : 1
         */

        private int startMinute;
        private int daysMask;
        private int endHour;
        private int endMinute;
        private int scheduleID;
        private int scheduleStatus;
        private int startHour;
        private int status;
        private int zigBeeLocalEndTime;
        private int zigBeeLocalStartTime;

        public int getDaysMask() {
            return daysMask;
        }

        public void setDaysMask(int daysMask) {
            this.daysMask = daysMask;
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

        public int getScheduleID() {
            return scheduleID;
        }

        public void setScheduleID(int scheduleID) {
            this.scheduleID = scheduleID;
        }

        public int getScheduleStatus() {
            return scheduleStatus;
        }

        public void setScheduleStatus(int scheduleStatus) {
            this.scheduleStatus = scheduleStatus;
        }

        public int getStartHour() {
            return startHour;
        }

        public void setStartHour(int startHour) {
            this.startHour = startHour;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getZigBeeLocalEndTime() {
            return zigBeeLocalEndTime;
        }

        public void setZigBeeLocalEndTime(int zigBeeLocalEndTime) {
            this.zigBeeLocalEndTime = zigBeeLocalEndTime;
        }

        public int getStartMinute() {
            return startMinute;
        }

        public void setStartMinute(int startMinute) {
            this.startMinute = startMinute;
        }

        public int getZigBeeLocalStartTime() {
            return zigBeeLocalStartTime;
        }

        public void setZigBeeLocalStartTime(int zigBeeLocalStartTime) {
            this.zigBeeLocalStartTime = zigBeeLocalStartTime;
        }
    }

    @Override
    public String toString() {
        return "GatewayPasswordResultBean{" +
                "deviceId='" + deviceId + '\'' +
                ", func='" + func + '\'' +
                ", gwId='" + gwId + '\'' +
                ", msgId=" + msgId +
                ", msgtype='" + msgtype + '\'' +
                ", params=" + params +
                ", returnCode=" + returnCode +
                ", returnData=" + returnData +
                ", timestamp='" + timestamp + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}
