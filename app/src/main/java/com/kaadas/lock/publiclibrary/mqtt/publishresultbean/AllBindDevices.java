package com.kaadas.lock.publiclibrary.mqtt.publishresultbean;

import android.text.TextUtils;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.bean.HomeShowBean;
import com.kaadas.lock.publiclibrary.bean.BleLockInfo;
import com.kaadas.lock.publiclibrary.bean.CateEyeInfo;
import com.kaadas.lock.publiclibrary.bean.GatewayInfo;
import com.kaadas.lock.publiclibrary.bean.GwLockInfo;
import com.kaadas.lock.publiclibrary.bean.ServerGwDevice;
import com.kaadas.lock.publiclibrary.bean.ServerGatewayInfo;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.publiclibrary.bean.ProductInfo;
import com.kaadas.lock.publiclibrary.http.result.ServerBleDevice;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.SPUtils;

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
     * code : 200
     * timestamp : 1556172487814
     * data : {"gwList":[{"deviceSN":"GW01182510163","deviceNickName":"GW01182510163","adminuid":"5c4fe492dc93897aa7d8600b",
     * "adminName":"8618954359822","adminNickname":"8618954359822","isAdmin":1,"meUsername":"17c830b7267a4d208029c217fbb6b7c5",
     * "mePwd":"1456dfc75ba34d5191399bcc6473b85a","meBindState":1,"deviceList":[{"ipaddr":"192.168.168.235",
     * "macaddr":"0C:9A:42:B7:8C:F5","SW":"orangecat-1.3.4","event_str":"offline","device_type":"kdscateye","deviceId":"CH01183910242",
     * "time":"2019-04-02 09:13:57.481"}]}],"devList":[{"_id":"5c8f5563dc938989e2f5429d","lockName":"BT123456","lockNickName":"BT123456",
     * "macLock":"123456","open_purview":"3","is_admin":"1","center_latitude":"0","center_longitude":"0","circle_radius":"0","auto_lock":"0",
     * "password1":"123456","password2":"654321","model":""}]}
     */

    private int msgId;
    private String msgtype;
    private String userId;
    private String gwId;
    private String deviceId;
    private String func;
    private String code;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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
        private List<WifiLockInfo> wifiList;
        private List<ProductInfo> productInfoList;

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

        public List<WifiLockInfo> getWifiList() {
            return wifiList;
        }

        public void setWifiList(List<WifiLockInfo> wifiList) {
            this.wifiList = wifiList;
        }

        public List<ProductInfo> getProductInfoList() {
            return productInfoList;
        }

        public void setProductInfoList(List<ProductInfo> productInfoList) {
            this.productInfoList = productInfoList;
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
            private String model;   // 6030 小网关、 6010大网关
            private int relayType;


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

            public GwListBean() {
            }

            public int getRelayType() {
                return relayType;
            }

            public void setRelayType(int relayType) {
                this.relayType = relayType;
            }

            public GwListBean(String deviceSN, String deviceNickName, String adminuid, String adminName, String adminNickname, int isAdmin, String meUsername, String mePwd, int meBindState, String model, int relayType, List<ServerGwDevice> deviceList) {
                this.deviceSN = deviceSN;
                this.deviceNickName = deviceNickName;
                this.adminuid = adminuid;
                this.adminName = adminName;
                this.adminNickname = adminNickname;
                this.isAdmin = isAdmin;
                this.meUsername = meUsername;
                this.mePwd = mePwd;
                this.meBindState = meBindState;
                this.model = model;
                this.relayType = relayType;
                this.deviceList = deviceList;
            }

            public GwListBean(String deviceSN, String deviceNickName, String adminuid, String adminName, String adminNickname, int isAdmin, String meUsername, String mePwd, int meBindState, String model, List<ServerGwDevice> deviceList) {
                this.deviceSN = deviceSN;
                this.deviceNickName = deviceNickName;
                this.adminuid = adminuid;
                this.adminName = adminName;
                this.adminNickname = adminNickname;
                this.isAdmin = isAdmin;
                this.meUsername = meUsername;
                this.mePwd = mePwd;
                this.meBindState = meBindState;
                this.model = model;
                this.deviceList = deviceList;
            }

            public String getModel() {
                return model;
            }

            public void setModel(String model) {
                this.model = model;
            }
        }
    }


    /**
     * 获取首页显示需要的对象，即除了网管之外的所有设备
     */
    public List<HomeShowBean> getHomeShow() {
        ReturnDataBean returnData = getData();
        List<HomeShowBean> homeShowBeans = new ArrayList<>();
        if (returnData == null) {
            return homeShowBeans;
        }
        List<ServerBleDevice> bleDevices = returnData.getDevList();
        if (bleDevices != null) {
            for (ServerBleDevice bleDevice : bleDevices) {
                boolean isExist = false;
                for (HomeShowBean homeShowBean : MyApplication.getInstance().getHomeShowDevices()) {
                    if (!isExist && homeShowBean.getDeviceType() == HomeShowBean.TYPE_BLE_LOCK) {
                        //如果设备原来就存在，那么只替换服务器数据   其他数据不变
                        BleLockInfo bleLockInfo = (BleLockInfo) homeShowBean.getObject();
                        if (bleDevice.getMacLock().equals(bleLockInfo.getServerLockInfo().getMacLock())) { //是否是同一个设备
                            isExist = true;
                            bleLockInfo.setServerLockInfo(bleDevice);
                            homeShowBean.setDeviceNickName(bleDevice.getLockNickName());
                            homeShowBeans.add(homeShowBean);
                        }
                        LogUtils.e("设备已存在  是否连接 " + bleLockInfo.isConnected());
                    }
                }
                if (!isExist) {
                    BleLockInfo bleLockInfo = new BleLockInfo(bleDevice);
                    homeShowBeans.add(new HomeShowBean(HomeShowBean.TYPE_BLE_LOCK, bleDevice.getLockName(), bleDevice.getLockNickName(), bleLockInfo));
                }
            }
        }

        //WiFi锁
        List<WifiLockInfo> wifiList = returnData.getWifiList();
        if (wifiList != null) {
            for (WifiLockInfo wifiLockInfo : wifiList) {
                homeShowBeans.add(new HomeShowBean(HomeShowBean.TYPE_WIFI_LOCK, wifiLockInfo.getWifiSN(), wifiLockInfo.getLockNickname(), wifiLockInfo));
            }
        }

        List<ReturnDataBean.GwListBean> gwList = returnData.getGwList();
        if (gwList != null) {
            for (ReturnDataBean.GwListBean gwListBean : gwList) {
                //首页不显示网关
                boolean isExistGateway = false;
                GatewayInfo newGatewayInfo = null;
                for (HomeShowBean homeShowBean : MyApplication.getInstance().getAllDevices()) {
                    if (!isExistGateway && homeShowBean.getDeviceType() == HomeShowBean.TYPE_GATEWAY) {
                        //如果设备原来就存在，那么只替换服务器数据   其他数据不变
                        GatewayInfo gatewayInfo = (GatewayInfo) homeShowBean.getObject();
                        LogUtils.e("gatewayInfo网关状态  " + gatewayInfo.getEvent_str());
                        LogUtils.e("网关id " + gatewayInfo.getServerInfo().getDeviceSN());
                        if (gwListBean.getDeviceSN().equals(gatewayInfo.getServerInfo().getDeviceSN())) {
                            isExistGateway = true;
                            gatewayInfo.setServerInfo(new ServerGatewayInfo(gwListBean));
                            homeShowBean.setDeviceNickName(gwListBean.getDeviceNickName());
                            homeShowBeans.add(homeShowBean);
                        }

                    }
                }
                if (!isExistGateway) {
                    newGatewayInfo = new GatewayInfo(new ServerGatewayInfo(gwListBean));
                    LogUtils.e("网关状态SN" + newGatewayInfo.getServerInfo().getDeviceSN());
                    String gatewayStatus = (String) SPUtils.get(newGatewayInfo.getServerInfo().getDeviceSN(), "");
                    newGatewayInfo.setEvent_str(gatewayStatus);
                    LogUtils.e("newGatewayInfo网关状态   " + newGatewayInfo.getEvent_str() + "网关状态" + gatewayStatus);
                    SPUtils.remove(newGatewayInfo.getServerInfo().getDeviceSN());
                    homeShowBeans.add(new HomeShowBean(HomeShowBean.TYPE_GATEWAY, gwListBean.getDeviceSN(), gwListBean.getDeviceNickName(), newGatewayInfo));
                }
                List<ServerGwDevice> deviceList = gwListBean.getDeviceList();
                for (ServerGwDevice serverGwDevice : deviceList) {
                    String nickName = serverGwDevice.getNickName();
                    if (TextUtils.isEmpty(nickName)) {
                        nickName = serverGwDevice.getDeviceId();
                    }
                    boolean isExist = false;
                    if ("kdscateye".equalsIgnoreCase(serverGwDevice.getDevice_type())) {
                        for (HomeShowBean homeShowBean : MyApplication.getInstance().getHomeShowDevices()) {
                            if (!isExist && homeShowBean.getDeviceType() == HomeShowBean.TYPE_CAT_EYE) {
                                CateEyeInfo cateEyeInfo = (CateEyeInfo) homeShowBean.getObject();
                                //如果设备 网关Id 一致   且deviceId也一致   替换服务器数据
                                if (cateEyeInfo.getGwID().equals(gwListBean.getDeviceSN())
                                        && cateEyeInfo.getServerInfo().getDeviceId().equals(serverGwDevice.getDeviceId())
                                        ) {
                                    isExist = true;
                                    cateEyeInfo.setServerInfo(serverGwDevice);
                                    homeShowBean.setDeviceNickName(nickName);
                                    homeShowBeans.add(homeShowBean);
                                }
                            }
                        }
                        if (!isExist) {
                            CateEyeInfo cateEyeInfo = new CateEyeInfo(gwListBean.getDeviceSN(), serverGwDevice);
                            cateEyeInfo.setGatewayInfo(newGatewayInfo);
                            homeShowBeans.add(new HomeShowBean(HomeShowBean.TYPE_CAT_EYE, serverGwDevice.getDeviceId(), nickName, cateEyeInfo));
                            LogUtils.e("猫眼不存在   昵称是" + cateEyeInfo.getServerInfo().getNickName());
                        }

                    } else {
                        for (HomeShowBean homeShowBean : MyApplication.getInstance().getHomeShowDevices()) {
                            if (!isExist && homeShowBean.getDeviceType() == HomeShowBean.TYPE_GATEWAY_LOCK) {
                                GwLockInfo gwLockInfo = (GwLockInfo) homeShowBean.getObject();
                                //如果设备 网关Id 一致   且deviceId也一致   替换服务器数据
                                LogUtils.e(gwLockInfo.getServerInfo().getNickName() + "值还没有改变");
                                if (gwLockInfo.getGwID().equals(gwListBean.getDeviceSN())
                                        && gwLockInfo.getServerInfo().getDeviceId().equals(serverGwDevice.getDeviceId())
                                        ) {
                                    isExist = true;
                                    gwLockInfo.setServerInfo(serverGwDevice);
                                    LogUtils.e(gwLockInfo.getServerInfo().getNickName() + "值发生改变");
                                    homeShowBean.setDeviceNickName(nickName);
                                    homeShowBeans.add(homeShowBean);
                                }
                            }
                        }
                        if (!isExist) {
                            homeShowBeans.add(new HomeShowBean(HomeShowBean.TYPE_GATEWAY_LOCK, serverGwDevice.getDeviceId(),
                                    nickName, new GwLockInfo(gwListBean.getDeviceSN(), serverGwDevice)));
                        }

                    }
                }
            }
        }
        return homeShowBeans;
    }


    /**
     * 获取猫眼列表
     */
    public List<CateEyeInfo> getCateEyes() {
        List<ReturnDataBean.GwListBean> gwList = data.getGwList();
        List<CateEyeInfo> cateEyeInfos = new ArrayList<>();
        for (ReturnDataBean.GwListBean gwListBean : gwList) {
            List<ServerGwDevice> deviceList = gwListBean.getDeviceList();
            GatewayInfo gatewayInfo = new GatewayInfo(new ServerGatewayInfo(gwListBean));
            for (ServerGwDevice gwDevice : deviceList) {
                if ("kdscateye".equalsIgnoreCase(gwDevice.getDevice_type())) {
                    CateEyeInfo cateEyeInfo = new CateEyeInfo(gwListBean.getDeviceSN(), gwDevice);
                    cateEyeInfo.setGatewayInfo(gatewayInfo);
                    cateEyeInfos.add(cateEyeInfo);
                }
            }
        }
        return cateEyeInfos;
    }

    /**
     * 获取网关锁列表
     */
    public List<GwLockInfo> getGwLocks() {
        List<ReturnDataBean.GwListBean> gwList = data.getGwList();
        List<GwLockInfo> gwLockInfos = new ArrayList<>();
        for (ReturnDataBean.GwListBean gwListBean : gwList) {
            List<ServerGwDevice> deviceList = gwListBean.getDeviceList();
            for (ServerGwDevice gwDevice : deviceList) {
                if ("kdscateye".equalsIgnoreCase(gwDevice.getDevice_type())) {
                    gwLockInfos.add(new GwLockInfo(gwListBean.getDeviceSN(), gwDevice));
                }
            }
        }
        return gwLockInfos;
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
