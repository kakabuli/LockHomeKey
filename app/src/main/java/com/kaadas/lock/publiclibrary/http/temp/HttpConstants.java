package com.kaadas.lock.publiclibrary.http.temp;

/**
 * Create By lxj  on 2019/1/8
 * Describe
 */
public class HttpConstants {

    /*******************************************************/
    public static final String BASE_URL = "https://app-kaadas.juziwulian.com:34000/";//正式服务器
//    public static final String BASE_URL = "https://47.107.175.212:8090/";//测试服务器
//    public static final String BASE_URL = "https://app.xiaokai.com:8090/";//正式服务器

    /**
     * linphone的地址
     */
    public static final String LINPHONE_URL = "sip-kaadas.juziwulian.com:5061";//正式sip
    /**
     * 获取短信验证码 get
     * <p>
     * /
     * public static final String SEND_MSG = BASE_URL+"sms/sendSmsTokenByTX";
     * <p>
     * <p>
     * /**
     * 获取国际短信验证码 get
     */
    public static final String SEND_INTERNATIONAL_MSG = BASE_URL + "sms/sendSmsTokenByTX";
    /**
     * 邮箱短信获取验证码 post
     */

    public static final String GET_EMAIN_YZM = BASE_URL + "mail/sendemailtoken";
    /**
     * 手机号登录 get
     */
    public static final String PHONE_LOGIN = BASE_URL + "user/login/getuserbytel";
    /**
     * 邮箱登录 get
     */
    public static final String EMAIL_LOGIN = BASE_URL + "user/login/getuserbymail";
    /**
     * 邮箱注册 post
     */
    public static final String EMAIL_REGISTER = BASE_URL + "user/reg/putuserbyemail";
    /**
     * 重登陆 get
     */
    public static final String RE_LOGIN = BASE_URL + "user/login/getreloginuser";
    /**
     * 手机注册 post
     */
    public static final String PHONE_REGISTER = BASE_URL + "user/reg/putuserbytel";
    /**
     * 忘记密码 post
     */
    public static final String EMAIL_FORGET_PWD = BASE_URL + "user/edit/forgetPwd";
    /**
     * 获取设备列表 post
     */
    public static final String GET_LOCK_LIST = BASE_URL + "adminlock/edit/getAdminDevlist";
    /**
     * 判断锁是否被绑定 post
     */
    public static final String CHECK_LOCK_BIND = BASE_URL + "adminlock/edit/checkadmindev";
    /**
     * 生成锁的新管理员 post
     */
    public static final String CREATE_LOCK_ADMIN = BASE_URL + "adminlock/reg/createadmindev";
    /**
     * 删除锁 post
     */
    public static final String RESET_LOCK_ADMIN = BASE_URL + "adminlock/reg/deleteadmindev";
    /**
     * 重置所有锁的管理员 最大权限 post
     */
    public static final String RESET_LOCK_ALL_ADMIN = BASE_URL + "adminlock/reg/deletevendordev";
    /**
     * 修改用户昵称 post
     */
    public static final String CHANGE_NICK_NAME = BASE_URL + "user/edit/postUsernickname";
    /**
     * 修改设备昵称 post
     */
    public static final String UPDATE_LOCK_NICKNAME = BASE_URL + "adminlock/edit/updateAdminlockNickName";
    /**
     * 管理员开锁 post
     */
    public static final String OPEN_LOCK = BASE_URL + "adminlock/open/adminOpenLock";
    //用户开锁鉴权
    public static final String USER_OPEN_LOCK_AUTHORITY = BASE_URL + "adminlock/open/openLockAuth";
    /**
     * 获取设备经纬度 post
     */
    public static final String GET_LOCTION = BASE_URL + "adminlock/edit/getAdminDevlocklongtitude";
    /**
     * 获取获取指定设备的普通管理员 post
     */
    public static final String GET_NORMALS_DEVLIST = BASE_URL + "normallock/ctl/getNormalDevlist";
    /**
     * 添加设备的普通用户 post
     */
    public static final String CREATENOR_NORMALDEV = BASE_URL + "normallock/reg/createNormalDev";
    /**
     * 修改设备的普通用户的权限 post
     */
    public static final String UPDATE_NORMALDEV = BASE_URL + "normallock/ctl/updateNormalDevlock";
    /**
     * 删除设备的普通用户 post
     */
    public static final String DELETE_NORMALDEV = BASE_URL + "normallock/reg/deletenormaldev";
    /**
     * 修改原始密码 post
     */
    public static final String CHANGE_PWD = BASE_URL + "user/edit/postUserPwd";
    /**
     * 获取用户昵称 post
     */
    public static final String GET_NICKNAME = BASE_URL + "user/edit/getUsernickname";
    /**
     * 修改昵称 post
     */
    public static final String CHANGE_NICKNAME = BASE_URL + "user/edit/postUsernickname";
    /**
     * 用户反馈 post
     */
    public static final String USER_FEEDBACK = BASE_URL + "suggest/putmsg";
    /**
     * 上传开锁记录 post
     */
    public static final String UPDATA_OPENLOCK = BASE_URL + "openlock/uploadopenlocklist";
    /**
     * 获取开锁记录 get
     */
    public static final String GET_OPEN_RECORD = BASE_URL + "openlock/downloadopenlocklist";
    /**
     * 更新指点设备的位置信息 post
     */
    public static final String UPDATA_lOCATION = BASE_URL + "adminlock/edit/editadmindev";
    /**
     * 是否启用自动开锁 post
     */
    public static final String IS_AUTO_OPENLOCK = BASE_URL + "adminlock/edit/updateAdminDevAutolock";
    /**
     * 上传头像 post
     */
    public static final String UP_LOAD_USERHEAD = BASE_URL + "user/edit/uploaduserhead";
    /**
     * 下载头像 post
     */
    public static final String DOWN_LOAD_USERHEAD = BASE_URL + "user/edit/showfileonline/";
    /**
     * 根据mac获取psw post
     */
    public static final String GET_PSW_MAC = BASE_URL + "model/getpwdBySN";
    //修改锁编号信息
    public static final String MODIFY_LOCK_NUMBER_INFORMATION = BASE_URL + "adminlock/info/number/update";
    //分类查询
    public static final String CLASSIFICATION_QUERY = BASE_URL + "openlock/Record/select";
    //批量修改锁编号信息
    public static final String MODIFY_LOCK_NUMBER_INFORMATION_BATCHES = BASE_URL + "adminlock/info/number/bulkupdate";
    //获取锁编号对应的昵称
    public static final String GET_LOCK_USER_NUMBER_NICKNAME = BASE_URL + "adminlock/info/number/get";

}
