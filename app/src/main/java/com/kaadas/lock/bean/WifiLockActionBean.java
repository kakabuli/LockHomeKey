package com.kaadas.lock.bean;

public class WifiLockActionBean {

    /**
     * devtype : kdswflock
     * eventparams : {"amMode":1,"defences":1,"language":"zh/en","operatingMode":1,"safeMode":1,"volume":0,
     * "powerSave":0,"faceStatus":1,"openForce":1,"lockingMethod":1,"openDirection":1,"bodySensor":1,"TouchHandleStatus":0,"hoverAlarm":0,"hoverAlarmLevel":0}
     * eventtype : action
     * func : wfevent
     * lockId :  lockId
     * msgId : 4
     * msgtype : event
     * timestamp : 1541468973342
     * wfId : wfuuid
     */

    private String devtype;
    private EventparamsBean eventparams;
    private String eventtype;
    private String func;
    private String lockId;
    private int msgId;
    private String msgtype;
    private String timestamp;
    private String wfId;

    public String getDevtype() {
        return devtype;
    }

    public void setDevtype(String devtype) {
        this.devtype = devtype;
    }

    public EventparamsBean getEventparams() {
        return eventparams;
    }

    public void setEventparams(EventparamsBean eventparams) {
        this.eventparams = eventparams;
    }

    public String getEventtype() {
        return eventtype;
    }

    public void setEventtype(String eventtype) {
        this.eventtype = eventtype;
    }

    public String getFunc() {
        return func;
    }

    public void setFunc(String func) {
        this.func = func;
    }

    public String getLockId() {
        return lockId;
    }

    public void setLockId(String lockId) {
        this.lockId = lockId;
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getWfId() {
        return wfId;
    }

    public void setWfId(String wfId) {
        this.wfId = wfId;
    }

    public static class EventparamsBean {
        /**
         * amMode : 1
         * defences : 1
         * language : zh/en
         * operatingMode : 1
         * safeMode : 1
         * volume : 0
         * volLevel：0
         * powerSave : 0
         * faceStatus : 1
         * openForce  :1
         * lockingMethod:1
         * openDirection:1
         * bodySensor:1
         * touchHandleStatus:0
         * hoverAlarm:0
         * hoverAlarmLevel:0
         */

        private int amMode;
        private int defences;
        private String language;
        private int operatingMode;
        private int safeMode;
        private int volume;
        private int volLevel;
        private int powerSave;
        private int faceStatus;

        private int openForce;
        private int lockingMethod;
        private int openDirection;
        private int bodySensor;
        private int touchHandleStatus;
        private int hoverAlarm;
        private int hoverAlarmLevel;


        public int getAmMode() {
            return amMode;
        }

        public void setAmMode(int amMode) {
            this.amMode = amMode;
        }

        public int getDefences() {
            return defences;
        }

        public void setDefences(int defences) {
            this.defences = defences;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public int getOperatingMode() {
            return operatingMode;
        }

        public void setOperatingMode(int operatingMode) {
            this.operatingMode = operatingMode;
        }

        public int getSafeMode() {
            return safeMode;
        }

        public void setSafeMode(int safeMode) {
            this.safeMode = safeMode;
        }

        public int getVolume() {
            return volume;
        }

        public void setVolume(int volume) {
            this.volume = volume;
        }

        public int getVolLevel() {
            return volLevel;
        }

        public void setVolLevel(int volLevel) {
            this.volLevel = volLevel;
        }

        public int getPowerSave() {
            return powerSave;
        }

        public void setPowerSave(int powerSave) {
            this.powerSave = powerSave;
        }

        public int getFaceStatus() {
            return faceStatus;
        }

        public void setFaceStatus(int faceStatus) {
            this.faceStatus = faceStatus;
        }

        public int getOpenForce() {
            return openForce;
        }

        public void setOpenForce(int openForce) {
            this.openForce = openForce;
        }

        public int getLockingMethod() {
            return lockingMethod;
        }

        public void setLockingMethod(int lockingMethod) {
            this.lockingMethod = lockingMethod;
        }

        public int getOpenDirection() {
            return openDirection;
        }

        public void setOpenDirection(int openDirection) {
            this.openDirection = openDirection;
        }

        public int getBodySensor() {
            return bodySensor;
        }

        public void setBodySensor(int bodySensor) {
            this.bodySensor = bodySensor;
        }

        public int getTouchHandleStatus() {
            return touchHandleStatus;
        }

        public void setTouchHandleStatus(int touchHandleStatus) {
            touchHandleStatus = touchHandleStatus;
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
    }
}
