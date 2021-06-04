package com.kaadas.lock.utils;

import com.kaadas.lock.BuildConfig;
import com.kaadas.lock.bean.BluetoothLockBroadcastBean;

/**
 * Create By lxj  on 2019/2/14
 * Describe
 * 各种常量Key的静态类
 * 每个Key都需要注释有什么作用
 */
public class KeyConstants {

    public static final String VERSION = "version: " + BuildConfig.HTTP_VERSION;

    /**
     * Intent 传递值的常量Key
     */

    /**
     * 在login界面，是否显示提示对话框key
     */
    public static final String IS_SHOW_DIALOG = "isShowDialog";

    /**
     * 设备类型
     */
    public static final String DEVICE_TYPE = "deviceType";

    /**
     * 密码1
     */
    public static final String PASSWORD1 = "password1";

    /**
     * 密码1
     */
    public static final String BLE_DEVICE_SN = "deviceSN";
    /**
     * 密码1
     */
    public static final String BLE_MAC = "bleMac";
    /**
     * 密码1
     */
    public static final String BLE_DEVICE_NAME = "deviceName";

    /**
     * 是否绑定
     */
    public static final String IS_BIND = "isBind";


    /**
     * 绑定成功的时传递的设备名
     */
    public static final String DEVICE_NAME = "deviceName";
    /**
     * 设备信息
     */
    public static final String BLE_DEVICE_INFO = "deviceInfo";
    /**
     * 临时密码
     */
    public static final String TEMP_PASSWORD = "tempPassword";
    /**
     * 临时密码的昵称
     */
    public static final String TEMP_PASSWORD_NICK = "tempPasswordNick";
    /**
     * 指纹的用户编号
     */
    public static final String USER_NUM = "userNum";

    /**
     * 密码和昵称
     */
    public static final String PASSWORD_NICK = "passwordNick";

    /**
     * 密码和昵称
     */
    public static final String IS_DELETE = "isDelete";

    /**
     * 设置密码成功，生成的ForeverPassword
     */
    public static final String TO_PWD_DETAIL = "toPwdDetail";

    /**
     * 密码类型   	密钥类型：1密码 2指纹密码 3卡片密码 4面容识别
     */
    public static final String PASSWORD_TYPE = "passwordType";

    /**
     * 密码创建时间
     */
    public static final String CREATE_TIME = "createTime";


    public static final String TO_DETAIL_NUMBER = "toDetailNumber";
    //1永久密码 2临时密码 3指纹密码 4卡片密码
    public static final String TO_DETAIL_TYPE = "toDetailType";
    public static final String TO_DETAIL_PASSWORD = "toDetailPassword";
    public static final String TO_DETAIL_NICKNAME = "toDetailNickName";

    /**
     * SharedPreferences中的Key常量
     */


    /**
     * 保存密码  header + deviceMac
     * 保存密码  头+设备Mac地址
     */
    public static final String SAVE_PWD_HEARD = "savePwd";

    /**
     * 图片路径
     */
    public static final String HEAD_PATH = "headPath";
    /**
     * 跳转临时密码详情key区分
     */
    public static final String JUMP_TEMPORARY_PASSWORD_DETAIL_KEY = "jump_temporary_password_detail_key";
    /**
     * 临时密码详情跳转来源 临时密码管理页面跳转
     */
    public static final String TEMPORARY_PASSWORD_MANAGER = "temporary_password_manager";
    /**
     * 临时密码详情跳转来源 添加临时密码页面跳转
     */
    public static final String ADD_TEMPORARY_PASSWORD = "add_temporary_password";
    /**
     * 周重复数据
     */
    public static final String WEEK_REPEAT_DATA = "week_repeat_data";

    /**
     * 周期的数组，  周日 1 2 3 4 5 6
     */
    public static final String DAY_MASK = "dayMask";
    /**
     * 手机号
     */
    public static final String AUTHORIZATION_TELEPHONE = "authorization_telephone";
    /**
     * 普通家庭成员数据
     */
    public static final String COMMON_FAMILY_MEMBER_DATA = "common_family_member_data";

    public static final String GATEWAY_SHARE_USER = "gateway_share_user";
    /**
     * 时间策略key值
     */
    public static final String TIME_CE_LUE = "time_ce_lue";
    /**
     * 时间策略
     */
    public static final int YONG_JIU = 1;
    public static final int ONE_DAY = 2;
    public static final int CUSTOM = 3;
    public static final int PERIOD = 4;//周期
    public static final int TEMP = 5;//临时

    /**
     * 自定义开始时间
     */
    public static final String CUSTOM_START_TIME = "custom_start_time";
    /**
     * 自定义结束时间
     */
    public static final String CUSTOM_END_TIME = "custom_end_time";
    /**
     * 周期开始时间
     */
    public static final String PERIOD_START_TIME = "period_START_time";
    /**
     * 周期结束时间
     */
    public static final String PERIOD_END_TIME = "period_end_time";
    /**
     * 选择国家请求码
     */
    public static final int SELECT_COUNTRY_REQUEST_CODE = 12;


    //1.系统消息
    //2.共享设备授权信息
    //3.网关授权信息
    public static final int SYSTEM_MESSAGE = 1;
    public static final int SHARE_DEVICE_AUTHORIZATION_MESSAGE = 2;
    public static final int GATEWAY_AUTHORIZATION_MESSAGE = 3;
    /**
     * 消息详情标题
     */
    public static final String MESSAGE_DETAIL_TITLE = "message_detail_title";
    /**
     * 消息详情内容
     */
    public static final String MESSAGE_DETAIL_CONTENT = "message_detail_content";
    /**
     * 消息详情时间
     */
    public static final String MESSAGE_DETAIL_TIME = "message_detail_TIME";
    /**
     * 引导页
     */
    public static final String SHOW_GUIDE_PAGE = "show_guide_page";
    //app通知
    public static final String APP_NOTIFICATION_STATUS = "app_notification_status";
    //智能监测
    public static final String SMART_MONITOR_STATUS = "smart_monitor_status";
    //消息免打扰状态
    public static final String MESSAGE_FREE_STATUS = "message_free_status";

    //A-M  自动上锁
    public static final String AM_AUTO_LOCK_STATUS = "am_auto_lock_status";

    //静音模式
    public static final String SILENT_MODE_STATUS = "silent_mode_status";

    //安全模式
    public static final String SAFE_MODE_STATUS = "safe_mode_status";

    //手势密码
    public static final String HAND_PASSWORD = "hand_password";


    //Touch ID
    public static final String TOUCH_ID = "touch_id";
    //蓝牙动态 普通
    public static final String BLUETOOTH_RECORD_COMMON = "bluetooth_record_common";
    //蓝牙动态 警告
    public static final String BLUETOOTH_RECORD_WARN = "bluetooth_record_warn";

    //网关wifi的名称
    public static final String GW_WIFI_SSID = "gwWifiSsid";
    //网关WiFi的密码
    public static final String GW_WIFI_PWD = "gwWifiPWd";
    //扫描的设备SN
    public static final String DEVICE_SN = "deviceSn";
    //扫描的设备mac
    public static final String DEVICE_MAC = "deviceMac";
    //扫描的设备mac
    public static final String GW_SN = "gwSn";

    //网关的id
    public static final String GATEWAY_ID = "gatewayId";
    //网关的昵称
    public static final String GATEWAY_NICKNAME = "gatewayNickName";
    public static final String IS_GATEAWAY = "is_gateway";

    // 小的6030和6032
    public static final String SMALL_GW = "6030";
    public static final String SMALL_GW2 = "6032";
    public static final String SMALL_GW_ID= "SMALL_GW_ID";

    // 大网关6010
    public static final String BIG_GW = "6010";
    //是否是管理员
    public static final String IS_ADMIN = "isadmin";
    //是否是管理员
    public static final String GW_MODEL = "gw_model";

    //设备的id
    public static final String DEVICE_ID = "deviceId";
    //猫眼
    public static final String CATEYE = "cateye";
    //网关锁
    public static final String GATEWAY_LOCK = "gateway_lock";

    //设备的id
    public static final String DEVICE_NICKNAME = "DEVICE_NICKNAME";

    //开锁
    //已反锁
    //正在开锁
    //开锁成功
    //开锁失败
    //设备离线
    public static final int OPEN_LOCK = 0;
    public static final int HAS_BEEN_LOCKED = 1;
    public static final int IS_LOCKING = 2;
    public static final int OPEN_LOCK_SUCCESS = 3;
    public static final int OPEN_LOCK_FAILED = 4;
    public static final int DEVICE_OFFLINE = 5;

    //设备
    public static final String DEVICE_DETAIL_BEAN = "deviceDetailBean";

    //是否保存了开锁密码
    public static final String SAVA_LOCK_PWD = "saveLockPwd";

    //开锁密码编号
    public static final String LOCK_PWD_NUMBER = "lockPwdNumber";

    //开锁密码集合
    public static final String LOCK_PWD_LIST = "lockPwdList";

    //开锁密码编号添加
    public static final String ADD_LOCK_PWD_NUMBER = "addLockPwdNumber";

    //设备名称
    public static final String NAME = "name";

    //设置onActivityResult
    public static final int DEVICE_DETAIL_BEAN_NUM = 1000;

    public static final int DELETE_PWD_REQUEST_CODE = 1001;

    public static final int ADD_PWD_REQUEST_CODE = 1002;

    public static final int UPDATE_DEVICE_NAME_REQUEST_CODE = 1003;

    //铃声
    public static final int RING_NUMBER_REQUESET_CODE = 1004;

    //音量
    public static final int VOLUME_REQUESET_CODE = 1005;

    //分辨率
    public static final int RESOLUTION_REQUEST_CODE = 1006;

    //扫描猫眼跳转
    public static final int SCANCATEYE_REQUEST_CODE = 1007;

    //扫描网关的跳转
    public static final int SCANGATEWAY_REQUEST_CODE = 1008;

    //使用扫一扫页面跳转
    public static final int SCANGATEWAYNEW_REQUEST_CODE = 1009;

    //产品激活
    public static final int SCANPRODUCT_REQUEST_CODE = 1010;

    //网关昵称修改
    public static final int GATEWAY_NICK_NAME = 1011;

    //铃声次数
    public static final String RIGH_NUMBER = "ringNumber";

    //音量
    public static final String VOLUME_NUMBER = "volumeNumber";

    //分辨率
    public static final String RESOLUTION_NUMBER = "resolutionNumber";

    //传递蓝牙电量
    public static final int GET_BLE_POWER = 1004;

    //传递pir sen
    public static final int WIFI_VIDEO_LOCK_WANDERING_SENSITIVITY_CODE = 2011;

    //传递pir stay time
    public static final int WIFI_VIDEO_LOCK_WANDERING_STAY_TIME_CODE = 2012;

    //传递安全模式
    public static final int WIFI_VIDEO_LOCK_SAFE_MODE_CODE = 2013;

    //传递自动模式
    public static final int WIFI_VIDEO_LOCK_AM_MODE_CODE =2014;

    //传递语言
    public static final int WIFI_VIDEO_LOCK_LANGUAGE_CODE = 2015;

    //徘徊报警
    public static final int WIFI_VIDEO_LOCK_WANDERING_ALARM_CODE = 2016;

    //传递视频重复规则
    public static final int WIFI_VIDEO_LOCK_REAL_TIME_PERIOD_CODE = 2017;

    //实时视频设置
    public static final int WIFI_VIDEO_LOCK_REAL_TIME_SETTING_CODE = 2018;

    //X9开门方向
    public static final int WIFI_LOCK_SET_OPEN_DIRECTION = 3001;

    //X9开门力度
    public static final int WIFI_LOCK_SET_OPEN_FORCE = 3002;

    //X9上锁方式
    public static final int WIFI_LOCK_LOCKING_METHOD = 3003;

    //显示屏时间
    public static final int WIFI_VIDEO_LOCK_SCREEN_DURATION = 3010;

    //显示屏亮度
    public static final int WIFI_VIDEO_LOCK_SCREEN_BRIGHTNESS = 3011;

    //设置语音质量
    public static final int WIFI_VICEO_LOCK_VOICE_QUALITY = 3012;

    //设置AMS
    public static final int WIFI_VICEO_LOCK_AMS_SETTING = 3013;

    //设置胁迫密钥账号
    public static final int WIFI_LOCK_SETTING_DURESS_ACCOUNT = 3014;

    /**
     * 传递胁迫密码信息
     */
    public static final String DURESS_PASSWORD_INfO = "duressPasswordInfo";

    /**
     * 胁迫密码账号
     */
    public static final String DURESS_AUTHORIZATION_TELEPHONE = "duress_authorization_telephone";

    /**
     * 传递第i个胁迫密码信息
     */
    public static final String DURESS_PASSWORD_POSITION_INfO = "duressPasswordPositionInfo";

    /**
     * 传递蓝牙设备信息的Key
     */
    public static final String BLE_LOCK_INFO = "bleLockInfo";
    /**
     * 传递当前位置
     */
    public static final String FRAGMENT_POSITION = "fragmentPosition";
    /**
     * 传递猫眼数据
     */
    public static final String CATE_INFO = "cateInfo";

    /**
     * 传递网关数据
     */
    public static final String GATEWAY_INFO = "gatewayInfo";

    /**
     * 传递网关锁数据
     */
    public static final String GATEWAY_LOCK_INFO = "gatewayLockInfo";


    /**
     * 是否是呼叫进来
     */
    public static final String IS_CALL_IN = "isCallIn";

    /**
     * 是否是呼叫进来
     */
    public static final String IS_ACCEPT_CALL = "isAcceptCall";

    /**
     * 升级时，保存的设备蓝牙版本号的Key
     */
    public static final String BLE_VERSION = "bleVersion";

    //区分密码是永久有效还是，临时有效
    public static final String PWD_TYPE = "pwdType";
    //密码值
    public static final String PWD_VALUE = "pwdValue";
    //密码编号
    public static final String PWD_ID = "pwdId";

    //密码编号管理
    public static final String ADD_PWD_ID = "addPwdId";
    //添加胁迫密码编号
    public static final String ADD_STRESS_PWD_ID = "addStressPwdId";

    //获取猫眼信息
    public static final String GET_CAT_EYE_INFO = "getCatEyeInfo";

    //上报类型为猫眼
    public static final String DEV_TYPE_CAT_EYE = "kdscateye";

    /**
     * 上报类型为网关锁
     */
    public static final String DEV_TYPE_LOCK = "kdszblock";

    //蓝牙电量信息
    public static final String BLE_INTO = "bleInfo";

    //响铃次数
    public static final String CAT_EYE_RING_NUMBER = "catEyeRingNumber";

    //分辨率
    public static final String CAT_EYE_RESOLUTION = "catEyeResolution";

    //账号
    public static final String ACCOUNT = "account";

    //密码
    public static final String PASSWORD = "password";
    //地区码
    public static final String AREA_CODE = "area_code";
    //国家
    public static final String COUNTRY = "country";

    //铃声音量
    public static final String CAT_EYE_VOLUME = "catEyeVolume";
    /**
     * 锁型号
     */
    public static final String LOCK_MODEL = "lock_model";

    //是否绑定了咪咪网
    public static final String IS_BIND_MEME = "isbindMeme";


    //获取猫眼信息
    public static final String GET_CAT_EYE_INFO_BASE = "GET_CAT_EYE_INFO_BASE";

    //获取第一次进入锁密码
    public static final String FIRST_IN_GATEWAY_LOCK = "firstInGatewayLock2";

    public static final String BAR_CODE = "BAR_CODE";
    //来源
    public static final String SOURCE = "source";
    //保存服务器的时间的Key  毫秒数
    public static final String SERVER_CURRENT_TIME = "Server_Current_time";
    //用户管理个数
    public static final String USER_MANAGE_NUMBER = "user_manage_number";
    //网关ota升级
    public static final String GATEWAY_OTA_UPGRADE = "gateway_ota_upgrade";

    public static final String GATEWAY_NIGHT_SIGHT = "GATEWAY_NIGHT_SIGHT";

    public static final String GATEWAY_PASSWORD_BEAN = "gatewayPasswordBean";

    public static final String GATEWAY_PASSWORD_BOUND = "gatewayPasswordBound";

    public static final int TAG_NUMBER = 2001;
    public static final int TAG_OTA_TYPE = 2002;


    public static final String KEY_TYPE = "keyType";  //
    public static final String WIFI_SN = "wifiSn";
    public static final String WIFI_LOCK_PASSWORD_LIST = "WifiLockPasswordList";  //WiFi锁密码列表
    public static final String WIFI_LOCK_SHARE_USER_LIST = "WifiLockShareUserList";  //WiFi锁密码列表
    public static final String WIFI_LOCK_ALARM_RECORD = "WifiLockAlarmRecord";  //WiFi锁 报警记录
    public static final String WIFI_LOCK_OPERATION_RECORD = "WifiLockOperationRecord";  //WiFi锁操作记录
    public static final String SHARE_USER_INFO = "shareUserInfo";   //分享用户信息
    public static final String WIFI_LOCK_ADMIN_PASSWORD = "wifiLockAdminPassword";  //WiFi锁管理员密码
    public static final String WIFI_LOCK_ADMIN_PASSWORD_TIMES = "wifiLockAdminPasswordTimes";  //W管理员密码输入次数
    public static final String WIFI_LOCK_WIFI_TIMES = "wifiLockAdminPasswordTimes";  //wifi账号密码输入次数
    public static final String WIFI_LOCK_ADMIN_PASSWORD_DATA = "wifiLockAdminPasswordData";  //WiFi锁管理员密码
    public static final String WIFI_LOCK_INFO = "wifiLockInfo"; //WiFi锁信息
    public static final String WIFI_LOCK_FUNCTION = "wifiLockFunction"; //WiFi锁信息
    public static final String WIFI_LOCK_OPEN_COUNT = "wifiLockOpenCount"; //WiFi锁开锁次数
    public static final String WIFI_LOCK_SETUP_IS_AP = "wifiLockSetUpType"; //WiFi锁配网方式是否是Ap
    public static final String WIFI_LOCK_HOME_PASSWORD = "wifiLockHomePassword"; //WiFi锁配网方式是否是Ap
    public static final String WIFI_LOCK_HOME_SSID = "wifiLockHomeSsid"; //WiFi锁配网方式是否是Ap
    public static final String WIFI_LOCK_NEW_NAME = "wifiLockNewName"; //WiFi锁配网方式是否是Ap

    public static final String WIFI_LOCK_RANDOM_CODE = "wifiLockRandomCode"; //获取的随机码
    public static final String WIFI_LOCK_FUNC = "wifiLockFunc"; //wifi锁功能集

    public static final String WIFI_LOCK_WIFI_SSID = "wifiLockWifiSsid";
    public static final String WIFI_LOCK_WIFI_SSID_ARRAYS = "wifiLockWifiSsidArrays";
    public static final String WIFI_LOCK_WIFI_BSSID = "wifiLockWifiBSsid";
    public static final String WIFI_LOCK_WIFI_PASSWORD = "wifiLockWifiPassword";

    public static final String WIFI_LOCK_CONNECT_NAME = "wifiLockConnectName";  //连接的wifi名称
    public static final String WIFI_LOCK_CONNECT_ORIGINAL_DATA = "wifiLockConnectOriginalData"; //连接的wifi对应的原始数据

    public static final String WIFI_VIDEO_LOCK_OPERATION_RECORD = "wifi_video_lock_operation_record";
    public static final String WIFI_VIDEO_LOCK_ALARM_RECORD = "wifi_video_lock_alarm_record";
    public static final String WIFI_VIDEO_LOCK_VISITOR_RECORD = "wifi_video_lock_visitor_record";

    public static final String WIFI_VIDEO_LOCK_DEVICE_DATA = "wifi_video_device_data";

    public static final String WIFI_VIEDO_LOCK_INFO = "wifi_video_lock_info";

    public static final String WIFI_VIDEO_LOCK_RANDOMCODE = "wifi_video_lock_randomcode";

    public static final String WIFI_VIDEO_LOCK_ALARM_RECORD_DATA = "wifi_video_lock_alarm_record_data";//WifiVideoLockAlarmRecord的数据

    public static final String WIFI_VIDEO_LOCK_XM_CONNECT = "wifi_video_lock_xm_connect";//讯美P2P是否连接

    public static final String CLOTHES_HANGER_PASSWORD_TIMES = "clothes_hanger_password_times";//晾衣机wifi校验次数

    public static final String SCAN_TYPE = "scanType";  //扫描类型
    public static final String QR_URL = "qrUrl";  //扫描到的链接
    public static final String URL_RESULT = "urlResult";  //扫描到的链接

    public static final String ClOTHES_HANGER_MACHINE_WIFI_SSID = "clothes_hanger_machine_wifi_ssid";

    public static final String ClOTHES_HANGER_MACHINE_WIFI_PASSWORD = "clothes_hanger_machine_wifi_password";

    public static final int WIFI_VIDEO_LOCK_PIR_SEN_1 = 35;
    public static final int WIFI_VIDEO_LOCK_PIR_SEN_2 = 70;
    public static final int WIFI_VIDEO_LOCK_PIR_SEN_3 = 90;


    /**
     *  晾衣机功能
     */
    public final static int CLOTHES_HANGER_MACHINE_FUNCTION_BAKING = 4001;
    public final static int CLOTHES_HANGER_MACHINE_FUNCTION_AIR_DRY = 4002;
    public final static int CLOTHES_HANGER_MACHINE_FUNCTION_MOTOR_UP = 4003;
    public final static int CLOTHES_HANGER_MACHINE_FUNCTION_MOTOR_DOWN = 4004;
    public final static int CLOTHES_HANGER_MACHINE_FUNCTION_MOTOR_PAUSE = 4005;


    /**
     * 传递网关Model
     */
    public static final String GATEWAY_MODEL = "gateway_model";

    /**
     * 离线密码因子
     */
    public static final String PASSWORD_FACTOR = "password_Factor";

    /**
     * 单火开关的开关个数
     */
    public static final String SWITCH_NUMBER = "switch_Number";
    /**
     * 单火开关的开关键位
     */
    public static final String SWITCH_KEY_NUMBER = "switch_Key_Number";
    /**
     * 添加单火开关模型
     */
    public static final String SWITCH_MODEL = "switch_model";
    /**
     * 单火开关数据变更
     */
    public static final String WIFI_LOCK_INFO_CHANGE = "wifi_Lock_Info_Change";
    /**
     * 单火开关数据结果
     */
    public static final String WIFI_LOCK_INFO_CHANGE_RESULT = "wifi_Lock_Info_Change_Result";

    /**
     * WIFI列表选择的wifi名称
     */
    public static final String CHOOSE_WIFI_NAME = "choose_wifi_name";

    /**
     *  显示首页
     */
    public static final String WIFI_LOCK_SHOW_HOME_PAGE = "wifi_lock_show_home_page";

    /**
     *  Video的路径
     */
    public static final String VIDEO_PIC_PATH = "video_pic_path";

    /**
     *  是否显示删除按钮
     */
    public static final String VIDO_SHOW_DELETE = "isShowDelete";

    /**
     *  呼叫或者实时视频
     */
    public static final String WIFI_VIDEO_LOCK_CALLING = "wifi_video_lock_calling";

    /**
     *  PIR灵敏度
     */
    public static final String WIFI_VIDEO_PIR_SENSITIVITY = "wifi_video_lock_pir_sensitivity";

    /**
     * 徘徊时间
     */
    public static final String WIFI_VIDEO_WANDERING_TIME = "wifi_video_lock_wandering_time";

    /**
     *  pri
     */
    public static final String WIFI_VIDEO_WANDERING_SENSITIVITY = "wifi_video_lock_wandering_sensitivity";

    /**
     * 安全模式
     */
    public static final String WIFI_VIDEO_LOCK_SAFE_MODE = "wifi_video_lock_safe_mode";

    /**
     * 自动模式
     */
    public static final String WIFI_VIDEO_LOCK_AM_MODE ="wifi_video_lock_am_mode";

    /**
     * 门锁语言
     */
    public static final String WIFI_VIDEO_LOCK_LANGUAGE = "wifi_video_lock_language";

    /**
     * 徘徊报警
     */
    public static final String WIFI_VIDEO_LOCK_WANDERING_ALARM = "wifi_video_lock_wandering_alarm";

    /**
     * 徘徊报警-预警信息
     */
    public static final String WIFI_VIDEO_LOCK_WANDERING_ALARM_PIR_FLAG = "wifi_video_lock_wandering_alarm_pir_flag";

    /**
     *  开始时间
     */
    public static final String WIFI_VIDEO_LOCK_REAL_TIME_SETTING_START = "wifi_video_lock_real_time_setting_start";

    /**
     * 结束时间
     */
    public static final String WIFI_VIDEO_LOCK_REAL_TIME_SETTING_END = "wifi_video_lock_real_time_setting_end";

    /**
     *  重复规则
     */
    public static final String WIFI_VIDEO_LOCK_REAL_TIME_PERIOD = "wifi_video_lock_real_time_period";

}

