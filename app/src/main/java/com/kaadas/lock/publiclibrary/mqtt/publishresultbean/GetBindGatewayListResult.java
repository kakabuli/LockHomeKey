package com.kaadas.lock.publiclibrary.mqtt.publishresultbean;

import java.util.List;

public class GetBindGatewayListResult extends BaseBeanResult {

    /**
     * code : 200
     * msg : 成功
     * func : gatewayBindList
     * data : [{"deviceSN":"GW01182510033","deviceNickName":"GW01182510033","adminuid":"5c3d370f35736f6e8f9ba676","adminName":"8617512018193","adminNickname":"8617512018193","isAdmin":1,"meUsername":"cf85c146123741c58109ebb3ee786c59","mePwd":"369f70f1b67c481e964004e7ade40f34","meBindState":1}]
     */

    private String code;
    private String msg;
    private String func;
    private List<DataBean> data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getFunc() {
        return func;
    }

    public void setFunc(String func) {
        this.func = func;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * deviceSN : GW01182510033
         * deviceNickName : GW01182510033
         * adminuid : 5c3d370f35736f6e8f9ba676
         * adminName : 8617512018193
         * adminNickname : 8617512018193
         * isAdmin : 1
         * meUsername : cf85c146123741c58109ebb3ee786c59
         * mePwd : 369f70f1b67c481e964004e7ade40f34
         * meBindState : 1
         */

        private String deviceSN;
        private String deviceNickName;
        private String adminuid;
        private String adminName;
        private String adminNickname;
        private int isAdmin;
        private String meUsername;
        private String mePwd;
        private int meBindState;

        public String getDeviceSN() {
            return deviceSN;
        }

        public void setDeviceSN(String deviceSN) {
            this.deviceSN = deviceSN;
        }

        public String getDeviceNickName() {
            return deviceNickName;
        }

        public void setDeviceNickName(String deviceNickName) {
            this.deviceNickName = deviceNickName;
        }

        public String getAdminuid() {
            return adminuid;
        }

        public void setAdminuid(String adminuid) {
            this.adminuid = adminuid;
        }

        public String getAdminName() {
            return adminName;
        }

        public void setAdminName(String adminName) {
            this.adminName = adminName;
        }

        public String getAdminNickname() {
            return adminNickname;
        }

        public void setAdminNickname(String adminNickname) {
            this.adminNickname = adminNickname;
        }

        public int getIsAdmin() {
            return isAdmin;
        }

        public void setIsAdmin(int isAdmin) {
            this.isAdmin = isAdmin;
        }

        public String getMeUsername() {
            return meUsername;
        }

        public void setMeUsername(String meUsername) {
            this.meUsername = meUsername;
        }

        public String getMePwd() {
            return mePwd;
        }

        public void setMePwd(String mePwd) {
            this.mePwd = mePwd;
        }

        public int getMeBindState() {
            return meBindState;
        }

        public void setMeBindState(int meBindState) {
            this.meBindState = meBindState;
        }
    }
}
