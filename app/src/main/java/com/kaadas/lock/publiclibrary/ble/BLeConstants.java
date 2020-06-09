package com.kaadas.lock.publiclibrary.ble;

/**
 * Create By lxj  on 2019/1/7
 * Describe
 */
public class BLeConstants {
    /**************************************     UUID     *************************************/
    //app->给蓝牙（数据通道）
    public static final String UUID_SEND_SERVICE = "0000ffe5-0000-1000-8000-00805f9b34fb";// 发送数据
    //app->给蓝牙（数据通道）
    public static final String UUID_WRITE_CHAR = "0000ffe9-0000-1000-8000-00805f9b34fb";// 发送数据
    //蓝牙->app  （数据通道）服务
    public static final String UUID_NOTIFY_SERVICE = "0000ffe0-0000-1000-8000-00805f9b34fb";// 通知数据
    //蓝牙->app （数据通道）特征值
    public static final String UUID_NOTIFY_CHAR = "0000ffe4-0000-1000-8000-00805f9b34fb";// 通知charUUID
    //门锁功能集 (数据通道)
    public static final String UUID_FUNCTION_SET = "0000ffe1-0000-1000-8000-00805f9b34fb";// 门锁功能集   最新版的协议才有这个服务  2019年5月9日
    /**
     * 电量特征值 UUID  0-100%，出厂默认值为100%
     */
    public static final String UUID_BATTERY_CHAR = "0000ffb1-0000-1000-8000-00805f9b34fb";//特征值UUID
    /**
     * 蓝牙模块的时间  UUID  BLE时间秒计数。以2000-01-01 00:00:00（本地时间）为起点开始计数
     */
    public static final String UUID_TIME_CHAR = "0000ffb1-0000-1000-8000-00805f9b34fb";//特征值UUID




    /**
     * 获取doorlock的特征值 UUID
     */
    public static final String UUID_DOORLOCK_CHAR = "00002a00-0000-1000-8000-00805f9b34fb";



///////////////////////////////////////////////////服务信息相关特征值///////////////////////////////////////////////////////////////////

    /**
     * 设备信息服务的 UUID   下面是该服务UUI下的特征值UUID
     */
    public static final String UUID_INFO_CHAR = "0000180a-0000-1000-8000-00805f9b34fb";// 读取设备信息服务UUID
    /**
     * 设置systemid特征值的 UUID
     * 系统ID，Hex，xxxxxx0000xxxxxx，”xxxxxxxxxxxxxx”为模块MAC地址，低字节在前
     *
     */
    public static final String UUID_SYSTEMID_CHAR = "00002a23-0000-1000-8000-00805f9b34fb";

    /**
     * 模块代号的 特征值的 UUID   模块代号，“RGBT1761K”  区分不同模块
     */
    public static final String UUID_MODE_CHAR = "00002a24-0000-1000-8000-00805f9b34fb";

    /**
     * 读取SN 特征值 UUID  CCXXYYWWPNNNN    CC：产品型号    XX：工厂代码    YY：年代码    WW：周代码    P:批次号    NNNN：流水号
     */
    public static final String UUID_INFO_SN_CHAR = "00002a25-0000-1000-8000-00805f9b34fb";
    /**
     *  蓝牙锁型号（特征值2A26）：包含DB2字符串，则密码数最大为20.
     *  锁型号，ASCII，“xxxxxxxVx.xx”
     *  出厂默认值：xxxxxVx.x
     *  此特征值区分不同的锁
     */
    public static final String UUID_LOCK_TYPE = "00002a26-0000-1000-8000-00805f9b34fb";

    /**
     *  硬件版本，“CCVxx.x”
     *  CC：产品型号
     */
    public static final String UUID_HARDWARE_INFO = "00002a27-0000-1000-8000-00805f9b34fb";

    /**
     * 蓝牙版本
     * 软件版本，“VM.mm.bbb”
     * M   主版本号
     * mm  子版本号
     * bbb  内部版本序号
     */
    public static final String UUID_BLE_VERSION = "00002a28-0000-1000-8000-00805f9b34fb";// 读取设备信息特征值UUID


    ////////////////////////////////////////////////////锁设置信息/////////////////////////////////////////////////////////////

    /**
     * 锁设置服务UUID  下面都是锁设置特征值UUID
     */
    public static final String UUID_LOCK_SETTINGS = "0000fff0-0000-1000-8000-00805f9b34fb";


    /**
     * 锁型号   广播服务中获取的信息LockType
     */
    public static final String UUID_LOCK_TYPE_BROADCAST = "0000fff1-0000-1000-8000-00805f9b34fb";
    /**
     * 门锁功能
     */
    public static final String UUID_LOCK_FUNCTION = "0000fff2-0000-1000-8000-00805f9b34fb";
    /**
     * 门锁状态
     */
    public static final String UUID_LOCK_STATUS = "0000fff3-0000-1000-8000-00805f9b34fb";

    /**
     * 门锁语言信息
     * ISO 639-1标准
     * zh：中文(默认)
     * en：英语
     */
    public static final String UUID_LOCK_LANGUAGE = "0000fff4-0000-1000-8000-00805f9b34fb";

    /**
     * 锁音量信息
     * SoundVolume锁音量
     * 0x00：Silent Mode静音
     * 0x01：Low Volume低音量
     * 0x02：High Volume高音量
     * 0x03~0xFF：保留
     */
    public static final String UUID_LOCK_VOICE = "0000fff5-0000-1000-8000-00805f9b34fb";

    /**
     * Ota重启服务 Ti 服务的特征值
     */
    public static final String OAD_RESET_TI_SERVICE = "f000ffd0-0451-4000-b000-000000000000";  //OTA升级的服务UUID

    /**
     *  Ota重启服务 P6 服务的特征值
     */
    public static final String OAD_RESET_P6_SERVICE = "00001802-0000-1000-8000-00805f9b34fb";
    /**
     * 进入boot模式才会有的服务
     */
    public static final String TI_OAD_SERVICE = "f000ffc0-0451-4000-b000-000000000000";    // TI OTA 模式下才有的服务UUID
    /**
     * 进入boot模式才会有的服务
     */
    public static final String P6_OAD_SERVICE = "00060000-f8ce-11e4-abf4-0002a5d5c51b";;    //P6 OTA 模式下才有的服务UUID


    ////////////////////////////////////////////////////BLE&WiFi配网服务/////////////////////////////////////////////////////////////

    /**
     * APP->BLE 配网通道服务
     */
    public static final String DISTRIBUTION_NETWORK_SEND_SERVICE = "0000ffc0-0000-1000-8000-00805f9b34fb";;

    /**
     * BLE->APP 配网通道服务
     */
    public static final String DISTRIBUTION_NETWORK_NOTIFY_SERVICE = "0000ffc5-0000-1000-8000-00805f9b34fb";;

    ////////////////////////////////////////////////////BLE&WiFi配网服务下特征/////////////////////////////////////////////////////////////

    /**
     * APP->BLE （配网通道）特征
     */
    public static final String DISTRIBUTION_NETWORK_SEND_CHAR = "0000ffc1-0000-1000-8000-00805f9b34fb";// 通知charUUID
    /**
     * BLE->APP （配网通道）特征
     */
    public static final String DISTRIBUTION_NETWORK_NOTIFY_CHAR = "0000ffc6-0000-1000-8000-00805f9b34fb";// 通知charUUID
    /**
     * BLE->APP  门锁功能集 （配网通道）特征
     */
    public static final String DISTRIBUTION_NETWORK__UUID_FUNCTION_SET = "0000ffc7-0000-1000-8000-00805f9b34fb";// 门锁功能集   单火开关项目最新版的协议才有这个服务  2020年6月4日
}
