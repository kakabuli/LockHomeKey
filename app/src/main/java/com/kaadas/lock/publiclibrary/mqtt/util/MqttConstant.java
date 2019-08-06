package com.kaadas.lock.publiclibrary.mqtt.util;

/**
 * 常量
 *
 * @author FJH

 * created at 2019/3/27 17:09
 */
public class MqttConstant {

    //米米网参数
    public final static String APP_ID = "AIB1EITFX0DB75MCUIZR";
    public final static String PARTERN_ID = "HQQ8H3HJGJ2KPQJ7NXZY";
    public final static int  DC_TEST = 6750465;


     public static final String MQTT_BASE_URL = "tcp://mqtt-kaadas.juziwulian.com:1883";//正式服务器
//          public static final String MQTT_BASE_URL = "tcp://test.juziwulian.com:1883";//测试服务器


      public static final String LINPHONE_URL = "sip-kaadas.juziwulian.com:5061";//正式sip
//             public static final String LINPHONE_URL = "test.juziwulian.com:5061";//测试sip

    public static final String  MQTT_REQUEST_APP = "/request/app/func";


    //断开后，是否自动连接
    public static final boolean MQTT_AUTOMATIC_RECONNECT = true;

    //是否清空客户端的连接记录。若为true，则断开后，broker将自动清除该客户端连接信息
    public static final boolean MQTT_CLEANSE_SSION = false;

    //设置超时时间，单位为秒
    public static final int MQTT_CONNECTION_TIMEOUT = 10;

    //设置心跳时间，单位为秒
    public static final int MQTT_KEEP_ALIVE_INTERVAL = 20;

    //允许同时发送几条消息（未收到broker确认信息）
    public static final int MQTT_MAX_INFLIGHT = 10;

    //msgtype---request
    public static final String MSG_TYPE_REQUEST = "request";

    //online
    public static final String ON_LINE = "online";

    //获取所有绑定的设备接口
    public static final String GET_ALL_BIND_DEVICE = "getAllBindDevice";


    public static String getSubscribeTopic(String userId) {
        String topic = "/" + userId + "/rpc/reply";
        return topic;
    }


    public static String  getCallTopic(String userId) {
        return "/" + userId + "/rpc/call";
    }

    //发布给服务器消息的主题，格式为/request/app/func
    public static final String PUBLISH_TO_SERVER = "/request/app/func";
    //发布给服务器中转网关消息的主题，/clientid/rpc/call
    public static String PUBLISH_TO_GATEWAY = "/rpc/call";

    //1 绑定网关
    public static final String BIND_GATEWAY = "bindGatewayByUser";
    // 绑定咪咪网
    public static final String REGISTER_MIMI_BIND="RegisterMemeAndBind";

    //2 获取网关列表
    public static final String GET_BIND_GATEWAY_LIST = "gatewayBindList";

    //3获取网关状态
    public static final String GATEWAY_STATE = "gatewayState";

    //获取WiFi信息
    public static final String GET_WIFI_BASIC = "getWiFiBasic";

    //获取WiFi信息
    public static final String ALLOW_GATEWAY_JOIN = "allowCateyeJoin";

    //设备上下线的 func
    public static final String GW_EVENT = "gwevent";

    //网关开启允许设备入网模式
    public static final String SET_JOIN_ALLOW = "setJoinAllow";

    //修改设备的昵称
    public static final String UPDATE_DEV_NICK_NAME = "updateDevNickName";

    //获取设备电量
    public static final String GET_POWER="getPower";

    //开锁
    public static final String OPEN_LOCK="openLock";

    //锁密码的操作
    public static final String SET_PWD="setPwd";

    //获取锁密码和RFID基本信息
    public static final String LOCK_PWD_INFO="lockPwdInfo";

    //获取锁的语言
    public static final String GET_LANG="getLang";

    //设置锁的语言
    public static final String SET_LANG="setLang";

    //获取锁的音量
    public static final String SOUND_VOLUME="soundVolume";

    //设置锁的音量
    public static final String SET_SOUND_VOLUME="setSoundVolume";

 //获取设备信息
 public static final String GET_LOCK_INFO="BasicInfo";

 //获取设备信息
 public static final String WAKEUP_CAMERA="wakeupCamera";

    //删除锁
    public static final String DELETE_GATEWAY_LOCK="delDevice";

    //唤醒FTP
    public static final String SET_FTP_ENABLE="setFtpEnable";
    //网关推送通知
    public static final  String GATEWAY_EVENT_NOTIFY = "gwevent";
    //推送消息
    public static final String EVENT = "event";

    //获取猫眼信息
    public static final String BASIC_INFO="basicInfo";

    //设置Pir
    public static final String SET_PIR_ENABLE="setPirEnable";

    //设置响铃次数setBellCount
    public static final String SET_BELL_COUNT="setBellCount";

    //设置猫眼的分辨率setVedioRes
    public static final String SET_VEDIO_RES="setVedioRes";

    //获取开锁记录
    public static final String GET_OPEN_LOCK_RECORD="selectOpenLockRecord";

    //设置猫眼的音量
    public static final String SET_BELL_VOLUME="setBellVolume";

    //设置pir徘徊监测
    public static final String SET_PIR_WANDER="setPirWander";

    //获取静默参数
    public static final String GET_PIR_SLIENT="getPirSilent";

    //设置静默参数
    public static final String SET_PIR_SLIENT="setPirSilent";

    //解绑网关
    public static final String UNBIND_GATEWAY="unbindGateway";

    //解绑测试网关
    public static final String UNBIND_TEST_GATEWAY="testUnBindGateway";

    //设置布防
    public static final String SET_ARM_LOCKED="setArmLocked";

    //设置AM
    public static final String SET_AM="setAM";

    //获取布防
    public static final String GET_ALRAM_LOCK="getArmLocked";

    //获取AM
    public static final String GET_AM="getAM";

    //获取网络设置基本信息
    public static final String GET_NET_BASIC="getNetBasic";

    //网关协调器信道获取
    public static final String GET_ZB_Channel="getZbChannel";

    //设置wifi
    public static final String SET_WIFI_BASIC="setWiFiBasic";

    //网络设置
    public static final String SET_NET_BASIC="setNetBasic";

    //网关协调器信道设置
    public static final String SET_ZB_CHANNEL="setZbChannel";

    //获取守护次数
    public static final String COUNT_OPEN_LOCK_RECORD="countOpenLockRecord";

    //获取网关ota升级通知
    public static final String NOTIFY_GATEWAY_OTA="otaApprovate";

    //网关ota升级确认
    public static final String CONFIRM_GATEWAY_OTA="otaApprovateResult";

    //分享设备
    public static final String SHARE_DEVICE="shareDevice";

    //分享的用户
    public static final String SHARE_USER_LIST="shareUserList";

    //修改网关昵称
    public static final String UPDATE_GATEWAY_NICK_NAME="updateGwNickName";

    //网关重置上报
    public static final String GATEWAY_RESET="gatewayReset";



}
