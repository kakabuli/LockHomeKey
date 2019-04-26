package com.kaadas.lock.publiclibrary.mqtt.publishresultbean;

import com.kaadas.lock.bean.HomeShowBean;
import com.kaadas.lock.publiclibrary.bean.BleLockInfo;
import com.kaadas.lock.publiclibrary.bean.CateEyeInfo;
import com.kaadas.lock.publiclibrary.bean.GatewayInfo;
import com.kaadas.lock.publiclibrary.bean.ServerGwDevice;
import com.kaadas.lock.publiclibrary.bean.ServerGatewayInfo;
import com.kaadas.lock.publiclibrary.http.result.ServerBleDevice;

import java.util.ArrayList;
import java.util.List;

public class AllBindDevices {


    /**
     * msgId : 123456
     * msgtype : response
     * userId :
     * gwId :
     * deviceId :
     * func : getAllBindDevice
     * returnCode : 200
     * timestamp : 1556172487814
     * data : {"gwList":[{"deviceSN":"GW01182510163","deviceNickName":"GW01182510163","adminuid":"5c4fe492dc93897aa7d8600b","adminName":"8618954359822","adminNickname":"8618954359822","isAdmin":1,"meUsername":"17c830b7267a4d208029c217fbb6b7c5","mePwd":"1456dfc75ba34d5191399bcc6473b85a","meBindState":1,"deviceList":[{"ipaddr":"192.168.168.235","macaddr":"0C:9A:42:B7:8C:F5","SW":"orangecat-1.3.4","event_str":"offline","device_type":"kdscateye","deviceId":"CH01183910242","time":"2019-04-02 09:13:57.481"}]}],"devList":[{"_id":"5c8f5563dc938989e2f5429d","lockName":"BT123456","lockNickName":"BT123456","macLock":"123456","open_purview":"3","is_admin":"1","center_latitude":"0","center_longitude":"0","circle_radius":"0","auto_lock":"0","password1":"123456","password2":"654321","model":""}]}
     */

    private int msgId;
    private String msgtype;
    private String userId;
    private String gwId;
    private String deviceId;
    private String func;
    private String returnCode;
    private String timestamp;
    private ReturnDataBean data;

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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGwId() {
        return gwId;
    }

    public void setGwId(String gwId) {
        this.gwId = gwId;
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

    public String getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public ReturnDataBean getData() {
        return data;
    }

    public void setData(ReturnDataBean data) {
        this.data = data;
    }

    public static class ReturnDataBean {
        private List<GwListBean> gwList;
        private List<ServerBleDevice> devList;

        public List<GwListBean> getGwList() {
            return gwList;
        }

        public void setGwList(List<GwListBean> gwList) {
            this.gwList = gwList;
        }

        public List<ServerBleDevice> getDevList() {
            return devList;
        }

        public void setDevList(List<ServerBleDevice> devList) {
            this.devList = devList;
        }


        public static class GwListBean {
            /**
             * deviceSN : GW01182510163
             * deviceNickName : GW01182510163
             * adminuid : 5c4fe492dc93897aa7d8600b
             * adminName : 8618954359822
             * adminNickname : 8618954359822
             * isAdmin : 1
             * meUsername : 17c830b7267a4d208029c217fbb6b7c5
             * mePwd : 1456dfc75ba34d5191399bcc6473b85a
             * meBindState : 1
             * deviceList : [{"ipaddr":"192.168.168.235","macaddr":"0C:9A:42:B7:8C:F5","SW":"orangecat-1.3.4","event_str":"offline","device_type":"kdscateye","deviceId":"CH01183910242","time":"2019-04-02 09:13:57.481"}]
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

            private List<ServerGwDevice> deviceList;

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

            public List<ServerGwDevice> getDeviceList() {
                return deviceList;
            }

            public void setDeviceList(List<ServerGwDevice> deviceList) {
                this.deviceList = deviceList;
            }

        }
    }


    /**
     * 获取所有设备
     */
    public void getAllDevices() {


    }


    /**
     * 获取首页显示需要的对象，即除了网管之外的所有设备
     */
    public  List<HomeShowBean> getHomeShow(boolean showGateway) {
        ReturnDataBean returnData = getData();
        List<HomeShowBean> homeShowBeans = new ArrayList<>();
        List<ServerBleDevice> bleDevices = returnData.getDevList();
        for (ServerBleDevice bleDevice : bleDevices) {
            BleLockInfo bleLockInfo = new BleLockInfo(bleDevice);
            homeShowBeans.add(new HomeShowBean(HomeShowBean.TYPE_BLE_LOCK, bleDevice.getDevice_name(), bleDevice.getDevice_nickname(), bleLockInfo));
        }

        List<ReturnDataBean.GwListBean> gwList = returnData.getGwList();
        for (ReturnDataBean.GwListBean gwListBean : gwList) {
            //首页不显示网关
            if (showGateway==true){
                homeShowBeans.add(new HomeShowBean(HomeShowBean.TYPE_GATEWAY, gwListBean.getDeviceSN(), gwListBean.getDeviceNickName(), new GatewayInfo(new ServerGatewayInfo(gwListBean))));
            }
            List<ServerGwDevice> deviceList = gwListBean.getDeviceList();
            for (ServerGwDevice serverGwDevice:deviceList){
                if ("kdscateye".equalsIgnoreCase(serverGwDevice.getDevice_type())){
                    homeShowBeans.add(new HomeShowBean(HomeShowBean.TYPE_CAT_EYE, serverGwDevice.getDeviceId(),
                            serverGwDevice.getNickName(), new CateEyeInfo(gwListBean.getDeviceSN(), serverGwDevice)));
                }else {
                    homeShowBeans.add(new HomeShowBean(HomeShowBean.TYPE_GATEWAY_LOCK, serverGwDevice.getDeviceId(),
                            serverGwDevice.getNickName(), new CateEyeInfo(gwListBean.getDeviceSN(), serverGwDevice)));
                }

            }
        }
        return homeShowBeans;
    }

    /**
     * 获取蓝牙设备列表
     */
    public List<ServerBleDevice> getBleDevices() {
        return data.getDevList();
    }

    /**
     * 获取猫眼列表
     */
    public List<ServerGwDevice> getCateEyes() {
        List<ReturnDataBean.GwListBean> gwList = data.getGwList();
        List<ServerGwDevice> gwDevices = new ArrayList<>();
        for (ReturnDataBean.GwListBean gwListBean : gwList) {
            List<ServerGwDevice> deviceList = gwListBean.getDeviceList();
            for (ServerGwDevice gwDevice : deviceList) {
                if ("kdscateye".equalsIgnoreCase(gwDevice.getDevice_type())) {
                    gwDevices.add(gwDevice);
                }
            }
        }
        return gwDevices;
    }

    /**
     * 获取网关锁列表
     */
    public List<ServerGwDevice> getGwLocks() {
        List<ReturnDataBean.GwListBean> gwList = data.getGwList();
        List<ServerGwDevice> gwDevices = new ArrayList<>();
        for (ReturnDataBean.GwListBean gwListBean : gwList) {
            List<ServerGwDevice> deviceList = gwListBean.getDeviceList();
            for (ServerGwDevice gwDevice : deviceList) {
                if (!"kdscateye".equalsIgnoreCase(gwDevice.getDevice_type())) {  //不是猫眼的设备
                    gwDevices.add(gwDevice);
                }
            }
        }

        return gwDevices;
    }

    /**
     * 获取网关列表
     *
     * @return
     */
    public List<ReturnDataBean.GwListBean> getGateways() {
        List<ReturnDataBean.GwListBean> gwList = data.getGwList();
        List<ServerGatewayInfo> gatewayInfos = new ArrayList<>();
        for (ReturnDataBean.GwListBean gwListBean : gwList) {
            gatewayInfos.add(new ServerGatewayInfo());
        }
        return gwList;
    }


}
