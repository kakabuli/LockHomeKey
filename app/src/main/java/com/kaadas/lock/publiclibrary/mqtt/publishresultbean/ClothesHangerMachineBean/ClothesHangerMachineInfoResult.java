package com.kaadas.lock.publiclibrary.mqtt.publishresultbean.ClothesHangerMachineBean;

import java.io.Serializable;

public class ClothesHangerMachineInfoResult implements Serializable {
    /**
    {
"msgtype":"event",
"func":"wfevent",
"msgId":4,
"devtype":" KdsMxchipHanger ",
"lockId":"lockId",
"eventtype":"HangerInf",
"eventparams":{
 "hangerSN":"xxxxxx",//晾衣机序列号
"hangerVersion":"xxxxxx",//晾衣机版本号
"moduleSN":"xxxxxx",//模组序列号
 "moduleVersion":"xxxxxx",//模组版本号
},
"wfId":"wfuuid",
"timestamp":"1541468973342"
}
     */

    private String msgtype;
    private String func;
    private int msgId;
    private String devtype;
    private String lockId;
    private String eventtype;
    private String wfId;
    private String timestamp;
    private Params eventparams;


    private class Params implements Serializable{
        private String hangerSN;
        private String hangerVersion;
        private String moduleSN;
        private String moduleVersion;

        public String getHangerSN() {
            return hangerSN;
        }

        public void setHangerSN(String hangerSN) {
            this.hangerSN = hangerSN;
        }

        public String getHangerVersion() {
            return hangerVersion;
        }

        public void setHangerVersion(String hangerVersion) {
            this.hangerVersion = hangerVersion;
        }

        public String getModuleSN() {
            return moduleSN;
        }

        public void setModuleSN(String moduleSN) {
            this.moduleSN = moduleSN;
        }

        public String getModuleVersion() {
            return moduleVersion;
        }

        public void setModuleVersion(String moduleVersion) {
            this.moduleVersion = moduleVersion;
        }

        @Override
        public String toString() {
            return "Params{" +
                    "hangerSN='" + hangerSN + '\'' +
                    ", hangerVersion='" + hangerVersion + '\'' +
                    ", moduleSN='" + moduleSN + '\'' +
                    ", moduleVersion='" + moduleVersion + '\'' +
                    '}';
        }
    }

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public String getFunc() {
        return func;
    }

    public void setFunc(String func) {
        this.func = func;
    }

    public int getMsgId() {
        return msgId;
    }

    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }

    public String getDevtype() {
        return devtype;
    }

    public void setDevtype(String devtype) {
        this.devtype = devtype;
    }

    public String getLockId() {
        return lockId;
    }

    public void setLockId(String lockId) {
        this.lockId = lockId;
    }

    public String getEventtype() {
        return eventtype;
    }

    public void setEventtype(String eventtype) {
        this.eventtype = eventtype;
    }

    public String getWfId() {
        return wfId;
    }

    public void setWfId(String wfId) {
        this.wfId = wfId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Params getEventparams() {
        return eventparams;
    }

    public void setEventparams(Params eventparams) {
        this.eventparams = eventparams;
    }

    @Override
    public String toString() {
        return "ClothesHangerMachineInfoResult{" +
                "msgtype='" + msgtype + '\'' +
                ", func='" + func + '\'' +
                ", msgId=" + msgId +
                ", devtype='" + devtype + '\'' +
                ", lockId='" + lockId + '\'' +
                ", eventtype='" + eventtype + '\'' +
                ", wfId='" + wfId + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", eventparams=" + eventparams +
                '}';
    }
}
