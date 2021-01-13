package com.kaadas.lock.publiclibrary.ble;

import android.text.TextUtils;

import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.Rsa;


/**
 * Create By lxj  on 2019/1/7
 * Describe
 */
public class BleCommandFactory {
    public static final int defineTime = 946684800 - 28800;      //1970-200年的时间 秒数 时差28800

    private static int TSN = 1;

    /**
     * @param pwd1        密码1
     * @param pwd2        密码2
     * @param systemBytes SystemId
     *                    负载数据为  Password1+Password2 加密 SystemID
     * @return
     */
    public static byte[] getAuthCommand(String pwd1, String pwd2, byte[] systemBytes) {
        byte cmd = 0x01;
        byte[] payload = new byte[16];
        byte[] serverPsw1 = Rsa.hex2byte(pwd1);
        byte[] tempPsw2 = Rsa.hex2byte(pwd2);
        System.arraycopy(serverPsw1, 0, payload, 0, serverPsw1.length);
        System.arraycopy(tempPsw2, 0, payload, serverPsw1.length, tempPsw2.length);

        return groupPackage(cmd, systemBytes, payload);
    }

    /**
     * @param originalData 接收到的原始数据
     * @return 绑定确认帧
     */
    public static byte[] confirmCommand(byte[] originalData) {
        byte[] bytes = new byte[20];
        bytes[1] = originalData[1];

        if ((((originalData[3] & 0xff) == 0x95)
                ||(((originalData[3] & 0xff) == 0x92))
                ||(((originalData[3] & 0xff) == 0x93)))){
            //单火项目要求ACK带CMD
            bytes[3] = originalData[3];
        }

        return bytes;
    }

    /**
     * 锁控制命令
     *
     * @param action 锁操作 0x00：UnLock  0x01：Lock  0x02：Toggle  0x03~0xFF：保留
     * @param type   开锁类型 0x00：保留  0x01：PIN 密码  0x02：RFID 卡片  0x04：APP 开锁（锁不鉴权）
     * @param pwd    开锁密码
     * @param key    加密秘钥
     * @return
     */
    public static byte[] controlLockCommand(byte action, byte type, String pwd, byte[] key) {
        //Action  1   Code Type  1   UserID  1  Length 1  Code 密码  转换成ascll
        //6~12 PIN Code 密码长度为6-12
        byte cmd = 0x02;
        byte[] payload = new byte[16]; //负载数据
        payload[0] = action;
        payload[1] = type;
        payload[2] = 0x00;
        if (type != 0x04) {  //需要密码才  直接开锁
            if (!TextUtils.isEmpty(pwd)) {  //如果密码不为空   才设置
                payload[3] = (byte) pwd.length();
                System.arraycopy(pwd.getBytes(), 0, payload, 4, pwd.getBytes().length);
            }
        }
        return groupPackage(cmd, payload, key);
    }

    /**
     * 秘钥管理操作   一般情况都是操作pin密码   即一般密码
     *
     * @param action 秘钥操作    0x00：保留      0x01：Set Code 设置密钥      0x02：Get Code 查询密钥      0x03：Clear Code 删除密钥      0x04：Check Code 验证密钥
     * @param type   秘钥类型      0x00：保留     0x01：PIN 密码（Set\Get\Clear）     0x02：指纹（Get\Clear）     0x03：RFID 卡片（Get\Clear）     0x04：管理员密码（Set\Check）
     * @param userID 0~9/19 Code Type 为 PIN 时      0~99 Code Type 为指纹时      0~99 Code Type 为 RFID 时      0xFF Action 为 Clear 时删除所有
     * @param pwd    开锁密码
     * @param key    加密秘钥
     * @return
     */
    public static byte[] controlKeyCommand(byte action, byte type, int userID, String pwd, byte[] key) {
        //密码长度           6~12/4~10 Set PIN Code          4~10 Set RFID Code          0 Get/Clear Code
        //  Action   操作类型    CodeType  秘钥类型     UserID   用户Id    Length   密码长度   Code  密码
        byte cmd = 0x03;
        byte[] payload = new byte[16]; //负载数据
        payload[0] = action;
        payload[1] = type;
        payload[2] = (byte) userID;
        if (!TextUtils.isEmpty(pwd)) { //密码不为空时设置密码   需不需要密码由逻辑层判断
            payload[3] = (byte) pwd.length();
            byte[] pwdBytes = new byte[pwd.length()];
            for (int i = 0; i < pwd.length(); i++) {
                int string = Integer.valueOf(pwd.substring(i, i + 1));
                pwdBytes[i] = (byte) (string + 48);
            }

            System.arraycopy(pwdBytes, 0, payload, 4, pwd.length());
        }
        return groupPackage(cmd, payload, key);
    }


    /**
     * 获取开锁记录
     * LogIndex Start：日志起始序号
     * LogIndex Start 的值为查询日志起始序号。
     * LogIndex End：日志结束序号
     * LogIndex End 必须大于等于 LogIndex Start，当只读取一条记录时 LogIndex start
     * 等于 LogIndex End。如果 LogIndex Start 大于 LogTotal 则直接返回确认帧。
     * 注意 ：的 锁最多存储最近的 20 条/ 组*10 组共 200 条记录 ，前 当前 BLE 锁记录查询只
     * 支持单条、单组和所有记录查询。
     * 单条：LogIndex Start = LogIndex End，取值：0~199；
     * 单组：LogIndex Start,LogIndex End 取值：{[0,20],[20,40],……,[180,200]}；
     * 全部：LogIndex Start,LogIndex End 取值：[1,200]
     *
     * @param startIndex 查询日志起始序号。
     * @param endIndex   日志结束序号
     * @param key        加密秘钥
     * @return
     */

    public static byte[] getLockRecordCommand(byte startIndex, byte endIndex, byte[] key) {

        byte cmd = 0x04;
        byte[] payload = new byte[16]; //负载数据
        payload[0] = startIndex;
        payload[1] = endIndex;

        return groupPackage(cmd, payload, key);
    }


    /**
     * 设置锁参数
     *
     * @param number     序号及设置的类型
     *                   操作类型          序列号     值长度    值内容
     *                   语言            0x01        2     ISO 639-1 标准   zh：中文 en：英语
     *                   音量            0x02        1     0x00：Silent Mode 静音   0x01：Low Volume 低音量   0x02：High Volume 高音量0x03~0xFF：保留
     *                   时间            0x03        4     时间秒计数。以 2000-01-01 00:00:00（本地   时间）为起点开始计数
     *                   自动关门        0x04        1     0x00：开启   0x01：关闭
     *                   反锁            0x05        1     0x00：关闭反锁   0x01：开启反锁
     *                   离家模式/布防   0x06        1     0x00：开启   0x01：关闭
     *                   蓝牙开关        0x07        1     0x00：开启   0x01：关闭
     *                   安全模式        0x08        1     0x00：关闭（正常模式）    0x01：开启
     * @param valueArray
     * @param key
     * @return
     */
    public static byte[] setLockParamsCommand(byte number, byte[] valueArray, int length, byte[] key) {
        byte cmd = 0x06;
        byte[] payload = new byte[16]; //负载数据
        payload[0] = number;
        payload[1] = (byte) length;
        System.arraycopy(valueArray, 0, payload, 2, length);
        return groupPackage(cmd, payload, key);
    }

    public static byte[] setLockParamsCommand(byte number, byte[] valueArray, byte[] key) {
        byte cmd = 0x06;
        byte[] payload = new byte[16]; //负载数据
        payload[0] = number;
        payload[1] = (byte) valueArray.length;
        System.arraycopy(valueArray, 0, payload, 2, valueArray.length);
        return groupPackage(cmd, payload, key);
    }


    /**
     * 用户类型设置
     *
     * @param codeType 密钥类型：    0x00：保留    0x01：PIN 密码    0x02：指纹    0x03：RFID 卡片
     * @param userID   用户编号      0~9/19 Code Type 为 PIN 时   0~99 Code Type 为指纹时   0~99 Code Type 为 RFID 时
     * @param userType 用户类型     0x00 默认（永久）     0x01 时间表用户      0x02 胁迫     0x03 管理员
     *                 0x04 无权限用户（查询权限）        0xFD 访客密码       0xFE 一次性密码
     * @param key
     * @return
     */
    public static byte[] setUserTypeCommand(byte codeType, byte userID, byte userType, byte[] key) {
        byte cmd = 0x09;
        byte[] payload = new byte[16]; //负载数据
        payload[0] = codeType;
        payload[1] = userID;
        payload[2] = userType;
        return groupPackage(cmd, payload, key);
    }

    /**
     * 用户类型查询
     *
     * @param codeType 密钥类型：    0x00：保留    0x01：PIN 密码    0x02：指纹    0x03：RFID 卡片
     * @param userID   用户编号      0~9/19 Code Type 为 PIN 时   0~99 Code Type 为指纹时   0~99 Code Type 为 RFID 时
     * @param key
     * @return
     */
    public static byte[] queryUserTypeCommand(byte codeType, byte userID, byte[] key) {
        byte cmd = 0x0A;
        byte[] payload = new byte[16]; //负载数据
        payload[0] = codeType;
        payload[1] = userID;
        return groupPackage(cmd, payload, key);
    }


    /**
     * 设置周计划
     * 每周的所有天数都复用的办一个时间计划
     * 结束时间必须大于开始时间
     *
     * @param scheduleId  计划编号
     * @param codeType    密钥类型：          0x00：保留      0x01：PIN 密码     0x03：RFID 卡片
     * @param userID      用户编号：     0~9 Code Type 为 PIN 时       0~99 Code Type 为 RFID 时
     * @param day         日掩码
     * @param startHout   开始时间
     * @param startMinute 开始的分钟
     * @param endHour     结束的时间
     * @param endMinute   结束的分钟
     * @param key         加密秘钥
     * @return
     */
    public static byte[] setWeekPlanCommand(byte scheduleId, byte codeType,
                                            byte userID, byte day,
                                            byte startHout, byte startMinute,
                                            byte endHour, byte endMinute,
                                            byte[] key
    ) {
        byte cmd = 0x0B;
        byte[] payload = new byte[16]; //负载数据
        payload[0] = scheduleId;
        payload[1] = codeType;
        payload[2] = userID;
        payload[3] = day;
        payload[4] = startHout;
        payload[5] = startMinute;
        payload[6] = endHour;
        payload[7] = endMinute;
        return groupPackage(cmd, payload, key);
    }


    /**
     * 周计划查询
     *
     * @param scheduleId 计划编号
     * @param key
     * @return
     */
    public static byte[] queryWeekPlanCommand(byte scheduleId, byte[] key) {
        byte cmd = 0x0C;
        byte[] payload = new byte[16]; //负载数据
        payload[0] = scheduleId;
        return groupPackage(cmd, payload, key);
    }


    /**
     * 周计划删除
     *
     * @param scheduleId 计划编号
     * @param key
     * @return
     */
    public static byte[] deleteWeekPlanCommand(byte scheduleId, byte[] key) {
        byte cmd = 0x0D;
        byte[] payload = new byte[16]; //负载数据
        payload[0] = scheduleId;
        return groupPackage(cmd, payload, key);
    }


    /**
     * 设置年计划
     *
     * @param scheduleId 计划Id
     * @param userID     用户Id
     * @param codeType   密码类型        0x00：保留   0x01：PIN 密码   0x03：RFID 卡片
     * @param startTime  4位
     * @param endTiem    4位
     * @param key
     * @return
     */
    public static byte[] setYearPlanCommand(byte scheduleId, byte userID,
                                            byte codeType, byte[] startTime,
                                            byte[] endTiem, byte[] key) {
        byte cmd = 0x0E;
        byte[] payload = new byte[16]; //负载数据
        payload[0] = scheduleId;
        payload[1] = userID;
        payload[2] = codeType;
        System.arraycopy(startTime, 0, payload, 3, startTime.length);
        System.arraycopy(endTiem, 0, payload, 7, endTiem.length);
        return groupPackage(cmd, payload, key);
    }


    /**
     * 年计划查询
     *
     * @param scheduleId 计划编号
     * @param key
     * @return
     */
    public static byte[] queryYearPlanCommand(byte scheduleId, byte[] key) {
        byte cmd = 0x0F;
        byte[] payload = new byte[16]; //负载数据
        payload[0] = scheduleId;
        return groupPackage(cmd, payload, key);
    }


    /**
     * 年计划删除
     *
     * @param scheduleId 计划编号
     * @param key
     * @return
     */
    public static byte[] deleteYearPlanCommand(byte scheduleId, byte[] key) {
        byte cmd = 0x10;
        byte[] payload = new byte[16]; //负载数据
        payload[0] = scheduleId;
        return groupPackage(cmd, payload, key);
    }


    /**
     * 同步门锁密钥状态：
     *
     * @param codeType Code Type 密钥类型：     0x00：保留     0x01：PIN 密码     0x02：指纹     0x03：RFID 卡片        0x04：保留（管理员密码
     * @param key
     * @return
     */
    public static byte[] syncLockPasswordCommand(byte codeType, byte[] key) {
        byte cmd = 0x11;
        byte[] payload = new byte[16]; //负载数据
        payload[0] = codeType;
        return groupPackage(cmd, payload, key);
    }


    /**
     * 查询门锁基本信息  此命令固定
     *
     * @param key
     * @return
     */
    public static byte[] syncLockInfoCommand(byte[] key) {
        LogUtils.e("获取门锁信息   ");
        byte cmd = 0x12;
        byte[] payload = new byte[16]; //负载数据
        payload[0] = 0x01;
        return groupPackage(cmd, payload, key);
    }

    /**
     * APP 绑定请求帧
     *
     * @param password 管理密码
     * @param random   随机码
     * @param key
     * @return
     */
    public byte[] requestBindLock(String password, String random, byte[] key) {
        byte cmd = 0x13;
        byte[] payload = new byte[16]; //负载数据
        payload[0] = (byte) password.getBytes().length;
        System.arraycopy(password.getBytes(), 0, payload, 1, password.getBytes().length);
        System.arraycopy(random.getBytes(), 0, payload, 1 + password.getBytes().length, random.getBytes().length);
        return groupPackage(cmd, payload, key);
    }


    /**
     * 锁报警记录查询
     *
     * @param startIndex
     * @param endIndex
     * @param key        LogIndex End必须大于等于LogIndex Start，当只读取一条记录时LogIndex start等于LogIndex End。如果LogIndex Start大于LogTotal则直接返回确认帧。
     *                   注意：锁最多存储最近的20条/组*10组共200条记录，当前BLE锁记录查询只支持单条、单组和所有记录查询。
     *                   单条：LogIndex Start = LogIndex End，取值：0~199；
     *                   单组：LogIndex Start,LogIndex End取值：{[0,20],[20,40],……,[180,200]}；
     *                   全部：LogIndex Start,LogIndex End取值：[1,200]。
     * @return
     */
    public static byte[] searchLockWarringRecord(int startIndex, int endIndex, byte[] key) {
        byte cmd = 0x14;
        byte[] payload = new byte[16]; //负载数据
        payload[0] = (byte) startIndex;
        payload[1] = (byte) endIndex;
        return groupPackage(cmd, payload, key);
    }


    /**
     * 2.2.21.	锁序列号查询
     *
     * @param key
     * @return
     */
    public static byte[] searchLockNumber(byte[] key) {
        byte cmd = 0x15;
        byte[] payload = new byte[16]; //负载数据
        return groupPackage(cmd, payload, key);
    }


    /**
     * 2.2.22.	锁开锁次数查询：0x16
     *
     * @param key
     * @return
     */
    public static byte[] searchOpenNumber(byte[] key) {
        byte cmd = 0x16;
        byte[] payload = new byte[16]; //负载数据
        return groupPackage(cmd, payload, key);
    }


    /**
     * 获取操作记录
     * LogIndex Start：查询的记录起始序号
     * LogIndex End：查询的记录结束序号
     * LogIndex End必须大于等于LogIndex Start，当只读取一条记录时LogIndex start等于LogIndex End。如果LogIndex Start大于LogTotal则直接返回确认帧。
     *
     * @param startIndex 查询日志起始序号。
     * @param endIndex   日志结束序号
     * @param key        加密秘钥
     * @return
     */

    public static byte[] getOperationCommand(byte[] startIndex, byte[] endIndex, byte[] key) {
        byte cmd = 0x18;
        byte[] payload = new byte[16]; //负载数据
        payload[0] = 0x01;
        payload[1] = 0x03;
        System.arraycopy(startIndex, 0, payload, 2, 2);
        System.arraycopy(endIndex, 0, payload, 4, 2);
        LogUtils.d("davi startIndex " + Rsa.bytesToHexString(startIndex) + " endIndex " + Rsa.bytesToHexString(endIndex));
        return groupPackage(cmd, payload, key);
    }


    /**
     * 获取子模块列表
     * @return
     */

    public static byte[] getModuleList(byte[] key) {
        byte cmd = (byte) 0x83;
        byte[] payload = new byte[16]; //负载数据
        return groupPackage(cmd, payload, key);
    }

    /**
     * 获取子模块版本信息
     * @param key
     * @param number   模块号
     * @return
     */

    public static byte[] getModuleVersion(byte[] key,byte number) {
        byte cmd = (byte) 0x84;
        byte[] payload = new byte[16]; //负载数据
        payload[0] = number;
        return groupPackage(cmd, payload, key);
    }




    /**
     *  ota控制
     * @param key
     * @param operation  0x1 升级请求；0x2 退出OTA请求；
     * @param number    模块编号
     * @param otaType  ota类型
     * @param newVersion  新版本
     * @return
     */
    public static byte[] moduleOtaRequest(byte[] key,byte operation,byte number,byte otaType ,byte[] newVersion) {
        byte cmd = (byte) 0x85;
        byte[] payload = new byte[16]; //负载数据
        payload[0] = operation;
        payload[1] = number;
        payload[2] = otaType;
        System.arraycopy(newVersion,0,payload,3,newVersion.length);
        return groupPackage(cmd, payload, key);
    }



    /**
     *  传输文件状态上报
     * @param key
     * @param state   模块号  0x1 开始传输文件；0x2传输文件中；0x3传输文件完成；
     * @return
     */

    public static byte[] reportOtaFileState(byte[] key,byte state ) {
        byte cmd = (byte) 0x88;
        byte[] payload = new byte[16]; //负载数据
        payload[0] = state;
        return groupPackage(cmd, payload, key);
    }

    /**
     * 心跳帧
     *
     * @return
     */
    public static byte[] heartCommand() {
        byte[] bytes = new byte[20];
        bytes[1] = getTSN();
        bytes[3] = (byte) 0xAA;
        bytes[4] = (byte) 0xFF;
        bytes[5] = (byte) 0xFF;
        return bytes;
    }


    public synchronized static byte getTSN() {
        TSN = TSN % 0xff;
        if (TSN == 0) {
            TSN = 1;
        }
        return (byte) TSN++;
    }


    /**
     * 组成20个字节的数据帧   发送的数据包
     */
    public static byte[] groupPackage(byte cmd, byte[] payload, byte[] key) {
        byte[] tempBuff = null;
        byte checkSum = 0;
        byte[] dataFrame = new byte[20];
        for (int i = 0; i < 20; i++) {
            dataFrame[i] = 0;
        }

        //加密前把数据的校验和算出来
        for (int i = 0; i < payload.length; i++) {
            checkSum += payload[i];
        }
//        LogUtils.e("--kaadas--加密前 " + Rsa.bytesToHexString(payload) + "key是  " + Rsa.bytesToHexString(key));
        if (key != null) {
            dataFrame[0] = 1;
            tempBuff = Rsa.encryptAES(payload, key);
        } else {
            dataFrame[0] = 0;
            tempBuff = payload;
        }
        //设置数据头  是加密的
//        dataFrame[0] = 1;
        dataFrame[1] = getTSN();
        dataFrame[2] = checkSum;
        dataFrame[3] = cmd;
        System.arraycopy(tempBuff, 0, dataFrame, 4, tempBuff.length);
//        LogUtils.e("加密后 " + Rsa.bytesToHexString(tempBuff));
//        LogUtils.e("加密后  整体数据 " + Rsa.bytesToHexString(dataFrame));
        return dataFrame;
    }

    /**
     *  密钥因子校验结果
     * @param key
     * @param result
     * @return
     */

    public static byte[] onDecodeResult(byte[] key,byte result) {
        byte cmd = (byte) 0x94;
        byte[] payload = new byte[16]; //负载数据
        payload[0] = result;
        return groupPackage(cmd, payload, key);
    }

    /**
     *  发送WIFI SSID
     * @param key
     * @param subSSID：分包
     * @return
     */
    public static byte[] sendWiFiSSID(int index,byte[] key,byte[] subSSID) {
        byte cmd = (byte) 0x90;
        byte[] payload = new byte[16]; //负载数据
        payload[0] = Rsa.int2BytesArray(33)[0];//SSID总长度
        payload[1] = Rsa.int2BytesArray(index)[0];//包序号
        System.arraycopy(subSSID,0,payload,2,subSSID.length);
        return groupPackage(cmd, payload, key);
    }

    /**
     * 发送WIFI SSID
     */
    public static byte[] sendHangerWiFiSSID(int index,byte[] key,byte[] subSSID) {
        byte cmd = (byte) 0x90;
        byte[] payload = new byte[16]; //负载数据
        payload[0] = Rsa.int2BytesArray(32)[0];//SSID总长度
        payload[1] = Rsa.int2BytesArray(index)[0];//包序号
        System.arraycopy(subSSID,0,payload,2,subSSID.length);
        return groupPackage(cmd, payload, key);
    }

    /**
     *  发送WIFI PWD
     * @param key
     * @param subPWD：分包
     * @return
     */
    public static byte[] sendHangerWiFiPWD(int index,byte[] key,byte[] subPWD) {
        byte cmd = (byte) 0x91;
        byte[] payload = new byte[16]; //负载数据
        payload[0] = Rsa.int2BytesArray(64)[0];//SSID总长度
        payload[1] = Rsa.int2BytesArray(index)[0];//包序号
        System.arraycopy(subPWD,0,payload,2,subPWD.length);
        return groupPackage(cmd, payload, key);
    }

    /**
     *  发送WIFI PWD
     * @param key
     * @param subPWD：分包
     * @return
     */
    public static byte[] sendWiFiPWD(int index,byte[] key,byte[] subPWD) {
        byte cmd = (byte) 0x91;
        byte[] payload = new byte[16]; //负载数据
        payload[0] = Rsa.int2BytesArray(65)[0];//SSID总长度
        payload[1] = Rsa.int2BytesArray(index)[0];//包序号
        System.arraycopy(subPWD,0,payload,2,subPWD.length);
        return groupPackage(cmd, payload, key);
    }
}
