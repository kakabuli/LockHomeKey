package com.kaadas.lock.publiclibrary.ble;


import com.kaadas.lock.utils.Rsa;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Create By lxj  on 2019/1/7
 * Describe
 */
public class OldBleCommandFactory {
    public static final int defineTime = 946684800 - 28800;      //1970-200年的时间 秒数 时差28800

    /**
     * 唤醒锁的命令
     *  在开门之前都要发送一次
     * @return
     */
    public static byte[] getWakeUpFrame() {
        // 确认帧
        byte[] inNetConfirm = new byte[]{
                0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00,
                0x00, 0x0d, 0x0a, 0x00
        };
        return inNetConfirm;
    }

    /**
     * 唤醒猫眼的帧
     *
     * @return
     */
    public static List< byte[]> getOpenLockCommands() {
        // 确认帧
        List<byte[]> commands = new ArrayList<>();
        byte[] openlockCommandFull = new byte[]{
                (byte) 0xf5, (byte) 0xcb, (byte) 0x00, (byte) 0x1c,
                (byte) 0xc2, (byte) 0x01, (byte) 0x08, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
        short shortSum = 0;
        for (int i = 3; i < openlockCommandFull.length; i++) {
            shortSum += Integer.parseInt(Integer.toHexString((int) (openlockCommandFull[i] & 0xFF)), 16);
        }
        byte[] bSend = new byte[2];
        bSend[0] = (byte) (shortSum >> 0);// 低字节
        bSend[1] = (byte) (shortSum >> 8);// 高字节
        Random random = new Random();// 定义随机类
        int result = random.nextInt(10);// 返回[0,10)集合中的整数，注意不包括10
        byte[] enCommand = Rsa.encrypt2(openlockCommandFull, oldModeKey.get(result));// 加密数据
        final byte[] command1 = new byte[20];
        final byte[] command2 = new byte[12];
        for (int i=0;i<20;i++){
            command1[i] = enCommand[i];
        }
        for (int i=0;i<12;i++){
            command2[i] = enCommand[i+20];
        }
        commands.add(command1);
        commands.add(command2);
        return commands;
    }

    /**
     * 数据后面的0
     *
     * @return
     */
    public static byte[] getEndFrame() {
        // 确认帧
        byte[] inNetConfirm = new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        return inNetConfirm;
    }

    /**
     * 入网确认帧
     *
     * @param isSuccess 是否正确响应
     * @return
     */
    public static byte[] getInNetConfirm(boolean isSuccess) {
        // 确认帧
        byte[] inNetConfirm = new byte[]{
                (byte) 0x5f, (byte) 0x80, (byte) 0x00, (byte) 0x1c, (byte) 0x80, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
        return inNetConfirm;
    }

    /**
     * 入网应答帧
     *
     * @return
     */
    public static byte[] getInNetResponse(boolean isSuccess) {
        byte[] inNetResponse;
        // 应答帧
        if (isSuccess) {
            inNetResponse = new byte[]{(byte) 0x5f, (byte) 0x30, (byte) 0x01, (byte) 0x1c, (byte) 0xb0, (byte) 0x80, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
        } else {
            inNetResponse = new byte[]{(byte) 0x5f, (byte) 0x31, (byte) 0x01, (byte) 0x1c, (byte) 0xb0, (byte) 0x81, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
        }
        return inNetResponse;
    }

    /**
     * 老模块的Key
     */
    public static List<byte[]> oldModeKey = new ArrayList<>();

    static {
        byte[] aesPaw1 = new byte[]{0x56, 0x6d, 0x59, 0x5a, 0x57, 0x59, 0x65, 0x32, 0x78, 0x47, 0x70, 0x79, 0x31, 0x49, 0x66, 0x6b};
        byte[] aesPaw2 = new byte[]{0x6d, 0x35, 0x35, 0x47, 0x52, 0x79, 0x57, 0x7a, 0x37, 0x6a, 0x6b, 0x36, 0x55, 0x4c, 0x39, 0x4f};
        byte[] aesPaw3 = new byte[]{0x37, 0x44, 0x7a, 0x32, 0x55, 0x79, 0x61, 0x50, 0x54, 0x59, 0x61, 0x49, 0x4e, 0x4f, 0x68, 0x54};
        byte[] aesPaw4 = new byte[]{0x37, 0x44, 0x55, 0x34, 0x78, 0x70, 0x77, 0x4f, 0x61, 0x42, 0x45, 0x39, 0x64, 0x56, 0x6e, 0x75};
        byte[] aesPaw5 = new byte[]{0x35, 0x76, 0x71, 0x75, 0x4e, 0x58, 0x31, 0x50, 0x5a, 0x75, 0x61, 0x74, 0x47, 0x44, 0x34, 0x58};
        byte[] aesPaw6 = new byte[]{0x56, 0x36, 0x54, 0x4e, 0x53, 0x45, 0x72, 0x68, 0x58, 0x50, 0x67, 0x64, 0x4a, 0x53, 0x5a, 0x55};
        byte[] aesPaw7 = new byte[]{0x38, 0x72, 0x72, 0x6b, 0x63, 0x42, 0x53, 0x77, 0x39, 0x39, 0x32, 0x38, 0x70, 0x78, 0x6d, 0x6a};
        byte[] aesPaw8 = new byte[]{0x72, 0x59, 0x41, 0x36, 0x78, 0x6d, 0x39, 0x6d, 0x50, 0x31, 0x67, 0x71, 0x64, 0x49, 0x74, 0x5a};
        byte[] aesPaw9 = new byte[]{0x64, 0x78, 0x6a, 0x34, 0x69, 0x77, 0x58, 0x50, 0x42, 0x52, 0x50, 0x4d, 0x32, 0x75, 0x6b, 0x34};
        byte[] aesPaw10 = new byte[]{0x6c, 0x72, 0x79, 0x30, 0x43, 0x72, 0x50, 0x35, 0x48, 0x44, 0x47, 0x4c, 0x35, 0x56, 0x71, 0x59};
        oldModeKey.add(aesPaw1);
        oldModeKey.add(aesPaw2);
        oldModeKey.add(aesPaw3);
        oldModeKey.add(aesPaw4);
        oldModeKey.add(aesPaw5);
        oldModeKey.add(aesPaw6);
        oldModeKey.add(aesPaw7);
        oldModeKey.add(aesPaw8);
        oldModeKey.add(aesPaw9);
        oldModeKey.add(aesPaw10);
    }

    /**
     * 获取电量的指令
     */

    public static byte[] getPowerCommand(){
        byte[] command = new byte[]{(byte) 0xf5, (byte) 0xc1, (byte) 0x00, (byte) 0x1c, (byte) 0xc1, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
        return command;
    }

}
