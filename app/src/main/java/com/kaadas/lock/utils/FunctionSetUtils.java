package com.kaadas.lock.utils;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.bean.BluetoothLockFunctionBean;
import com.kaadas.lock.bean.FunctionBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FunctionSetUtils {


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
        FUNCTION_SET.put(0x00, new Integer[]{1,2,3,10});
        FUNCTION_SET.put(0x01, new Integer[]{1,2,3,4,5,6,7,8,9,10,11,13,14,15,16,17,18,19,20,21,22});
        FUNCTION_SET.put(0x02, new Integer[]{1,2,3,4,5,6,7,8,10,11,13,14,15,16,17,19,20,21,22});
        FUNCTION_SET.put(0x03, new Integer[]{1,2,3,4,5,6,7,8,10,11,13,14,15,16,17,19,20,21,22,23});
        FUNCTION_SET.put(0x20, new Integer[]{1,2,3,4,5,7,8,10,13,16,17,19,20,21,22});
        FUNCTION_SET.put(0x31, new Integer[]{1,2,3,4,5,6,7,8,10,13,16,17,19,20,21,22});
        FUNCTION_SET.put(0x32, new Integer[]{1,2,3,4,5,7,8,9,10,13,16,17,19,20,21,22});
        FUNCTION_SET.put(0x33, new Integer[]{1,2,3,4,5,6,7,8,9,10,13,16,17,19,20,21,22});
        FUNCTION_SET.put(0x34, new Integer[]{1,2,3,4,5,6,7,8,9,10,12,13,16,17,19,20,21,22});
        FUNCTION_SET.put(0x35, new Integer[]{1,2,3,4,5,6,7,8,10,12,13,16,17,19,20,21,22,23});
    }


    /**
     * 根据功能集判断是否支持手动自动模式显示
     * @param functionSet
     * @return
     */
    public static boolean isSupportAMModeShow(int functionSet) {
        Integer[] funcs = FUNCTION_SET.get(functionSet);
        List<Integer> integers = Arrays.asList(funcs);
        return integers.contains(11);
    }
    /**
     * 根据功能集判断是否支持手动自动模式设置
     * @param functionSet
     * @return
     */
    public static boolean isSupportAMModeSet(int functionSet) {
        Integer[] funcs = FUNCTION_SET.get(functionSet);
        List<Integer> integers = Arrays.asList(funcs);
        return integers.contains(12);
    }

    /**
     * 根据功能集判断是否支持修改管理员密码
     * @param functionSet
     * @return
     */
    public static boolean isSupportModifyManagerPassword(int functionSet) {
        Integer[] funcs = FUNCTION_SET.get(functionSet);
        List<Integer> integers = Arrays.asList(funcs);
        return integers.contains(18);
    }


    public static boolean isExistFunctionSet(int functionSet){
        //获取改功能集是否存在
        return !(FUNCTION_SET.get(functionSet)==null);

    }

    /**
     * 根据功能集判断显示的界面
     * @param functionSet
     * @return
     */
    public static  List<BluetoothLockFunctionBean> getSupportFunction(int functionSet) {
        List<BluetoothLockFunctionBean> functionBeans = new ArrayList<>();
        Integer[] funcs = FUNCTION_SET.get(functionSet);
        System.out.println(funcs==null);
        List<Integer> integers = Arrays.asList(funcs);
        if (funcs == null){ //未知功能集
            funcs = FUNCTION_SET.get(0x31);
        }

        if (integers.contains(7)){
            functionBeans.add(new BluetoothLockFunctionBean(MyApplication.getInstance().getString(R.string.password),R.mipmap.bluetooth_password,1));
        }
        if (integers.contains(8)){
            functionBeans.add(new BluetoothLockFunctionBean(MyApplication.getInstance().getString(R.string.fingerprint),R.mipmap.bluetooth_fingerprint,2));
        }
        if (integers.contains(9)){
            functionBeans.add(new BluetoothLockFunctionBean(MyApplication.getInstance().getString(R.string.card),R.mipmap.bluetooth_card,3));
        }
        functionBeans.add(new BluetoothLockFunctionBean(MyApplication.getInstance().getString(R.string.device_share),R.mipmap.bluetooth_share,4));
        functionBeans.add(new BluetoothLockFunctionBean(MyApplication.getInstance().getString(R.string.more),R.mipmap.bluetooth_more,5));

        return functionBeans;
    }


    public static void main(String[] args ){

        List<BluetoothLockFunctionBean> functionBeans = new ArrayList<>();
        Integer[] funcs = FUNCTION_SET.get(5);
        System.out.println(funcs==null);


    }

}
