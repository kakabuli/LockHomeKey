package com.kaadas.lock.publiclibrary.mqtt.publishresultbean;

import java.io.Serializable;

public class CatEyeInfoBeanResult implements Serializable {


    /**
     * deviceId : CH01183910013
     * func : basicInfo
     * gwId : GW01182510033
     * msgId : 1
     * msgtype : respone
     * params : {}
     * returnCode : 200
     * returnData : {"HW":"M04U02K02S02AC02F02","MCU":"0","SW":"orangecat-1.4.3","T200":"18121918","bellCount":2,"bellVolume":1,"curBellNum":0,"ipaddr":"192.168.168.204","macaddr":"0C:9A:42:B7:8D:0E","maxBellNum":1,"mbStatus":0,"pirEnable":0,"pirSilent":0,"pirWander":"1,5","power":85,"resolution":"960x540","sdStatus":0,"wifiStrength":0}
     * timestamp : 1557303904479
     * userId : 5cb34da6dc938905c308c083
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
    }

    public static class ReturnDataBean {
        /**
         * HW : M04U02K02S02AC02F02
         * MCU : 0
         * SW : orangecat-1.4.3
         * T200 : 18121918
         * bellCount : 2
         * bellVolume : 1
         * curBellNum : 0
         * ipaddr : 192.168.168.204
         * macaddr : 0C:9A:42:B7:8D:0E
         * maxBellNum : 1
         * mbStatus : 0
         * pirEnable : 0
         * pirSilent : 0
         * pirWander : 1,5
         * power : 85
         * resolution : 960x540
         * sdStatus : 0
         * wifiStrength : 0
         */

        private String HW;
        private String MCU;
        private String SW;
        private String T200;
        private int bellCount;
        private int bellVolume;
        private int curBellNum;
        private String ipaddr;
        private String macaddr;
        private int maxBellNum;
        private int mbStatus;
        private int pirEnable;
        private int pirSilent;
        private String pirWander;
        private int power;
        private String resolution;
        private int sdStatus;
        private int wifiStrength;

        public String getHW() {
            return HW;
        }

        public void setHW(String HW) {
            this.HW = HW;
        }

        public String getMCU() {
            return MCU;
        }

        public void setMCU(String MCU) {
            this.MCU = MCU;
        }

        public String getSW() {
            return SW;
        }

        public void setSW(String SW) {
            this.SW = SW;
        }

        public String getT200() {
            return T200;
        }

        public void setT200(String T200) {
            this.T200 = T200;
        }

        public int getBellCount() {
            return bellCount;
        }

        public void setBellCount(int bellCount) {
            this.bellCount = bellCount;
        }

        public int getBellVolume() {
            return bellVolume;
        }

        public void setBellVolume(int bellVolume) {
            this.bellVolume = bellVolume;
        }

        public int getCurBellNum() {
            return curBellNum;
        }

        public void setCurBellNum(int curBellNum) {
            this.curBellNum = curBellNum;
        }

        public String getIpaddr() {
            return ipaddr;
        }

        public void setIpaddr(String ipaddr) {
            this.ipaddr = ipaddr;
        }

        public String getMacaddr() {
            return macaddr;
        }

        public void setMacaddr(String macaddr) {
            this.macaddr = macaddr;
        }

        public int getMaxBellNum() {
            return maxBellNum;
        }

        public void setMaxBellNum(int maxBellNum) {
            this.maxBellNum = maxBellNum;
        }

        public int getMbStatus() {
            return mbStatus;
        }

        public void setMbStatus(int mbStatus) {
            this.mbStatus = mbStatus;
        }

        public int getPirEnable() {
            return pirEnable;
        }

        public void setPirEnable(int pirEnable) {
            this.pirEnable = pirEnable;
        }

        public int getPirSilent() {
            return pirSilent;
        }

        public void setPirSilent(int pirSilent) {
            this.pirSilent = pirSilent;
        }

        public String getPirWander() {
            return pirWander;
        }

        public void setPirWander(String pirWander) {
            this.pirWander = pirWander;
        }

        public int getPower() {
            return power;
        }

        public void setPower(int power) {
            this.power = power;
        }

        public String getResolution() {
            return resolution;
        }

        public void setResolution(String resolution) {
            this.resolution = resolution;
        }

        public int getSdStatus() {
            return sdStatus;
        }

        public void setSdStatus(int sdStatus) {
            this.sdStatus = sdStatus;
        }

        public int getWifiStrength() {
            return wifiStrength;
        }

        public void setWifiStrength(int wifiStrength) {
            this.wifiStrength = wifiStrength;
        }
    }
}


