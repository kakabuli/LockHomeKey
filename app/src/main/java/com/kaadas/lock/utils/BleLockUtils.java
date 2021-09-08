package com.kaadas.lock.utils;

import android.content.Intent;
import android.text.TextUtils;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.bean.BluetoothLockBroadcastBean;
import com.kaadas.lock.bean.BluetoothLockFunctionBean;
import com.kaadas.lock.bean.WifiLockFunctionBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BleLockUtils {

    public static final int TYPE_PASSWORD = 1;
    public static final int TYPE_FINGER = 2;
    public static final int TYPE_CARD = 3;
    public static final int TYPE_SHARE = 4;
    public static final int TYPE_MORE = 5;
    public static final int TYPE_OFFLINE_PASSWORD = 6;
    public static final int TYPE_FACE_PASSWORD = 7;
    public static final int TYPE_SMART_SWITCH = 8;
    public static final int TYPE_VIDEO = 9;
    public static final int TYPE_RECORD = 10;
    public static final int TYPE_ALBUM = 11;

    /***更新版本V 3.6(20200603) 刘静
     *
     * 1	开门	带密码开门,需要输入用户密码密码开门,不支持不带密码开门。安全模式（双重验证模式）下,APP不能开门；电子反锁状态下,APP不能开门；系统锁定模式下,APP不能开门
     * 2	获取开锁记录
     * 3	电量显示	电量只能下降,不能上升（除去更换电池的情况）。
     * 4	门锁状态显示	显示门锁状态。
     * 5	获取报警记录	获取防撬等报警。
     * 6	获取操作记录	用户添加删除密码、指纹或者卡片的操作记录
     * 7	密码管理	同步密码信息
     * 		添加密码,支持添加永久密码、时间策略、临时密码
     * 		删除密码
     * 8	指纹管理	同步指纹信息
     * 		添加指纹
     * 		删除指纹
     * 9	卡片管理	同步卡片信息
     * 		添加卡片
     * 		删除卡片
     * 10	设备共享	非主用户开门必须也是带密码开门
     * 11	参数设置	自动,手动模式状态显示,不支持手动模式设置
     * 12	自动,手动模式状态显示,支持手动模式设置,该功能为524系列锁体适用,手动模式下,电机不会反驱；自动模式下开门后5秒自动反驱
     * 13	安全模式设置
     * 14	布防模式设置
     * 15	反锁状态显示,不支持设置反锁状态
     * 16	语言设置
     * 17	静音模式设置
     * 18	修改管理密码
     * 19	设备信息获取	显示门锁型号、功能集版本、固件版本等信息
     * 20	OTA	支持门锁固件空中升级。
     * 21	删除设备	APP端删除设备,服务器清空门锁数据。
     * 22	报警信息上报	包含防撬等报警信息上报。
     * 23	获取门锁日志	日志还包含开锁记录和报警记录还有操作记录（添加删除密码、卡片或者指纹）。
     * 24	支持20组密码	工程项目专用，相对原本10组密码，额外增加10组密码
     * 25	支持20组指纹	P6把手低成本方案，只能支持20组指纹。
     * 26	人脸管理	同步人脸信息
     * 		        添加人脸
     * 		        删除人脸
     * 27	人脸低电量提示	电量低关闭人识别记录与推送提示
     * 28	人脸模组OTA	独立机制升级人脸模组（wifiOTA）
     * 29	获取操作记录
     *     （+人脸信息）	用户添加删除密码、指纹、人脸或者卡片等的操作记录
     * 30	获取门锁日志
     *     （+人脸信息）	日志还包含开锁记录和报警记录还有操作记录（添加删除密码、指纹、人脸或者卡片）。
     * 31	关锁命令	支持APP下发关锁命令
     * 32	手动/自动模式设置	支持APP下发 手动/自动 模式设置命令
     * 33	主动入网	可以通过APP输入管理员密码配网
     * 34	支持扫码配网	支持扫码配网
     * 35	常开/正常 模式查询	支持APP查询 常开/正常模式
     * 36	多国语言	支持多国语言设置，比如：英语，西班牙语，葡萄牙语，法语，中文
     * 37	指纹时间策略	支持指纹设置时间策略
     * 38	支持离线临时密码	支持离线临时密码
     * 39	单独wifi锁	默认情况下，带功能集的都是蓝牙锁，但是带39的，该锁只支持wifi功能
     * 40	wifi+蓝牙锁	该锁支持wifi+BLE功能
     * 41	访客临时密码	支持主用户通过分享临时密码给访客，访客在APP端输入该临时密码可以开门
     * 42	无临时密码	不支持设置一次性密码
     * 43	永久密码10组 10组永久密码，编号范围00~09，每组密码带一组策略，共计10组策略；
     * 44	永久密码20组	20组永久密码，编号范围00~19，每组密码带一组策略，共计20组策略；
     * 45	蓝牙智能单火开关连动	支持配置、功能的开关、功能开启后的时间设置、更换；
     * 46	节能模式	节能模式的传感器的开启或关闭状态
     * 47	感应把手开门	区分感应把手与内门按键的开门方式信息
     * 48	反锁状态设置	支持通过APP进行反锁状态设置
     * 49	BLE辅助wifi配网	支持通过BLE辅助wifi配网
     * 50   多国语言
     * 51   报警视频记录
     * 52   按门铃记录
     * 53   远程视频通话
     * 54   WiFi模组设置
     * 55   门铃消息推送
     * 56   支持250组指纹
     *  57  PIR报警视频记录
     *  58  PIR设置
     */


    public static final Map<Integer, Integer[]> FUNCTION_SET = new HashMap<>();

    static {
        FUNCTION_SET.put(0x00, new Integer[]{1, 2, 3, 10});
        FUNCTION_SET.put(0x01, new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 13, 14, 15, 16, 17, 19, 20, 21, 22});
        FUNCTION_SET.put(0x02, new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 10, 11, 13, 14, 15, 16, 17, 19, 20, 21, 22});
        FUNCTION_SET.put(0x03, new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 10, 11, 13, 14, 15, 16, 17, 19, 20, 21, 22, 23});
        FUNCTION_SET.put(0x04, new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 13, 14, 15, 16, 17, 19, 20, 21, 22, 23});
        FUNCTION_SET.put(0x05, new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 13, 14, 15, 16, 17, 19, 20, 21, 22, 23, 24});
        FUNCTION_SET.put(0x06, new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 10, 11, 13, 14, 15, 16, 17, 19, 20, 21, 22, 23, 24});        //2019年8月29日11:10:53
        FUNCTION_SET.put(0x07, new Integer[]{1, 2, 3, 4, 5, 7, 8, 10, 11, 13, 14, 15, 16, 17, 19, 20, 21, 22, 26, 27, 28, 29, 30});        //2019年8月29日11:10:53
        FUNCTION_SET.put(0x08, new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 13, 14, 15, 16, 17, 19, 20, 21, 22, 23, 38, 40});   //2019年12月30日15:16:25
        FUNCTION_SET.put(0x09, new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 13, 14, 15, 16, 17, 19, 20, 21, 22, 23, 24, 38, 40});   //2019年12月30日15:16:25
        FUNCTION_SET.put(0x0A, new Integer[]{2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 19, 20, 21, 22, 23, 38, 39,94});       //2019年12月30日15:16:25
        FUNCTION_SET.put(0x0B, new Integer[]{2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 19, 20, 21, 22, 23, 24, 38, 40});        //2019年12月30日15:16:25
        FUNCTION_SET.put(0x0C, new Integer[]{2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 15, 19, 20, 21, 22, 23, 26, 27, 29, 30, 34, 38, 39, 46, 47});
        FUNCTION_SET.put(0x0D, new Integer[]{2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 19, 20, 21, 22, 23, 24, 38, 40, 49});
        FUNCTION_SET.put(0x0E, new Integer[]{2, 3, 4, 5, 6, 7, 8, 10, 11, 15, 19, 20, 21, 22, 23, 24, 26, 27, 28, 29, 30, 33, 34, 38, 39, 46, 47});
        FUNCTION_SET.put(0x0F, new Integer[]{2, 3, 4, 5, 6, 7, 8, 10, 11, 15, 19, 20, 21, 22, 23, 24, 26, 27, 28, 29, 30, 32, 33, 34, 38, 39, 46});//2020年10月21日14:10
        FUNCTION_SET.put(0x10, new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 19, 20, 21, 22, 23, 24, 32, 36, 38, 40, 48});
        FUNCTION_SET.put(0x11, new Integer[]{2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 19, 20, 21, 22, 23, 38, 39, 56});

        FUNCTION_SET.put(0x20, new Integer[]{1, 2, 3, 4, 5, 7, 8, 10, 13, 16, 17, 19, 20, 21, 22});
        FUNCTION_SET.put(0x31, new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 10, 13, 16, 17, 19, 20, 21, 22});
        FUNCTION_SET.put(0x32, new Integer[]{1, 2, 3, 4, 5, 7, 8, 9, 10, 13, 16, 17, 19, 20, 21, 22});
        FUNCTION_SET.put(0x33, new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 13, 16, 17, 19, 20, 21, 22});
        FUNCTION_SET.put(0x34, new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 13, 16, 17, 19, 20, 21, 22});
        FUNCTION_SET.put(0x35, new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 10, 13, 16, 17, 19, 20, 21, 22, 23});
        FUNCTION_SET.put(0x36, new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 10, 13, 16, 17, 19, 20, 21, 22, 23});
        FUNCTION_SET.put(0x37, new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 13, 16, 17, 19, 20, 21, 22, 23});
        FUNCTION_SET.put(0x38, new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 10, 13, 16, 17, 19, 20, 21, 22, 23, 24});
        FUNCTION_SET.put(0x39, new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 13, 16, 17, 19, 20, 21, 22, 23, 24});
        FUNCTION_SET.put(0x40, new Integer[]{1, 2, 3, 4, 5, 6, 7, 9, 10, 13, 16, 17, 19, 20, 21, 22, 23});        //2019年8月29日11:11:10
        FUNCTION_SET.put(0x41, new Integer[]{1, 2, 3, 4, 5, 6, 7, 9, 10, 13, 16, 17, 19, 20, 21, 22, 23, 24});        //2019年9月6日08:45:45


        FUNCTION_SET.put(0x60, new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 13, 16, 17, 19, 20, 21, 22, 23, 25});
        FUNCTION_SET.put(0x61, new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 10, 13, 16, 17, 19, 20, 21, 22, 23, 25});
        FUNCTION_SET.put(0x62, new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 10, 13, 16, 17, 19, 20, 21, 22, 23, 24, 25});
        FUNCTION_SET.put(0x63, new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 13, 16, 17, 19, 20, 21, 22, 23, 24, 25});
        FUNCTION_SET.put(0x64, new Integer[]{2, 3, 4, 5, 6, 7, 8, 10, 13, 16, 17, 19, 20, 21, 22, 23, 24, 25, 38, 39});
        FUNCTION_SET.put(0x65, new Integer[]{2, 3, 4, 5, 6, 7, 8, 9, 10, 13, 16, 17, 19, 20, 21, 22, 23, 24, 25, 38, 39});
        FUNCTION_SET.put(0x66, new Integer[]{2, 3, 4, 5, 6, 7, 8, 9, 13, 16, 17, 19, 20, 21, 22, 23, 24, 25, 38, 40, 45});
        FUNCTION_SET.put(0x67, new Integer[]{2, 3, 4, 5, 6, 7, 8, 10, 13, 16, 17, 19, 20, 21, 22, 23, 38, 39});
        FUNCTION_SET.put(0x68, new Integer[]{2, 3, 4, 5, 6, 7, 8, 9, 10, 13, 16, 17, 19, 20, 21, 22, 23, 38, 39});


        FUNCTION_SET.put(0x70, new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 13, 14, 15, 16, 17, 19, 20, 21, 22, 23});        //2019年12月3日17:18:47
        FUNCTION_SET.put(0x71, new Integer[]{1, 2, 3, 4, 5, 6, 7, 9, 10, 11, 13, 14, 15, 16, 17, 19, 20, 21, 22, 23});
        FUNCTION_SET.put(0x72, new Integer[]{1, 2, 3, 4, 5, 6, 7, 10, 11, 13, 14, 15, 16, 17, 19, 20, 21, 22, 23});
        FUNCTION_SET.put(0x73, new Integer[]{1, 2, 3, 4, 5, 6, 7, 10, 11, 15, 16, 17, 19, 20, 21, 22, 23, 41, 43});        //2020年3月21日14:02:06
        FUNCTION_SET.put(0x74, new Integer[]{1, 2, 3, 4, 5, 6, 7, 9, 10, 11, 13, 15, 16, 17, 19, 20, 21, 22, 23, 41, 43});
        FUNCTION_SET.put(0x75, new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 13, 15, 16, 17, 19, 20, 21, 22, 23, 41, 43});

        FUNCTION_SET.put(0x76, new Integer[]{1, 2, 3, 4, 5, 6, 7, 10, 11, 15, 16, 17, 19, 20, 21, 22, 23, 41, 43, 50});
        FUNCTION_SET.put(0x77, new Integer[]{1, 2, 3, 4, 5, 6, 7, 9, 10, 11, 13, 15, 16, 17, 19, 20, 21, 22, 23, 41, 43, 50});
        FUNCTION_SET.put(0x78, new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 13, 15, 16, 17, 19, 20, 21, 22, 23, 41, 43, 50});
        FUNCTION_SET.put(0x79, new Integer[]{1, 2, 3, 4, 5, 6, 7, 9, 10, 13, 14, 15, 16, 17, 19, 20, 21, 22, 23, 26, 27, 28, 29, 30, 33, 34, 38, 47, 51, 52, 53, 55, 59, 64, 65, 66, 67, 86, 93});
        FUNCTION_SET.put(0x80, new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 13, 14, 15, 16, 17, 19, 20, 21, 22, 23, 26, 28, 29, 30, 33, 34, 38, 47, 51, 52, 53, 55, 59, 64, 65, 66, 67, 86, 93});

        FUNCTION_SET.put(0x7A, new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 10, 13, 14, 15, 16, 17, 19, 20, 21, 22, 23, 32, 33, 34, 38, 39, 47, 49, 51, 52, 53, 55, 57, 58, 59, 93});//2020年9月28日11:24:09
        FUNCTION_SET.put(0x7B, new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 10, 13, 14, 15, 16, 17, 19, 20, 21, 22, 23, 32, 33, 34, 37, 38, 47, 51, 52, 53, 55, 57, 58, 59, 68, 69, 70, 71, 72, 73, 74, 75, 76, 86, 87, 93});
        FUNCTION_SET.put(0x7C, new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 10, 13, 14, 15, 16, 17, 19, 21, 22, 23, 33, 34, 38, 39, 47, 51, 52, 53, 55, 57, 58, 59, 95});//2021年7月20日16:34:09 功能集表格没有93


        FUNCTION_SET.put(0xC0, new Integer[]{1, 2, 3, 4, 5, 6, 7, 10, 11, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 31, 32, 33, 36, 41, 42, 44});
        FUNCTION_SET.put(0xC1, new Integer[]{1, 2, 3, 4, 5, 6, 7, 10, 15, 16, 17, 18, 19, 20, 21, 22, 23, 31, 33, 35, 36, 41, 42, 44});
        FUNCTION_SET.put(0xC3, new Integer[]{1, 2, 3, 4, 5, 6, 7, 10, 11, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 31, 32, 33, 41, 42, 44});
        FUNCTION_SET.put(0xC4, new Integer[]{1, 2, 3, 4, 5, 6, 7, 10, 15, 16, 17, 18, 19, 20, 21, 22, 23, 31, 33, 35, 41, 42, 44});
        FUNCTION_SET.put(0xC5, new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 13, 14, 15, 16, 17, 19, 20, 21, 22, 23, 36});        //2020年3月21日14:02:06
        FUNCTION_SET.put(0xC6, new Integer[]{1, 2, 3, 4, 5, 6, 7, 9, 10, 11, 13, 14, 15, 16, 17, 19, 20, 21, 22, 23, 36});
        FUNCTION_SET.put(0xC7, new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 12, 13, 16, 17, 19, 20, 21, 22, 23, 36});
        FUNCTION_SET.put(0xC8, new Integer[]{1, 2, 3, 4, 5, 6, 7, 9, 10, 12, 13, 16, 17, 19, 20, 21, 22, 23, 36});
        FUNCTION_SET.put(0xC9, new Integer[]{1, 2, 3, 4, 5, 6, 7, 10, 12, 13, 16, 17, 19, 20, 21, 22, 23, 36});

        FUNCTION_SET.put(0xA0, new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 13, 16, 17, 19, 20, 21, 22, 23, 32, 33, 34, 38, 40, 44, 49, 50, 60, 61, 63});  //2021年1月27 X9功能集
        FUNCTION_SET.put(0xA1, new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 13, 16, 17, 19, 20, 21, 22, 23, 32, 33, 34, 38, 44, 51, 52, 53, 55, 57, 58, 59, 60, 61, 63, 64, 86, 88, 89, 90, 93, 94});
        FUNCTION_SET.put(0xA2, new Integer[]{1, 2});
        FUNCTION_SET.put(0xA3, new Integer[]{1, 2});
        FUNCTION_SET.put(0xA4, new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 13, 16, 17, 19, 20, 21, 22, 23, 32, 33, 34, 38, 44, 51, 52, 53, 55, 57, 58, 59, 60, 61, 63, 64, 68, 69, 70, 71, 72, 73, 74, 75, 76, 86, 88, 89, 90, 93, 94});
        FUNCTION_SET.put(0xA5, new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 13, 16, 19, 20, 21, 22, 23, 26, 28, 29, 30, 32, 33, 34, 38, 47, 51, 52, 53, 55, 59, 60, 61, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 86, 88, 89, 90, 91, 92, 93});

        FUNCTION_SET.put(0xA8, new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 13, 16, 19, 20, 21, 22, 23, 33, 34, 38, 44, 51, 52, 53, 55, 57, 58, 59, 88, 90, 93, 96});

        FUNCTION_SET.put(0xFF, new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 10, 13, 16, 17, 19, 20, 21, 22}); //默认为FF
    }

    /**
     * 根据功能集判断是否支持手动自动模式显示
     *
     * @param functionSet
     * @return
     */
    public static boolean isSupportAMModeShow(int functionSet) {
        Integer[] funcs = FUNCTION_SET.get(functionSet);
        if (funcs == null) {
            return false;
        }
        List<Integer> integers = Arrays.asList(funcs);
        return integers.contains(11) || integers.contains(12);
    }

    /**
     * 根据功能集判断是否支持手动自动模式设置
     *
     * @param functionSet
     * @return
     */
    public static boolean isSupportAppAMModeSet(int functionSet) {
        Integer[] funcs = FUNCTION_SET.get(functionSet);
        if (funcs == null) {
            return false;
        }
        List<Integer> integers = Arrays.asList(funcs);
        return integers.contains(32);
    }

    /**
     * 根据功能集判断是否支持手动自动模式设置
     *
     * @param functionSet
     * @return
     */
    public static boolean isSupportAMModeSet(int functionSet) {
        Integer[] funcs = FUNCTION_SET.get(functionSet);
        if (funcs == null) {
            return false;
        }
        List<Integer> integers = Arrays.asList(funcs);
        return integers.contains(32);
    }

    /**
     * 根据功能集判断是否支持节能模式显示
     *
     * @param functionSet
     * @return
     */
    public static boolean isSupportPowerSaveModeShow(int functionSet) {
        Integer[] funcs = FUNCTION_SET.get(functionSet);
        if (funcs == null) {
            return false;
        }
        List<Integer> integers = Arrays.asList(funcs);
        return integers.contains(46);
    }

    /**
     * 根据功能集判断是否支持节能模式显示
     *
     * @param functionSet
     * @return
     */
    public static boolean isSupportPirSetting(int functionSet) {
        Integer[] funcs = FUNCTION_SET.get(functionSet);
        if (funcs == null) {
            return false;
        }
        List<Integer> integers = Arrays.asList(funcs);
        return integers.contains(58) || integers.contains(57);
    }

    /**
     * 根据功能集判断是否支持面容识别功能显示
     *
     * @param functionSet
     * @return
     */
    public static boolean isSupportFaceStatusShow(int functionSet) {
        Integer[] funcs = FUNCTION_SET.get(functionSet);
        if (funcs == null) {
            return false;
        }
        List<Integer> integers = Arrays.asList(funcs);
        return integers.contains(26);
    }

    /**
     * 根据功能集判断是否支持修改管理员密码
     *
     * @param functionSet
     * @return
     */
    public static boolean isSupportModifyManagerPassword(int functionSet) {
        Integer[] funcs = FUNCTION_SET.get(functionSet);
        if (funcs == null) {
            return false;
        }
        List<Integer> integers = Arrays.asList(funcs);
        return integers.contains(18);
    }

    /**
     * 根据功能集判断是否支持实时视频设置
     *
     * @param functionSet
     * @return
     */
    public static boolean isSupportRealTimeVideo(int functionSet) {
        Integer[] funcs = FUNCTION_SET.get(functionSet);
        if (funcs == null) {
            return false;
        }
        List<Integer> integers = Arrays.asList(funcs);
        return integers.contains(54);
    }

    /**
     * 根据功能集判断主用户是否需要带密码开门
     *
     * @param functionSet
     * @return 是否需要密码开门   默认是需要的
     */
    public static boolean isNeedPwdOpen(String functionSet) {
        if (TextUtils.isEmpty(functionSet)) {
            return true;  //默认需要带密码开门
        }
        int func;
        try {
            func = Integer.parseInt(functionSet);
        } catch (NumberFormatException e) {
            return true;
        }

        Integer[] funcs = FUNCTION_SET.get(func);
        if (funcs == null) {
            return false;
        }
        List<Integer> integers = Arrays.asList(funcs);
        return integers.contains(1);
    }


    /**
     * 根据功能集判断是否支持卡片
     *
     * @param functionSet
     * @return
     */
    public static boolean isSupportCard(String functionSet) {
        if (TextUtils.isEmpty(functionSet)) {
            return false;
        }
        int func = Integer.parseInt(functionSet);
        Integer[] funcs = FUNCTION_SET.get(func);
        if (funcs == null) { //  没有该功能集对应的  知己false
            return false;
        }
        List<Integer> integers = Arrays.asList(funcs);
        return integers.contains(9);
    }

    /**
     * 根据功能集判断是否支持指纹
     *
     * @param functionSet
     * @return
     */
    public static boolean isSupportFinger(String functionSet) {
        if (TextUtils.isEmpty(functionSet)) {
            return false;
        }
        int func = Integer.parseInt(functionSet);
        Integer[] funcs = FUNCTION_SET.get(func);
        if (funcs == null) { //  没有该功能集对应的  知己false
            return false;
        }
        List<Integer> integers = Arrays.asList(funcs);
        return integers.contains(8);
    }

    /**
     * 根据功能集判断是否支持密码
     *
     * @param functionSet
     * @return
     */
    public static boolean isSupportPassword(String functionSet) {
        if (TextUtils.isEmpty(functionSet)) {
            return false;
        }
        int func = Integer.parseInt(functionSet);
        Integer[] funcs = FUNCTION_SET.get(func);
        if (funcs == null) { //  没有该功能集对应的  知己false
            return false;
        }
        List<Integer> integers = Arrays.asList(funcs);
        return integers.contains(7);
    }

    /**
     * 使用讯美P2P连接方式
     */
    public static boolean isSupportXMConnect(String functionSet){
        if(TextUtils.isEmpty(functionSet)){
            return false;
        }
        int funcSet = Integer.parseInt(functionSet);
        Integer[] funcs = FUNCTION_SET.get(funcSet);
        if (funcs == null) {
            return false;
        }
        List<Integer> integers = Arrays.asList(funcs);
        return integers.contains(59);
    }

    /**
     * 门锁方向设置显示（上报，查询）
     */
    public static boolean isSupportDoorDirection(int functionSet){
        Integer[] funcs = FUNCTION_SET.get(functionSet);
        if (funcs == null) {
            return false;
        }
        List<Integer> integers = Arrays.asList(funcs);
        return integers.contains(60);
    }

    /**
     * 开门力量设置显示（上报，查询）
     */
    public static boolean isSupportOpenDoorPower(int functionSet){
        Integer[] funcs = FUNCTION_SET.get(functionSet);
        if (funcs == null) {
            return false;
        }
        List<Integer> integers = Arrays.asList(funcs);
        return integers.contains(61);
    }

    /**
     * 上锁方式设置显示（上报，查询）
     */
    public static boolean isSupportLockType(int functionSet){
        Integer[] funcs = FUNCTION_SET.get(functionSet);
        if (funcs == null) {
            return false;
        }
        List<Integer> integers = Arrays.asList(funcs);
        return integers.contains(62);
    }

    /**
     * 显示屏亮度
     */
    public static boolean isSupportScreenBrightness(String functionSet){
        if(TextUtils.isEmpty(functionSet)){
            return false;
        }
        int funcSet = Integer.parseInt(functionSet);
        Integer[] funcs = FUNCTION_SET.get(funcSet);
        if (funcs == null) {
            return false;
        }
        List<Integer> integers = Arrays.asList(funcs);
        return integers.contains(73) || integers.contains(72) || integers.contains(71);
    }

    /**
     * 显示屏时长
     */
    public static boolean isSupportScreenDuration(String functionSet){
        if(TextUtils.isEmpty(functionSet)){
            return false;
        }
        int funcSet = Integer.parseInt(functionSet);
        Integer[] funcs = FUNCTION_SET.get(funcSet);
        if (funcs == null) {
            return false;
        }
        List<Integer> integers = Arrays.asList(funcs);
        return integers.contains(74) || integers.contains(75) || integers.contains(76);
    }

    /**
     * 静音设置
     */
    public static boolean isSupportSilentMode(String functionSet){
        if(TextUtils.isEmpty(functionSet)){
            return false;
        }
        int funcSet = Integer.parseInt(functionSet);
        Integer[] funcs = FUNCTION_SET.get(funcSet);
        if (funcs == null) {
            return false;
        }
        List<Integer> integers = Arrays.asList(funcs);
        return integers.contains(17);
    }

    /**
     * 语音设置
     */
    public static boolean isSupportVoiceQuality(String functionSet){
        if(TextUtils.isEmpty(functionSet)){
            return false;
        }
        int funcSet = Integer.parseInt(functionSet);
        Integer[] funcs = FUNCTION_SET.get(funcSet);
        if (funcs == null) {
            return false;
        }
        List<Integer> integers = Arrays.asList(funcs);
        return integers.contains(88);
    }

    /**
     * 语音模组
     */
    public static boolean isSupportVoiceModel(String functionSet){
        if(TextUtils.isEmpty(functionSet)){
            return false;
        }
        int funcSet = Integer.parseInt(functionSet);
        Integer[] funcs = FUNCTION_SET.get(funcSet);
        if (funcs == null) {
            return false;
        }
        List<Integer> integers = Arrays.asList(funcs);
        return integers.contains(89);
    }

    /**
     * App设置胁迫报警
     */
    public static boolean isSupportDuressAlarm(String functionSet){
        if(TextUtils.isEmpty(functionSet)){
            return false;
        }
        int funcSet = Integer.parseInt(functionSet);
        Integer[] funcs = FUNCTION_SET.get(funcSet);
        if (funcs == null) {
            return false;
        }
        List<Integer> integers = Arrays.asList(funcs);
        return integers.contains(90);
    }

    /**
     * 感应把手功能开关显示
     */
    public static boolean isSupportSensingHandleSwitch(String functionSet){
        if(TextUtils.isEmpty(functionSet)){
            return false;
        }
        int funcSet = Integer.parseInt(functionSet);
        Integer[] funcs = FUNCTION_SET.get(funcSet);
        if (funcs == null) {
            return false;
        }
        List<Integer> integers = Arrays.asList(funcs);
        return integers.contains(91);
    }

    /**
     * 人脸识别开关显示
     */
    public static boolean isSupportFacereCognitionSwitch(String functionSet){
        if(TextUtils.isEmpty(functionSet)){
            return false;
        }
        int funcSet = Integer.parseInt(functionSet);
        Integer[] funcs = FUNCTION_SET.get(funcSet);
        if (funcs == null) {
            return false;
        }
        List<Integer> integers = Arrays.asList(funcs);
        return integers.contains(92);
    }

    /**
     * 视频模式开关显示
     */
    public static boolean isSupportVideoModeSwitch(String functionSet){
        if(TextUtils.isEmpty(functionSet)){
            return false;
        }
        int funcSet = Integer.parseInt(functionSet);
        Integer[] funcs = FUNCTION_SET.get(funcSet);
        if (funcs == null) {
            return false;
        }
        List<Integer> integers = Arrays.asList(funcs);
        return integers.contains(93);
    }

    /**
     * 人脸模组开关
     */
    public static boolean isSupportFaceModelSwitch(String functionSet){
        if(TextUtils.isEmpty(functionSet)){
            return false;
        }
        int funcSet = Integer.parseInt(functionSet);
        Integer[] funcs = FUNCTION_SET.get(funcSet);
        if (funcs == null) {
            return false;
        }
        List<Integer> integers = Arrays.asList(funcs);
        return integers.contains(77) || integers.contains(78) || integers.contains(79);
    }

    /**
     * AMS传感器
     */
    public static boolean isSupportAMSSensor(String functionSet){
        if(TextUtils.isEmpty(functionSet)){
            return false;
        }
        int funcSet = Integer.parseInt(functionSet);
        Integer[] funcs = FUNCTION_SET.get(funcSet);
        if (funcs == null) {
            return false;
        }
        List<Integer> integers = Arrays.asList(funcs);
        return integers.contains(80) || integers.contains(81) || integers.contains(82);
    }

    /**
     * 根据功能集判断  授权用户 是否需要带密码开门
     *
     * @param functionSet
     * @return 是否需要密码开门   默认是需要的
     */
    public static boolean authUserNeedPwdOpen(String functionSet) {
        if (TextUtils.isEmpty(functionSet)) {
            return true;  //默认需要带密码开门
        }
        int func;
        try {
            func = Integer.parseInt(functionSet);
        } catch (NumberFormatException e) {
            return true;
        }

        Integer[] funcs = FUNCTION_SET.get(func);
        if (funcs == null) {
            return false;
        }
        List<Integer> integers = Arrays.asList(funcs);
        return integers.contains(10);
    }


    /**
     * 根据功能集判断是否支持操作记录
     *
     * @param functionSet
     * @return
     */
    public static boolean isSupportOperationRecord(int functionSet) {
        Integer[] funcs = FUNCTION_SET.get(functionSet);
        if (funcs == null) {
            return false;
        }
        List<Integer> integers = Arrays.asList(funcs);
        return integers.contains(23);
    }


    /**
     * 根据功能集判断是否支持操作记录
     *
     * @param functionSet
     * @return
     */
    public static boolean isSupport20Passwords(String functionSet) {
        if(TextUtils.isEmpty(functionSet)){
            return false;
        }
        int funcSet = Integer.parseInt(functionSet);
        Integer[] funcs = FUNCTION_SET.get(funcSet);
        if (funcs == null) {
            return false;
        }
        List<Integer> integers = Arrays.asList(funcs);
        return integers.contains(24);
    }

    /**
     * 根据功能集判断是否只支持20个指纹
     *
     * @param functionSet
     * @return
     */
    public static boolean isOnlySupport20Fingers(String functionSet) {
        if(TextUtils.isEmpty(functionSet)){
            return false;
        }
        int funcSet = Integer.parseInt(functionSet);
        Integer[] funcs = FUNCTION_SET.get(funcSet);
        if (funcs == null) {
            return false;
        }
        List<Integer> integers = Arrays.asList(funcs);
        return integers.contains(25);
    }


    public static boolean isExistFunctionSet(int functionSet) {
        //获取改功能集是否存在
        return !(FUNCTION_SET.get(functionSet) == null);

    }

    public static boolean isSupportFace(String functionSet) {
        if(TextUtils.isEmpty(functionSet)){
            return false;
        }
        int funcSet = Integer.parseInt(functionSet);
        Integer[] funcs = FUNCTION_SET.get(funcSet);
        if (funcs == null) {
            return false;
        }
        List<Integer> integers = Arrays.asList(funcs);
        return integers.contains(26);
    }

    public static boolean isSupportFacePir(String functionSet) {
        if(TextUtils.isEmpty(functionSet)){
            return false;
        }
        int funcSet = Integer.parseInt(functionSet);
        Integer[] funcs = FUNCTION_SET.get(funcSet);
        if (funcs == null) {
            return false;
        }
        List<Integer> integers = Arrays.asList(funcs);
        return integers.contains(65) || integers.contains(66) || integers.contains(67);
    }

    public static boolean isSupportPanelMultiOTA(String functionSet){
        if(TextUtils.isEmpty(functionSet)){
            return false;
        }
        int funcSet = Integer.parseInt(functionSet);
        Integer[] funcs = FUNCTION_SET.get(funcSet);
        if (funcs == null) {
            return false;
        }
        List<Integer> integers = Arrays.asList(funcs);
        return integers.contains(63);
    }

    public static boolean isSupportVideoPanelMultiOTA(String functionSet){
        if(TextUtils.isEmpty(functionSet)){
            return false;
        }
        int funcSet = Integer.parseInt(functionSet);
        Integer[] funcs = FUNCTION_SET.get(funcSet);
        if (funcs == null) {
            return false;
        }
        List<Integer> integers = Arrays.asList(funcs);
        return integers.contains(87);
    }

    public static boolean isSupportLockOTA(String functionSet){
        if(TextUtils.isEmpty(functionSet)){
            return false;
        }
        int funcSet = Integer.parseInt(functionSet);
        Integer[] funcs = FUNCTION_SET.get(funcSet);
        if (funcs == null) {
            return false;
        }
        List<Integer> integers = Arrays.asList(funcs);
        return integers.contains(20);
    }

    public static boolean isSupportSinglePanelOTA(String functionSet){
        if(TextUtils.isEmpty(functionSet)){
            return false;
        }
        int funcSet = Integer.parseInt(functionSet);
        Integer[] funcs = FUNCTION_SET.get(funcSet);
        if (funcs == null) {
            return false;
        }
        List<Integer> integers = Arrays.asList(funcs);
        return integers.contains(86);
    }

    public static boolean isSupportFrontPanelOnlyShow(String functionSet){
        if(TextUtils.isEmpty(functionSet)){
            return false;
        }
        int funcSet = Integer.parseInt(functionSet);
        Integer[] funcs = FUNCTION_SET.get(funcSet);
        if (funcs == null) {
            return false;
        }
        List<Integer> integers = Arrays.asList(funcs);
        return integers.contains(95);
    }

    public static boolean isSupportFrontPanelShowAndOTA(String functionSet){
        if(TextUtils.isEmpty(functionSet)){
            return false;
        }
        int funcSet = Integer.parseInt(functionSet);
        Integer[] funcs = FUNCTION_SET.get(funcSet);
        if (funcs == null) {
            return false;
        }
        List<Integer> integers = Arrays.asList(funcs);
        return integers.contains(96);
    }

    public static boolean isSupportAMIntroduce(int functionSet) {
        Integer[] funcs = FUNCTION_SET.get(functionSet);
        if (funcs == null) {
            return false;
        }
        List<Integer> integers = Arrays.asList(funcs);
        return integers.contains(26);
    }
    /**
     * 根据功能集判断显示蓝牙锁的界面
     *
     * @param functionSet
     * @return
     */
    public static List<BluetoothLockFunctionBean> getSupportFunction(int functionSet) {
        List<BluetoothLockFunctionBean> functionBeans = new ArrayList<>();
        Integer[] funcs = FUNCTION_SET.get(functionSet);
        System.out.println(funcs == null);
        if (funcs == null) { //未知功能集
            funcs = FUNCTION_SET.get(0x31);
        }
        List<Integer> integers = Arrays.asList(funcs);
        LogUtils.e("获取到的  功能集是1   " + functionSet);
        LogUtils.e("获取到的  功能集是2   " + Arrays.toString(funcs));
        LogUtils.e("获取到的  功能集是3否包含卡片      " + integers.contains(9));
        if (integers.contains(7)) {
            functionBeans.add(new BluetoothLockFunctionBean(MyApplication.getInstance().getString(R.string.password), R.mipmap.bluetooth_password, TYPE_PASSWORD));
        }
        if (integers.contains(8)) {
            functionBeans.add(new BluetoothLockFunctionBean(MyApplication.getInstance().getString(R.string.fingerprint), R.mipmap.bluetooth_fingerprint, TYPE_FINGER));
        }
        if (integers.contains(9)) {
            functionBeans.add(new BluetoothLockFunctionBean(MyApplication.getInstance().getString(R.string.card), R.mipmap.bluetooth_card, TYPE_CARD));
        }
        functionBeans.add(new BluetoothLockFunctionBean(MyApplication.getInstance().getString(R.string.device_share), R.mipmap.wifi_lock_share, TYPE_SHARE));
        functionBeans.add(new BluetoothLockFunctionBean(MyApplication.getInstance().getString(R.string.more), R.mipmap.bluetooth_more, TYPE_MORE));

        return functionBeans;
    }

    /**
     * 根据功能集判断显示WI-FI锁的界面
     *
     * @param functionSet
     * @return
     */
    public static List<WifiLockFunctionBean> getWifiLockSupportFunction(int functionSet) {
        List<WifiLockFunctionBean> functionBeans = new ArrayList<>();
        Integer[] funcs = FUNCTION_SET.get(functionSet);
        System.out.println(funcs == null);
        if (funcs == null) { //未知功能集
            funcs = FUNCTION_SET.get(0x31);
        }
        List<Integer> integers = Arrays.asList(funcs);
        LogUtils.e("获取到的  功能集是1   " + functionSet);
        LogUtils.e("获取到的  功能集是2   " + Arrays.toString(funcs));
        LogUtils.e("获取到的  功能集是3否包含卡片      " + integers.contains(9));
        functionBeans.add(new WifiLockFunctionBean(MyApplication.getInstance().getString(R.string.offline_password), R.mipmap.bluetooth_password, TYPE_OFFLINE_PASSWORD));
        if(integers.contains(53)){
            functionBeans.add(new WifiLockFunctionBean(MyApplication.getInstance().getString(R.string.real_time_video_function),R.mipmap.wifi_lock_video,TYPE_VIDEO));
            functionBeans.add(new WifiLockFunctionBean(MyApplication.getInstance().getString(R.string.dynamic_recording),R.mipmap.wifi_lock_recording,TYPE_RECORD));
        }
        if (integers.contains(26)) {
            functionBeans.add(new WifiLockFunctionBean(MyApplication.getInstance().getString(R.string.face_password), R.mipmap.face_password, TYPE_FACE_PASSWORD));
        }
        if (integers.contains(7)) {
            functionBeans.add(new WifiLockFunctionBean(MyApplication.getInstance().getString(R.string.password), R.mipmap.bluetooth_password, TYPE_PASSWORD));
        }

        if (integers.contains(8)) {
            functionBeans.add(new WifiLockFunctionBean(MyApplication.getInstance().getString(R.string.fingerprint), R.mipmap.bluetooth_fingerprint, TYPE_FINGER));
        }
        // TODO: 2021/5/16 根据K10FV的功能集去掉53（远程视频通话） 判断
        //if (!integers.contains(53) && integers.contains(9)) {
        if (integers.contains(9)) {
            functionBeans.add(new WifiLockFunctionBean(MyApplication.getInstance().getString(R.string.card), R.mipmap.bluetooth_card, TYPE_CARD));
        }
        if(integers.contains(53)){
            functionBeans.add(new WifiLockFunctionBean(MyApplication.getInstance().getString(R.string.my_album),R.mipmap.wifi_lock_album,TYPE_ALBUM));
        }
        if (!integers.contains(53) && integers.contains(45)) {
            functionBeans.add(new WifiLockFunctionBean(MyApplication.getInstance().getString(R.string.smart_switch), R.mipmap.single_switch_smart_icon, TYPE_SMART_SWITCH));
        }

        functionBeans.add(new WifiLockFunctionBean(MyApplication.getInstance().getString(R.string.device_share), R.mipmap.wifi_lock_share, TYPE_SHARE));
        functionBeans.add(new WifiLockFunctionBean(MyApplication.getInstance().getString(R.string.more), R.mipmap.bluetooth_more, TYPE_MORE));

        return functionBeans;
    }


    /**
     * 根据设备型号,获取授权界面的显示图片, 授权用户，大图
     *
     * @param model
     * @return
     */
    public static int getAuthorizationImageByModel(String model) {

        if (!TextUtils.isEmpty(model)) {

            if (model.startsWith("K7")) {
                return R.mipmap.bluetooth_authorization_lock_k7;
            }

            else if (model.startsWith("S118") ) {
                return R.mipmap.bluetooth_authorization_lock_s118;
            }
            else if (model.startsWith("K10W") ) {
                return R.mipmap.bluetooth_authorization_lock_k10w;
            }
            else if (model.startsWith("S300C") ) {
                return R.mipmap.bluetooth_authorization_lock_s300c;
            }
            else if (model.startsWith("X6") ) {
                return R.mipmap.bluetooth_authorization_lock_x6;
            } else if (model.startsWith("K7P") ) {
                return R.mipmap.bluetooth_authorization_lock_k7p;
            }



            else if (model.startsWith("KX-T") || model.startsWith("K8300")) {
                return R.mipmap.bluetooth_authorization_lock_kx_t;
            } else if (model.startsWith("S110")) {
                return R.mipmap.bluetooth_authorization_lock_s110;
            } else if (model.startsWith("F1") || model.startsWith("D5601")) {
                return R.mipmap.bluetooth_authorization_lock_f1;
            } else if (model.startsWith("K12W")) {
                return R.mipmap.bluetooth_authorization_lock_k12w;
            } else if (model.contentEquals("K13")) {
                return R.mipmap.bluetooth_authorization_lock_k13;
            } else if (model.startsWith("K9W")) {
                return R.mipmap.bluetooth_authorization_lock_k9w;
            } else if (model.startsWith("K11W")) {
                return R.mipmap.bluetooth_authorization_lock_k11w;
            } else if (model.startsWith("X1")) {
                return R.mipmap.bluetooth_authorization_lock_x1;
            } else if (model.startsWith("K8-T")) {
                return R.mipmap.bluetooth_authorization_lock_k8_t;
            } else if (model.startsWith("S800")) {
                return R.mipmap.bluetooth_authorization_lock_s800;
            } else if (model.startsWith("K8")) {
                return R.mipmap.bluetooth_authorization_lock_k8;
            } else if (model.startsWith("Q8")) {
                return R.mipmap.bluetooth_authorization_lock_q8;
            } else if (model.startsWith("8012") || model.startsWith("G8012")) {
                return R.mipmap.bluetooth_authorization_lock_8012;
            } else if (model.startsWith("K9")) {
                return R.mipmap.bluetooth_authorization_lock_k9;
            } else if (model.startsWith("KX")) {
                return R.mipmap.bluetooth_authorization_lock_kx;
            } else if (model.startsWith("S8C")) {
                return R.mipmap.bluetooth_authorization_lock_s8;
            } else if (model.contains("V6") || model.contains("V350")) {
                return R.mipmap.bluetooth_authorization_lock_v6;
            } else if (model.startsWith("V7") || model.startsWith("S100")) {
                return R.mipmap.bluetooth_authorization_lock_v7;
            } else if (model.startsWith("S8")) {
                return R.mipmap.bluetooth_authorization_lock_s8;
            } else if (model.startsWith("QZ013")) {
                return R.mipmap.bluetooth_authorization_lock_qz013;
            } else if (model.startsWith("QZ012")) {
                return R.mipmap.bluetooth_authorization_lock_qz012;
            } else if (model.startsWith("S6")) {
                return R.mipmap.bluetooth_authorization_lock_s6;
            } else if (model.contains("K100") || model.contains("V450")) {
                return R.mipmap.bluetooth_authorization_lock_k100;
            } else if (model.startsWith("H5606") || model.startsWith("S700")) {
                return R.mipmap.bluetooth_authorization_lock_h5606;
            } else if (model.startsWith("8008")) {
                return R.mipmap.bluetooth_authorization_lock_8008;
            } else if (model.startsWith("8100B")) {
                return R.mipmap.bluetooth_authorization_lock_8100_a1p;
            } else if (model.startsWith("8100C")) {
                return R.mipmap.bluetooth_authorization_lock_8100_a6;
            } else if (model.startsWith("8100")) {
                return R.mipmap.bluetooth_authorization_lock_8100;
            } else if (model.startsWith("K200")) {
                return R.mipmap.bluetooth_authorization_lock_k200;
            } else if (model.startsWith("S3001")) {
                return R.mipmap.bluetooth_authorization_lock_s3001;
            } else if (model.startsWith("S3006")) {
                return R.mipmap.bluetooth_authorization_lock_s3006;
            } else if (model.startsWith("S3007")) {
                return R.mipmap.bluetooth_authorization_lock_s3007;
            } else if (model.startsWith("S300")) {
                return R.mipmap.bluetooth_authorization_lock_s300;
            } else if (model.startsWith("5011")) {
                return R.mipmap.bluetooth_authorization_lock_5011;
            } else if (model.startsWith("G3350")) {
                return R.mipmap.bluetooth_authorization_lock_g3350;
            } else if (model.startsWith("G3560")) {
                return R.mipmap.bluetooth_authorization_lock_g3560;
            } else if (model.startsWith("6113")||model.startsWith("G5361")) {
                return R.mipmap.bluetooth_authorization_lock_6113;
            } else if (model.startsWith("A8")) {
                return R.mipmap.bluetooth_authorization_lock_a8;
            } else if (model.contentEquals("K13F")) {
                return R.mipmap.bluetooth_authorization_lock_k13f;
            }
            else if (model.contentEquals("A5601")) {
                return R.mipmap.bluetooth_authorization_lock_a7_w;
            }
            else if (model.startsWith("K5501")) {
                return R.mipmap.bluetooth_authorization_lock_k5501;
            }
            else if (model.contentEquals("A5602")) {
                return R.mipmap.bluetooth_authorization_lock_a8_w;
            }
            else if (model.contentEquals("A5603")) {
                return R.mipmap.bluetooth_authorization_lock_k100_w;
            }
            else if (model.contentEquals("G5601")) {
                return R.mipmap.bluetooth_authorization_lock_5500_a5p_w;
            }
            else if (model.contentEquals("G5602")) {
                return R.mipmap.bluetooth_authorization_lock_5310_a5p_w;
            }
            else if (model.startsWith("G8501")) {
                return R.mipmap.bluetooth_authorization_lock_k9_a5p_w;
            }
            else if (model.contentEquals("K10F")) {
                return R.mipmap.bluetooth_authorization_lock_k10_f;
            }
            else {
                return R.mipmap.bluetooth_authorization_lock_default;
            }
        } else {
            return R.mipmap.bluetooth_authorization_lock_default;
        }
    }


    /**
     * 根据设备型号,获取设备列表显示的图片，小图
     *
     * @param model
     * @return
     */
    public static int getSmallImageByModel(String model) {

        if (!TextUtils.isEmpty(model)) {
            if (model.startsWith("K7")) {
                return R.mipmap.k7;
            }
            else if (model.startsWith("S118") ) {
                return R.mipmap.s118;
            }  else if (model.startsWith("K10W") ) {
                return R.mipmap.k10w;
            }  else if (model.startsWith("S300C") ) {
                return R.mipmap.s300c;
            }  else if (model.startsWith("X6") ) {
                return R.mipmap.x6;
            } else if (model.startsWith("K7P") ) {
                return R.mipmap.k7p;
            }
            else if (model.startsWith("KX-T") || model.startsWith("K8300")) {
                return R.mipmap.kx_t;
            } else if (model.startsWith("S110")) {
                return R.mipmap.small_s110;
            } else if (model.startsWith("F1") || model.startsWith("D5601")) {
                return R.mipmap.small_f1;
            } else if (model.startsWith("K12W")) {
                return R.mipmap.small_k12w;
            } else if (model.contentEquals("K13")) {
                return R.mipmap.small_k13;
            } else if (model.startsWith("K9W")) {
                return R.mipmap.small_k9w;
            } else if (model.startsWith("K11W")) {
                return R.mipmap.small_k11w;
            } else if (model.startsWith("X1")) {
                return R.mipmap.small_x1;
            } else if (model.startsWith("K8-T")) {
                return R.mipmap.k8_t;
            } else if (model.startsWith("S800")) {
                return R.mipmap.s800;
            } else if (model.startsWith("K8")) {
                return R.mipmap.k8;
            } else if (model.startsWith("Q8")) {
                return R.mipmap.q8;
            } else if (model.startsWith("8012") || model.startsWith("G8012")) {
                return R.mipmap.small_8012;
            } else if (model.startsWith("K9")) {
                return R.mipmap.k9;
            } else if (model.startsWith("KX")) {
                return R.mipmap.kx;
            } else if (model.startsWith("S8C")) {
                return R.mipmap.s8;
            } else if (model.startsWith("V6") || model.startsWith("V350")) {
                return R.mipmap.v6;
            } else if (model.startsWith("V7") || model.startsWith("S100")) {
                return R.mipmap.v7;
            } else if (model.startsWith("S8")) {
                return R.mipmap.s8;
            } else if (model.startsWith("QZ012")) {
                return R.mipmap.qz012;
            } else if (model.startsWith("QZ013")) {
                return R.mipmap.qz013;
            } else if (model.startsWith("S6")) {
                return R.mipmap.s6;
            } else if (model.startsWith("K100") || model.startsWith("V450")) {
                return R.mipmap.k100;
            } else if (model.startsWith("H5606") || model.startsWith("S700")) {
                return R.mipmap.h5606;
            } else if (model.startsWith("8008")) {
                return R.mipmap.small_8008;
            } else if (model.startsWith("8100B")) {
                return R.mipmap.small_8100_a1p;
            } else if (model.startsWith("8100C")) {
                return R.mipmap.small_8100_a6;
            } else if (model.startsWith("8100")) {
                return R.mipmap.small_8100;
            } else if (model.startsWith("K200")) {
                return R.mipmap.k200;
            } else if (model.startsWith("S3001")) {
                return R.mipmap.small_s3001;
            } else if (model.startsWith("S3006")) {
                return R.mipmap.small_s3006;
            } else if (model.startsWith("S3007")) {
                return R.mipmap.small_s3007;
            } else if (model.startsWith("S300")) {
                return R.mipmap.s300;
            } else if (model.startsWith("5011")) {
                return R.mipmap.small_5011;
            } else if (model.startsWith("G3350")) {
                return R.mipmap.small_g3350;
            } else if (model.startsWith("G3560")) {
                return R.mipmap.small_g3560;
            } else if (model.startsWith("6113")||model.startsWith("G5361")) {
                return R.mipmap.small_6113;
            } else if (model.startsWith("A8")) {
                return R.mipmap.small_a8;
            } else if (model.contentEquals("K13F")) {
                return R.mipmap.small_k13f;
            }
            else if (model.contentEquals("A5601")) {
                return R.mipmap.small_a7_w;
            }
            else if (model.startsWith("K5501")) {
                return R.mipmap.small_k5501;
            }
            else if (model.contentEquals("A5602")) {
                return R.mipmap.small_a8_w;
            }
            else if (model.contentEquals("A5603")) {
                return R.mipmap.small_k100_w;
            }
            else if (model.contentEquals("G5601")) {
                return R.mipmap.small_5500_a5p_w;
            }
            else if (model.contentEquals("G5602")) {
                return R.mipmap.small_5310_a5p_w;
            }
            else if (model.startsWith("G8501")) {
                return R.mipmap.small_k9_a5p_w;
            }
            else if (model.contentEquals("K10F")) {
                return R.mipmap.small_k10_f;
            }
            else if(model.contentEquals("K10V") || model.contentEquals("K20V")){
                return R.mipmap.small_k20_v;
            }
            else {
                return R.mipmap.default_zigbee_lock_icon;
            }
        } else {
            return R.mipmap.default_zigbee_lock_icon;
        }
    }


    /**
     * 根据设备型号,获取设备详情界面显示的图片
     *
     * @param model   , 圈圈
     * @return
     */
    public static int getDetailImageByModel(String model) {

        if (!TextUtils.isEmpty(model)) {
            if (model.startsWith("K7")) {
                return R.mipmap.bluetooth_lock_k7;
            }

            else if (model.startsWith("S118") ) {
                return R.mipmap.bluetooth_lock_s118;
            }
            else if (model.startsWith("K10W") ) {
                return R.mipmap.bluetooth_lock_k10w;
            }else if (model.startsWith("S300C") ) {
                return R.mipmap.bluetooth_lock_s300c;
            } else if (model.startsWith("X6") ) {
                return R.mipmap.bluetooth_lock_x6;
            } else if (model.startsWith("K7P") ) {
                return R.mipmap.bluetooth_lock_k7p;
            }


            else if (model.startsWith("KX-T") || model.startsWith("K8300")) {
                return R.mipmap.bluetooth_lock_kx_t;
            } else if (model.startsWith("S110")) {
                return R.mipmap.bluetooth_lock_s110;
            } else if (model.startsWith("F1") || model.startsWith("D5601")) {
                return R.mipmap.bluetooth_lock_f1;
            } else if (model.startsWith("K12W")) {
                return R.mipmap.bluetooth_lock_k12w;
            } else if (model.contentEquals("K13")) {
                return R.mipmap.bluetooth_lock_k13;
            } else if (model.startsWith("K9W")) {
                return R.mipmap.bluetooth_lock_k9w;
            } else if (model.startsWith("K11W")) {
                return R.mipmap.bluetooth_lock_k11w;
            } else if (model.startsWith("X1")) {
                return R.mipmap.bluetooth_lock_x1;
            } else if (model.startsWith("K8-T")) {
                return R.mipmap.bluetooth_lock_k8_t;
            } else if (model.startsWith("S800")) {
                return R.mipmap.bluetooth_lock_s800;
            } else if (model.startsWith("K8")) {
                return R.mipmap.bluetooth_lock_k8;
            } else if (model.startsWith("Q8")) {
                return R.mipmap.bluetooth_lock_q8;
            } else if (model.startsWith("8012") || model.startsWith("G8012")) {
                return R.mipmap.bluetooth_lock_8012;
            } else if (model.startsWith("K9")) {
                return R.mipmap.bluetooth_lock_k9;
            } else if (model.startsWith("KX")) {
                return R.mipmap.bluetooth_lock_kx;
            } else if (model.startsWith("S8C")) {
                return R.mipmap.bluetooth_lock_s8;
            } else if (model.startsWith("V6") || model.startsWith("V350")) {
                return R.mipmap.bluetooth_lock_v6;
            } else if (model.startsWith("V7") || model.startsWith("S100")) {
                return R.mipmap.bluetooth_lock_v7;
            } else if (model.startsWith("S8")) {
                return R.mipmap.bluetooth_lock_s8;
            } else if (model.startsWith("QZ013")) {
                return R.mipmap.bluetooth_lock_qz013;
            } else if (model.startsWith("QZ012")) {
                return R.mipmap.bluetooth_lock_qz012;
            } else if (model.startsWith("K100") || model.startsWith("V450")) {
                return R.mipmap.bluetooth_lock_k100;
            } else if (model.startsWith("S6")) {
                return R.mipmap.bluetooth_lock_s6;
            } else if (model.startsWith("H5606") || model.startsWith("S700")) {
                return R.mipmap.bluetooth_lock_h5606;
            } else if (model.startsWith("8008")) {
                return R.mipmap.bluetooth_lock_8008;
            } else if (model.startsWith("8100B")) {
                return R.mipmap.bluetooth_lock_8100_a1p;
            } else if (model.startsWith("8100C")) {
                return R.mipmap.bluetooth_lock_8100_a6;
            } else if (model.startsWith("8100")) {
                return R.mipmap.bluetooth_lock_8100;
            } else if (model.startsWith("K200")) {
                return R.mipmap.bluetooth_lock_k200;
            } else if (model.startsWith("S3001")) {
                return R.mipmap.bluetooth_lock_s3001;
            } else if (model.startsWith("S3006")) {
                return R.mipmap.bluetooth_lock_s3006;
            } else if (model.startsWith("S3007")) {
                return R.mipmap.bluetooth_lock_s3007;
            } else if (model.startsWith("S300")) {
                return R.mipmap.bluetooth_lock_s300;
            } else if (model.startsWith("5011")) {
                return R.mipmap.bluetooth_lock_5011;
            } else if (model.startsWith("G3350")) {
                return R.mipmap.bluetooth_lock_g3350;
            } else if (model.startsWith("G3560")) {
                return R.mipmap.bluetooth_lock_g3560;
            } else if (model.startsWith("6113")||model.startsWith("G5361")) {
                return R.mipmap.bluetooth_lock_6113;
            } else if (model.startsWith("A8")) {
                return R.mipmap.bluetooth_lock_a8;
            } else if (model.contentEquals("K13F")) {
                return R.mipmap.bluetooth_lock_k13f;
            } else if (model.contentEquals("A5601")) {
                return R.mipmap.bluetooth_lock_a7_w;
            }
            else if (model.startsWith("K5501")) {
                return R.mipmap.bluetooth_lock_k5501;
            }
             else if (model.contentEquals("A5602")) {
                return R.mipmap.bluetooth_lock_a8_w;
            }
             else if (model.contentEquals("A5603")) {
                return R.mipmap.bluetooth_lock_k100_w;
            }
            else if (model.contentEquals("G5601")) {
                return R.mipmap.bluetooth_lock_5500_a5p_w;
            }
            else if (model.contentEquals("G5602")) {
                return R.mipmap.bluetooth_lock_5310_a5p_w;
            }
            else if (model.startsWith("G8501")) {
                return R.mipmap.bluetooth_lock_k9_a5p_w;
            }
            else if (model.contentEquals("K10F")) {
                return R.mipmap.bluetooth_lock_k10_f;
            }
            else {
                return R.mipmap.bluetooth_lock_default;
            }
        } else {
            return R.mipmap.bluetooth_lock_default;
        }
    }

    /**
     * 根据功能集判断是否显示人脸模组OTA
     *
     * @param functionSet
     * @return
     */
    public static boolean isSupportWiFiFaceOTA(String functionSet) {
        if(TextUtils.isEmpty(functionSet)){
            return false;
        }
        Integer[] funcs = FUNCTION_SET.get(Integer.parseInt(functionSet));
        if (funcs == null) {
            return false;
        }
        List<Integer> integers = Arrays.asList(funcs);
        return integers.contains(28);
    }

}
