package com.kaadas.lock.utils;

/**
 * Create By lxj  on 2019/2/14
 * Describe
 * 各种常量Key的静态类
 * 每个Key都需要注释有什么作用
 */
public class KeyConstants {
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
     * */
    public static final int SELECT_COUNTRY_REQUEST_CODE=12;
}
