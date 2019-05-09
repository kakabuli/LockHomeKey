package com.kaadas.lock.publiclibrary.mqtt.eventbean;

/**
 * 猫眼上报事件
 */
public class CatEyeEventBean {

    /**
     * deviceId : devuuid
     * devtype : devtype
     * eventcode : 2
     * eventparams : {"devecode":0,"devetype":"pir","devinfo":{"ename":"pir","etype":"alarm","params":{"url":"xxxx/xxxx/filename"}}}
     * eventtype : alarm
     * func : gwevent
     * gwId :
     * msgid : msgid
     * msgtype : event
     * timestamp : 134211111111
     */

    private String deviceId;
    private String devtype;
    private int eventcode;
    private EventparamsBean eventparams;
    private String eventtype;
    private String func;
    private String gwId;
    private String msgid;
    private String msgtype;
    private String timestamp;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDevtype() {
        return devtype;
    }

    public void setDevtype(String devtype) {
        this.devtype = devtype;
    }

    public int getEventcode() {
        return eventcode;
    }

    public void setEventcode(int eventcode) {
        this.eventcode = eventcode;
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

    public String getGwId() {
        return gwId;
    }

    public void setGwId(String gwId) {
        this.gwId = gwId;
    }

    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
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

    public static class EventparamsBean {
        /**
         * devecode : 0
         * devetype : pir
         * devinfo : {"ename":"pir","etype":"alarm","params":{"url":"xxxx/xxxx/filename"}}
         */

        private int devecode;
        private String devetype;
        private DevinfoBean devinfo;

        public int getDevecode() {
            return devecode;
        }

        public void setDevecode(int devecode) {
            this.devecode = devecode;
        }

        public String getDevetype() {
            return devetype;
        }

        public void setDevetype(String devetype) {
            this.devetype = devetype;
        }

        public DevinfoBean getDevinfo() {
            return devinfo;
        }

        public void setDevinfo(DevinfoBean devinfo) {
            this.devinfo = devinfo;
        }

        public static class DevinfoBean {
            /**
             * ename : pir
             * etype : alarm
             * params : {"url":"xxxx/xxxx/filename"}
             */

            private String ename;
            private String etype;
            private ParamsBean params;

            public String getEname() {
                return ename;
            }

            public void setEname(String ename) {
                this.ename = ename;
            }

            public String getEtype() {
                return etype;
            }

            public void setEtype(String etype) {
                this.etype = etype;
            }

            public ParamsBean getParams() {
                return params;
            }

            public void setParams(ParamsBean params) {
                this.params = params;
            }

            public static class ParamsBean {
                /**
                 * url : xxxx/xxxx/filename
                 */
                private String url;

                public String getUrl() {
                    return url;
                }

                public void setUrl(String url) {
                    this.url = url;
                }
            }
        }
    }
}
