package com.kaadas.lock.publiclibrary.mqtt.publishresultbean;

public class GetBindGatewayStatusResult extends BaseBeanResult{

    /**
     * func : gatewayState
     * data : {"state":"offline"}
     * devuuid : GW01182510141
     */

    private String func;
    private DataBean data;
    private String devuuid;

    public String getFunc() {
        return func;
    }

    public void setFunc(String func) {
        this.func = func;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getDevuuid() {
        return devuuid;
    }

    public void setDevuuid(String devuuid) {
        this.devuuid = devuuid;
    }

    public static class DataBean {
        /**
         * state : offline
         */

        private String state;

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }
    }
}
