package com.kaadas.lock.utils;

import android.text.TextUtils;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.bean.BluetoothLockFunctionBean;

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

    /***
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
     */

    public static final Map<Integer, Integer[]> FUNCTION_SET = new HashMap<>();

    static {
        FUNCTION_SET.put(0x00, new Integer[]{1, 2, 3, 10});
        FUNCTION_SET.put(0x01, new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22});
        FUNCTION_SET.put(0x02, new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 10, 11, 13, 14, 15, 16, 17, 19, 20, 21, 22});
        FUNCTION_SET.put(0x03, new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 10, 11, 13, 14, 15, 16, 17, 19, 20, 21, 22, 23});
        FUNCTION_SET.put(0x04  , new Integer[]{1,2,3,4,5,6,7,8,9,10,11,13,14,15,16,17,18,19,20,21,22,23});
        FUNCTION_SET.put(0x20, new Integer[]{1, 2, 3, 4, 5, 7, 8, 10, 13, 16, 17, 19, 20, 21, 22});
        FUNCTION_SET.put(0x31, new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 10, 13, 16, 17, 19, 20, 21, 22});
        FUNCTION_SET.put(0x32, new Integer[]{1, 2, 3, 4, 5, 7, 8, 9, 10, 13, 16, 17, 19, 20, 21, 22});
        FUNCTION_SET.put(0x33, new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 13, 16, 17, 19, 20, 21, 22});
        FUNCTION_SET.put(0x34, new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 12, 13, 16, 17, 19, 20, 21, 22});
        FUNCTION_SET.put(0x35, new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 10, 12, 13, 16, 17, 19, 20, 21, 22, 23});
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
    public static boolean isSupportAMModeSet(int functionSet) {
        Integer[] funcs = FUNCTION_SET.get(functionSet);
        if (funcs == null) {
            return false;
        }
        List<Integer> integers = Arrays.asList(funcs);
        return integers.contains(12);
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


    public static boolean isExistFunctionSet(int functionSet) {
        //获取改功能集是否存在
        return !(FUNCTION_SET.get(functionSet) == null);

    }

    /**
     * 根据功能集判断显示的界面
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
        if (integers.contains(7)) {
            functionBeans.add(new BluetoothLockFunctionBean(MyApplication.getInstance().getString(R.string.password), R.mipmap.bluetooth_password, TYPE_PASSWORD));
        }
        if (integers.contains(8)) {
            functionBeans.add(new BluetoothLockFunctionBean(MyApplication.getInstance().getString(R.string.fingerprint), R.mipmap.bluetooth_fingerprint, TYPE_FINGER));
        }
        if (integers.contains(9)) {
            functionBeans.add(new BluetoothLockFunctionBean(MyApplication.getInstance().getString(R.string.card), R.mipmap.bluetooth_card, TYPE_CARD));
        }
        functionBeans.add(new BluetoothLockFunctionBean(MyApplication.getInstance().getString(R.string.device_share), R.mipmap.bluetooth_share, TYPE_SHARE));
        functionBeans.add(new BluetoothLockFunctionBean(MyApplication.getInstance().getString(R.string.more), R.mipmap.bluetooth_more, TYPE_MORE));

        return functionBeans;
    }


    /**
     * 根据设备型号,获取授权界面的显示图片
     * @param model
     * @return
     */
    public static int getAuthorizationImageByModel(String model) {
        if (!TextUtils.isEmpty(model)) {
            if (model.startsWith("K7")) {
                return R.mipmap.bluetooth_authorization_lock_k7;
            }  else if (model.startsWith("K8-T")) {
                return R.mipmap.bluetooth_authorization_lock_k8_t;
            } else if (model.startsWith("K8")) {
                return R.mipmap.bluetooth_authorization_lock_k8;
            } else if (model.startsWith("K9")) {
                return R.mipmap.bluetooth_authorization_lock_k9;
            } else if (model.startsWith("KX")) {
                return R.mipmap.bluetooth_authorization_lock_kx;
            } else if (model.startsWith("S8C")) {
                return R.mipmap.bluetooth_authorization_lock_s8;
            } else if (model.startsWith("V6")) {
                return R.mipmap.bluetooth_authorization_lock_v6;
            } else if (model.startsWith("V7") || model.startsWith("S100")) {
                return R.mipmap.bluetooth_authorization_lock_v7;
            } else if (model.startsWith("S8")) {
                return R.mipmap.bluetooth_authorization_lock_s8;
            } else if (model.startsWith("QZ013")) {
                return R.mipmap.bluetooth_authorization_lock_qz013;
            } else if (model.startsWith("QZ012")) {
                return R.mipmap.bluetooth_authorization_lock_qz012;
            }else if (model.startsWith("S6")) {
                return R.mipmap.bluetooth_authorization_lock_s6;
            } else if (model.startsWith("K100")) {
                return R.mipmap.bluetooth_authorization_lock_k100;
            }else if (model.startsWith("H5606")){
                return R.mipmap.bluetooth_authorization_lock_h5606;
            }else {
                return R.mipmap.bluetooth_authorization_lock_default;
            }
        }else {
            return R.mipmap.bluetooth_authorization_lock_default;
        }
    }


    /**
     * 根据设备型号，获取设备列表显示的图片
     * @param model
     * @return
     */
    public static int getSmallImageByModel(String model) {
        if (!TextUtils.isEmpty(model)){
            if (model.startsWith("K7")){
               return R.mipmap.k7;
            }else if (model.startsWith("K8-T")){
                return R.mipmap.k8_t;
            }else if (model.startsWith("K8")){
               return R.mipmap.k8;
            }else if (model.startsWith("K9")){
               return R.mipmap.k9;
            }else if (model.startsWith("KX")){
               return R.mipmap.kx;
            }else if (model.startsWith("S8C")){
                return R.mipmap.s8;
            }else if (model.startsWith("V6")) {
                return R.mipmap.v6;
            }else if (model.startsWith("V7")|| model.startsWith("S100")) {
                return R.mipmap.v7;
            }else if (model.startsWith("S8")){
                return R.mipmap.s8;
            }else if (model.startsWith("QZ012")){
               return R.mipmap.qz012;
            }else if (model.startsWith("QZ013")){
               return R.mipmap.qz013;
            }else if (model.startsWith("S6")){
               return R.mipmap.s6;
            }else if (model.startsWith("K100")){
                return R.mipmap.k100;
            }else if (model.startsWith("H5606")){
                return R.mipmap.h5606;
            } else{
               return R.mipmap.default_zigbee_lock_icon;
            }
        }else{
           return R.mipmap.default_zigbee_lock_icon;
        }
    }


    /**
     * 根据设备型号，获取设备详情界面显示的图片
     * @param model
     * @return
     */
    public static int getDetailImageByModel(String model) {
        if (!TextUtils.isEmpty(model)) {
            if (model.contains("K7")) {
                return R.mipmap.bluetooth_lock_k7;
            }else if (model.contains("K8-T")) {
                return R.mipmap.bluetooth_lock_k8_t;
            }  else if (model.contains("K8")) {
                return R.mipmap.bluetooth_lock_k8;
            } else if (model.contains("K9")) {
                return R.mipmap.bluetooth_lock_k9;
            }  else if (model.contains("KX")) {
                return R.mipmap.bluetooth_lock_kx;
            }else if (model.contains("S8C")) {
                return R.mipmap.bluetooth_lock_s8;
            } else if (model.contains("V6")) {
                return R.mipmap.bluetooth_lock_v6;
            } else if (model.contains("V7") || model.contains("S100")) {
                return R.mipmap.bluetooth_lock_v7;
            }  else if (model.contains("S8")) {
                return R.mipmap.bluetooth_lock_s8;
            }else if (model.contains("QZ013")) {
                return R.mipmap.bluetooth_lock_qz013;
            } else if (model.contains("QZ012")) {
                return R.mipmap.bluetooth_lock_qz012;
            } else if (model.contains("K100")) {
                return R.mipmap.bluetooth_lock_k100;
            } else if (model.contains("S6")) {
                return R.mipmap.bluetooth_lock_s6;
            }else if (model.contains("H5606")) {
                return R.mipmap.bluetooth_lock_h5606;
            }else {
                return R.mipmap.bluetooth_lock_default;
            }
        }else {
            return R.mipmap.bluetooth_lock_default;
        }
    }


}
