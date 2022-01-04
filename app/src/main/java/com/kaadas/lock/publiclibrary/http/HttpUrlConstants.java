package com.kaadas.lock.publiclibrary.http;

import com.kaadas.lock.utils.ConstantConfig;

/**
 * Create By lxj  on 2019/3/3
 * Describe
 */
public class HttpUrlConstants {
    public static final String BASE_URL = ConstantConfig.HTTP_BASE_URL;

    /**
     * OTA  升级  API地址
     */
    public static final String OTA_INFO_URL = ConstantConfig.OTA_INFO_URL + "ota/checkUpgrade";  //测试OTA服务器 otaUpgrade/check

    /**
     *  OTA 多固件检查更新
     */
    public static final String OTA_MULTI_CHECK = ConstantConfig.OTA_INFO_URL + "ota/multiCheckUpgrade";//otaUpgrade/multiCheck
    /**
     * OTA  升级结果上报  API地址
     */
    public static final String OTA_RESULT_UPLOAD_URL = ConstantConfig.OTA_RESULT_UPLOAD_URL;  //测试OTA服务器

    //////////////////////////////////////// 注册登陆/////////////////////////////////////////////////
    /**
     * 通过手机注册
     */
    public static final String REGISTER_BY_PHONE = BASE_URL + "user/reg/putuserbytel";

    /**
     * 邮箱注册
     */
    public static final String EMAIL_REGISTER = BASE_URL + "user/reg/putuserbyemail";
    /**
     * 通过手机登陆
     */
    public static final String LOGIN_BY_PHONE = BASE_URL + "user/login/getuserbytel";

    /**
     * 邮箱登录
     */
    public static final String EMAIL_LOGIN = BASE_URL + "user/login/getuserbymail";
    /**
     * 忘记密码
     */
    public static final String FORGET_PASSWORD = BASE_URL + "user/edit/forgetPwd";

    /**
     * 修改密码
     */
    public static final String MODIFY_PASSWORD = BASE_URL + "user/edit/postUserPwd";

    /**
     * 退出登陆
     */
    public static final String LOGIN_OUT = BASE_URL + "user/logout";

    //////////////////////////////////////////////用户管理/////////////////////////////////////////
    /**
     * 获取用户昵称
     */
    public static final String GET_USER_NICK_NAME = BASE_URL + "user/edit/getUsernickname";

    /**
     * 修改用户昵称
     */
    public static final String MODIFY_USER_NICK_NAME = BASE_URL + "user/edit/postUsernickname";

    /**
     * 上传用户头像
     */
    public static final String UPLOAD_USER_HEARD = BASE_URL + "user/edit/uploaduserhead";


    /**
     * 获取用户头像
     */
    public static final String GET_USER_HEARD = BASE_URL + "user/edit/showfileonline/:uid";

    /**
     * 用户留言
     */
    public static final String PUT_MESSAGE = BASE_URL + "suggest/putmsg";
    /**
     * 获取获取指定设备的普通管理员 post
     */
    public static final String GET_NORMALS_DEVLIST = BASE_URL + "normallock/ctl/getNormalDevlist";
    /**
     * 删除设备的普通用户 post
     */
    public static final String DELETE_NORMALDEV = BASE_URL + "normallock/reg/deletenormaldev";
    /**
     * 获取用户协议版本
     */
    public static final String GET_USER_PROTOCOL_VERSION = BASE_URL + "user/protocol/version/select";

    /**
     * 获取用户协议
     */
    public static final String GET_USER_PROTOCOL_CONTENT = BASE_URL + "user/protocol/content";


    ////////////////////////////////////////////////门锁管理/////////////////////////////////////////////////////////////
    /**
     * •	管理员添加设备
     */
    public static final String ADD_DEVICE = BASE_URL + "adminlock/reg/createadmindev";


    /**
     * 查询列表
     */
    public static final String GET_DEVICES = BASE_URL + "adminlock/edit/getAdminDevlist";

    /**
     * 删除设备
     */
    public static final String DELETE_DEVICE = BASE_URL + "adminlock/reg/deleteadmindev";


    /**
     * 修改门锁昵称
     */
    public static final String MODIFY_LOCK_NICK_NAME = BASE_URL + "adminlock/edit/updateAdminlockNickName";


    /**
     * .根据SN获取蓝牙密码
     */
    public static final String GET_PWD_BY_SN = BASE_URL + "model/getpwdBySN";


    /**
     * 三方重置门锁
     */
    public static final String RESET_DEVICE = BASE_URL + "adminlock/reg/deletevendordev";


    /**
     * 门锁密钥添加
     */
    public static final String ADD_PASSWORD = BASE_URL + "adminlock/pwd/add";


    /**
     * 删除秘钥
     */
    public static final String DELETE_PASSWORD = BASE_URL + "adminlock/pwd/delete";


    /**
     * 修改秘钥昵称
     */
    public static final String MODIFY_PASSWORD_NICK = BASE_URL + "adminlock/pwd/edit/nickname";


    /**
     * 查询秘钥列表
     */
    public static final String GET_PASSWORDS = BASE_URL + "adminlock/pwd/list";


    /**
     * 查询单个秘钥
     */
    public static final String GET_SINGLE_PASSWORD = BASE_URL + "adminlock/pwd/getNickname";

    /**
     * 判断锁是否被绑定 post
     */
    public static final String CHECK_LOCK_BIND = BASE_URL + "adminlock/edit/checkadmindev";


    ////////////////////////////////////////////用户管理////////////////////////////////////////////////

    /**
     * 添加普通用户
     */
    public static final String ADD_USER = BASE_URL + "normallock/reg/createNormalDev";
    /**
     * 修改普通用户昵称
     */
    public static final String MODIFY_COMMON_USER_NICKNAME = BASE_URL + "normallock/reg/updateNormalDevUnickName";
    /**
     * 删除普通用户
     */
    public static final String DELETE_USER = BASE_URL + "normallock/reg/createNormalDev";


    /**
     * 查询普通用户列表
     */
    public static final String SEARCH_USER = BASE_URL + "normallock/ctl/getNormalDevlis";

    /**
     * 常见问题列表
     */
    public static final String FAQ_LIST = BASE_URL + "FAQ/list/";

    /**
     * 消息列表
     */
    public static final String SYSTEM_MESSAGE = BASE_URL + "systemMessage/list";

    /**
     * 删除消息列表
     */
    public static final String SYSTEM_MESSAGE_DELETE = BASE_URL + "systemMessage/delete";

    /**
     * 查询日志列表
     */
    public static final String SYSTEM_HELP_LOG = BASE_URL + "errHelpLog/list";


    //////////////////////////////////////开门记录///////////////////////////////////////////////

    /**
     * 上传秘钥开门记录
     */
    public static final String UPLOAD_BIN_RECORD = BASE_URL + "openlock/uploadopenlocklist";

    /**
     * 上传开门记录
     */
    public static final String UPLOAD_APP_RECORD = BASE_URL + "adminlock/open/adminOpenLock";

    /**
     * 获取操作记录
     */
    public static final String GET_OPERATION_RECORD = BASE_URL + "operation/list";

    /**
     * 获取开门记录
     */
    public static final String GET_LOCK_RECORD = BASE_URL + "openlock/downloadopenlocklist";

    /**
     * 上传操作记录
     */
    public static final String UPLOAD_OPERATION_RECORD = BASE_URL + "operation/add";
    ////////////////////////////////////////////预警记录////////////////////////////////////////////

    /**
     * 上报预警记录
     */
    public static final String UPLOAD_WARRING_RECORD = BASE_URL + "warning/upload";

    /**
     * 获取预警记录
     */
    public static final String GET_WARRING_RECORD = BASE_URL + "warning/list";


    /**
     * 发送短信验证码
     */
    public static final String SEND_MSG = BASE_URL + "sms/sendSmsTokenByTX";
    /**
     * 发送邮箱验证码
     */
    public static final String GET_EMAIL_YZM = BASE_URL + "mail/sendemailtoken";
    /**
     * 上传头像 post
     */
    public static final String UP_LOAD_USERHEAD = BASE_URL + "user/edit/uploaduserhead";
    /**
     * 下载头像 post
     */
    public static final String DOWN_LOAD_USERHEAD = BASE_URL + "user/edit/showfileonline/";

    /**
     * App版本信息地址
     */
    public static final String GET_APP_VERSION = "http://s.kaadas.com:8989/cfg/SoftMgr/app.json";


    /**
     * 用户开锁鉴权
     */
    public static final String USER_OPEN_LOCK_AUTHORITY = BASE_URL + "adminlock/open/openLockAuth";

    public static final String UPLOAD_PUSH_ID = BASE_URL + "user/upload/pushId";

    public static final String UPLOAD_PHONE_MSG = ConstantConfig.OTA_INFO_URL + "mobile/add";

    public static final String GET_PUSH_SWITch = BASE_URL + "user/get/getPushSwitch";
    public static final String UPDATE_PUSH_SWITch = BASE_URL + "user/edit/postPushSwitch";


    /**
     * OTA之前  上传当前设备SN和版本号
     */
    public static final String UPDATE_SOFTWARE_VERSION = BASE_URL + "adminlock/reg/updateSoftwareVersion";


    /**
     * 修改蓝牙版本
     */
    public static final String UPDATE_BLE_VERSION = BASE_URL + "adminlock/reg/updateBleVersion";


    /**
     * 修改功能集
     */

    public static final String MODIFY_FUNCTION_SET = BASE_URL + "adminlock/reg/updateFunctionSet";


    ////////////////////////////////////////////           WiFi锁api功能            ///////////////////////////////////////////////
    /**
     * 绑定WiFi锁
     */
    public static final String WIFI_LOCK_BIND = BASE_URL + "wifi/device/bind";


    /**
     * 解绑WiFi锁
     */
    public static final String WIFI_LOCK_UNBIND = BASE_URL + "wifi/device/unbind";

    /**
     * 预绑定设备
     */
    public static final String WIFI_LOCK_PRE_BINDING = BASE_URL + "device/prebinding";


    /**
     * 修改WiFi昵称
     */
    public static final String WIFI_LOCK_UPDATE_NICK_NAME = BASE_URL + "wifi/device/updatenickname";


    /**
     * 修改推送开关
     */
    public static final String WIFI_LOCK_UPDATE_PUSH = BASE_URL + "wifi/device/updatepushswitch";


    /**
     * 获取wifi锁密码列表
     */
    public static final String WIFI_LOCK_GET_PWD_LIST = BASE_URL + "wifi/pwd/list";


    /**
     * 修改wifi密码昵称
     */
    public static final String WIFI_LOCK_UPDATE_PWD_NICKNAME = BASE_URL + "wifi/pwd/updatenickname";

    /**
     * wifi锁授权用户
     */
    public static final String WIFI_LOCK_SHARE = BASE_URL + "wifi/share/add";

    /**
     * wifi锁删除授权用户
     */
    public static final String WIFI_LOCK_DELETE_SHARE = BASE_URL + "wifi/share/del";

    /**
     * wifi锁修改授权用户昵称
     */
    public static final String WIFI_LOCK_UPDATE_SHARE_NICKNAME = BASE_URL + "wifi/share/updatenickname";
    /**
     * wifi锁授权用户列表
     */
    public static final String WIFI_LOCK_GET_SHARE_LIST = BASE_URL + "wifi/share/list";

    /**
     * 获取wifi锁操作记录
     */
    public static final String WIFI_LOCK_OPERATION_LIST = BASE_URL + "wifi/operation/list";

    /**
     * 获取wifi报警记录
     */
    public static final String WIFI_LOCK_ALARM_LIST = BASE_URL + "wifi/alarm/list";
    /**
     * 获取开锁次数
     */
    public static final String WIFI_LOCK_OPEN_COUNT = BASE_URL + "wifi/operation/opencount";


    /**
     * 设备信息修改
     */
    public static final String WIFI_LOCK_UPDATE_INFO = BASE_URL + "wifi/device/infoUpdate";


    /**
     * 确认升级（单组件）
     */
    public static final String WIFI_LOCK_UPLOAD_OTA = BASE_URL + "wifi/device/ota";

    /**
     * 确认升级（多组件）
     */
    public static final String WIFI_LOCK_UPLOAD_MULTI_OTA = BASE_URL + "wifi/device/multiOta";


    /**
     * 获取开锁次数
     */
    public static final String  GET_ZIGBEEN_INFO = BASE_URL + "v1/user/getZigBeeInfo";

    /**
     *  单火开关设备联动开关昵称修改
     */
    public static final String UPDATE_SWITCH_NICK_NAME = BASE_URL + "wifi/device/updateSwitchNickname";

    /**
     * 设置密钥胁迫报警接收账户
     */
    public static final String WIFI_PWD_DURESS_ACCOUNT = BASE_URL + "user/edit/pwdDuressAccount";

    /**
     * 设置密钥胁迫报警开关
     */
    public static final String WIFI_PWD_DURESS_ALARM = BASE_URL + "user/edit/pwdDuressAlarm";

    /**
     * 设置胁迫报警开关
     */
    public static final String WIFI_DURESS_ALARM_SWITCH = BASE_URL + "user/edit/duressAlarmSwitch";


    ////////////////////////////////////////////           WiFi视频锁api功能            ///////////////////////////////////////////////

    /**
     *  app绑定设备
     */
    public static final String WIFI_VIDEO_LOCK_BIND = BASE_URL + "wifi/vedio/bind";

    /**
     *  app解绑设备
     */
    public static final String WIFI_VIDEO_LOCK_UNBIND = BASE_URL + "wifi/vedio/unbind";

    /**
     *  app绑定设备失败
     */
    public static final String WIFI_VIDEO_LOCK_BIND_FAIL = BASE_URL + "wifi/vedio/bindFail";

    /**
     *  更新设备
     */
    public static final String WIFI_VIDEO_LOCK_UPDATE_BIND = BASE_URL + "wifi/vedio/updateBind";

    /**
     * 获取视频锁报警记录
     */
    public static final String WIFI_VIDEO_LOCK_ALARM_LIST = BASE_URL + "wifi/vedio/alarmList";

    /**
     * 获取视频锁门铃记录
     */
    public static final String WIFI_VIDEO_LOCK_DOORBELL_LIST = BASE_URL + "wifi/vedio/doorbellList";

    /**
     *  查询wifi锁设备列表
     */
    public static final String WIFI_DEVICE_LIST = BASE_URL + "wifi/device/list";

    /**
     * 查询设备列表
     */
    public static final String GET_ALL_BIND_DEVICES = BASE_URL + "app/user/getAllBindDevice";


    //////////////////////////////////////////晾衣机 Api 功能 //////////////////////////////////////////////
    /**
     * 检查晾衣机设备是否被绑定
     */
    public static final String HANGER_CHECK_BINDING = BASE_URL + "wifi/hanger/checkadmind";

    /**
     * 晾衣机绑定
     */
    public static final String HANGER_BIND = BASE_URL + "wifi/hanger/bind";

    /**
     * 晾衣机解绑
     */
    public static final String HANGER_UNBIND = BASE_URL + "wifi/hanger/unbind";

    /**
     *  晾衣机设备修改昵称
     */
    public static final String MODIFY_HANGER_NICK_NAME = BASE_URL + "/wifi/hanger/updateNickName";
}
