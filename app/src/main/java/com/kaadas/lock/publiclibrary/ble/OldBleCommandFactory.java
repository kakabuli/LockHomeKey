package com.kaadas.lock.publiclibrary.ble;


/**
 * Create By lxj  on 2019/1/7
 * Describe
 */
public class OldBleCommandFactory {
    public static final int defineTime = 946684800 - 28800;      //1970-200年的时间 秒数 时差28800

    /**
     * 唤醒猫眼的帧
     * @return
     */
    public static byte[] getWakeUpFrame() {
        // 确认帧
        byte[] inNetConfirm = new byte[]{
                0x00,0x00,0x00,0x00,
                0x00,0x00,0x00,0x00,
                0x00,0x00,0x00,0x00,
                0x00,0x00,0x00,0x00,
                0x00,0x0d,0x0a,0x00
        };
        return inNetConfirm;
    }

    /**
     * 数据后面的0
     * @return
     */
    public static byte[] getEndFrame() {
        // 确认帧
        byte[] inNetConfirm = new byte[]{0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00 };
        return inNetConfirm;
    }

    /**
     * 入网确认帧
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
}
