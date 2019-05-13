package com.kaadas.lock.publiclibrary.http;

/**
 * Create By lxj  on 2019/3/3
 * Describe
 */
public class HttpUrlConstants {
    //public static final String BASE_URL = "https://app.xiaokai.com:8090/";//小凯正式服务器
     //public static final String BASE_URL = "https://app-kaadas.juziwulian.com:34000/";//凯迪仕正式服务器
    //public static final String BASE_URL = "https://47.107.175.212:8090/";//小凯测试服务器
  public static final String BASE_URL = "https://test.juziwulian.com:8090/";//凯迪仕测试服务器

    ////////////////////////////////////////2019年2月27日15:02:43  小凯新加接口////////////////


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
    public static final String FAQ_LIST=BASE_URL+"FAQ/list/";

    /**
     *消息列表
     */
    public static final String SYSTEM_MESSAGE=BASE_URL+"systemMessage/list";

    /**
     * 删除消息列表
     */
    public static final String SYSTEM_MESSAGE_DELETE=BASE_URL+"systemMessage/delete";

    /**
     * 查询日志列表
     */
    public static final String SYSTEM_HELP_LOG=BASE_URL+"errHelpLog/list";


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
     * 获取开门记录
     */
    public static final String GET_LOCK_RECORD = BASE_URL + "openlock/downloadopenlocklist";



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
    public static final String GET_APP_VERSION="http://s.kaadas.com:8989/cfg/SoftMgr/app.json";

  /**
   * OTA  升级  API地址
   */
  public static final String OTA_INFO_URL = "http://test.juziwulian.com:9000/api/otaUpgrade/check";

  /**
   * 用户开锁鉴权
   */
  public static final String USER_OPEN_LOCK_AUTHORITY = BASE_URL + "adminlock/open/openLockAuth";

  public static final String UPLOAD_PUSH_ID = BASE_URL + "user/upload/pushId";
}
