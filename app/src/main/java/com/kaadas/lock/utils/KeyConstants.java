package com.kaadas.lock.utils;

/**
 * Create By lxj  on 2019/2/14
 * Describe
 * 各种常量Key的静态类
 * 每个Key都需要注释有什么作用
 */
public class   KeyConstants {
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
    public static final String GATEWAY_ID="gatewayId";
    //网关的昵称
    public static final String GATEWAY_NICKNAME="gatewayNickName";

    //是否是管理员
    public static final String IS_ADMIN="isadmin";


    //设备的id
    public static final String DEVICE_ID="deviceId";
    //猫眼
    public static final String CATEYE="cateye";
    //网关锁
    public static final String GATEWAY_LOCK="gateway_lock";

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
    public static final String DEVICE_DETAIL_BEAN="deviceDetailBean";

    //是否保存了开锁密码
    public static final String SAVA_LOCK_PWD="saveLockPwd";

    //开锁密码编号
    public static final String LOCK_PWD_NUMBER="lockPwdNumber";

    //开锁密码集合
    public static final String LOCK_PWD_LIST="lockPwdList";

    //开锁密码编号添加
    public static final String ADD_LOCK_PWD_NUMBER="addLockPwdNumber";

    //设备名称
    public static final String NAME="name";

    //设置onActivityResult
    public static final int DEVICE_DETAIL_BEAN_NUM=1000;

    public static final int DELETE_PWD_REQUEST_CODE=1001;

    public static final int ADD_PWD_REQUEST_CODE=1002;

    public static final int UPDATE_DEVICE_NAME_REQUEST_CODE=1003;

    //铃声
    public static final int RING_NUMBER_REQUESET_CODE=1004;

    //音量
    public static final int VOLUME_REQUESET_CODE=1005;

    //分辨率
    public static final int RESOLUTION_REQUEST_CODE=1006;

    //扫描猫眼跳转
    public static final int SCANCATEYE_REQUEST_CODE=1007;

    //扫描网关的跳转
    public static final int SCANGATEWAY_REQUEST_CODE=1008;

    //使用扫一扫页面跳转
    public static final int SCANGATEWAYNEW_REQUEST_CODE=1009;

    //产品激活
    public static final int SCANPRODUCT_REQUEST_CODE=1010;

    //网关昵称修改
    public static final int GATEWAY_NICK_NAME=1011;

    //铃声次数
    public static final String RIGH_NUMBER="ringNumber";

    //音量
    public static final String VOLUME_NUMBER="volumeNumber";

    //分辨率
    public static final String RESOLUTION_NUMBER="resolutionNumber";

    //传递蓝牙电量
    public static final int GET_BLE_POWER=1004;


    /**
     * 传递蓝牙设备信息的Key
     */
    public static final String BLE_LOCK_INFO = "bleLockInfo";
    /**
     * 传递当前位置
     */
    public static final String FRAGMENT_POSITION = "fragmentPosition";
    /**
     *  传递猫眼数据
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
     *  是否是呼叫进来
     */
    public static final String IS_CALL_IN = "isCallIn";

    /**
     *  是否是呼叫进来
     */
    public static final String IS_ACCEPT_CALL = "isAcceptCall";

    /**
     * 升级时，保存的设备蓝牙版本号的Key
     */
    public static final String BLE_VERSION= "bleVersion";

    //区分密码是永久有效还是，临时有效
    public static final String PWD_TYPE="pwdType";
    //密码值
    public static final String PWD_VALUE="pwdValue";
    //密码编号
    public static final String PWD_ID="pwdId";

    //密码编号管理
    public static final String ADD_PWD_ID="addPwdId";
    //添加胁迫密码编号
    public static final String ADD_STRESS_PWD_ID="addStressPwdId";

    //获取猫眼信息
    public static final String GET_CAT_EYE_INFO="getCatEyeInfo";

    //上报类型为猫眼
    public static final String DEV_TYPE_CAT_EYE = "kdscateye";

    /**
     * 上报类型为网关锁
     */
    public static final String DEV_TYPE_LOCK= "kdszblock";

    //蓝牙电量信息
    public static final String BLE_INTO="bleInfo";

    //响铃次数
    public static final String CAT_EYE_RING_NUMBER="catEyeRingNumber";

    //分辨率
    public static final String CAT_EYE_RESOLUTION="catEyeResolution";

    //账号
    public static final String ACCOUNT="account";

    //密码
    public static final String PASSWORD="password";
    //地区码
    public static final String AREA_CODE="area_code";
    //国家
    public static final String COUNTRY="country";

    //铃声音量
    public static final String CAT_EYE_VOLUME="catEyeVolume";
    /**
     * 锁型号
     */
    public static final String LOCK_MODEL = "lock_model";

    //是否绑定了咪咪网
    public static final String IS_BIND_MEME="isbindMeme";


    //获取猫眼信息
    public static final String GET_CAT_EYE_INFO_BASE="GET_CAT_EYE_INFO_BASE";

    //获取第一次进入锁密码
    public static final String FIRST_IN_GATEWAY_LOCK="firstInGatewayLock";

    public static final String BAR_CODE="BAR_CODE";
    //来源
    public static final String SOURCE="source";
    //保存服务器的时间的Key  毫秒数
    public static final String SERVER_CURRENT_TIME = "Server_Current_time";
    //用户管理个数
    public static final String USER_MANAGE_NUMBER="user_manage_number";
    //网关ota升级
    public static final String GATEWAY_OTA_UPGRADE="gateway_ota_upgrade";
}

