package com.kaadas.lock.publiclibrary.ble;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;


import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.publiclibrary.bean.ForeverPassword;
import com.kaadas.lock.publiclibrary.ble.bean.OpenLockRecord;
import com.kaadas.lock.publiclibrary.ble.bean.OperationLockRecord;
import com.kaadas.lock.publiclibrary.ble.bean.WarringRecord;
import com.kaadas.lock.publiclibrary.http.result.GetPasswordResult;
import com.kaadas.lock.utils.DateUtils;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.Rsa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Create By lxj  on 2019/1/7
 * Describe
 */
public class BleUtil {


    public static final String PASSWORD = "密码";
    public static final String RF = "遥控";
    public static final String MANUAL = "手动";
    public static final String RFID = "卡片";
    public static final String FINGERPRINT = "指纹";
    public static final String VOICE = "语音";
    public static final String FINGER_VEIN = "静脉";
    public static final String FACE_RECOGNITION = "人脸";
    public static final String PHONE = "手机";
    public static final String ONE_KEY_OPEN = "一键开启";
    public static final String UNKNOWN_OPEN = "不确定";

    public static final String DOOR_LOCK = "门锁";
    public static final String APP = "App";

    public static final int BLE_VERSION_OLD = 1; //最老的版本  老版本协议  功能只有开锁  开锁记录  电量
    public static final int BLE_VERSION_NEW1 = 2;   //中间的版本  新版本协议   功能只有开锁  开锁记录  电量
    public static final int BLE_VERSION_NEW2 = 3;   //最新的版本  最新的协议  全功能支持


    public static final String ADMINISTRATOR_PASSWORD_MODIFY = "修改管理员密码";
    public static final String PASSWORD_ADD = "添加密码";
    public static final String PASSWORD_DELETE = "删除密码";
    public static final String FINGERPRINT_ADD = "添加指纹";
    public static final String FINGERPRINT_DELETE = "删除指纹";
    public static final String FACE_ADD = "人脸添加";
    public static final String FACE_DELETE = "人脸删除";
    public static final String INTRAVENOUS_ADD = "静脉添加";
    public static final String INTRAVENOUS_DELETE = "静脉删除";
    public static final String RETAIN = "保留";

    public static String getRealName(byte[] data) {
        ParseBean parseBean = ParseHead(data);
        for (int i = 0; i < parseBean.Type.size(); i++) {
            if ("09".equals(parseBean.Type.get(i))) {  //此时的数据是
                return new String(parseBean.Date.get(i));
            }
        }
        return null;
    }

    public static class ParseBean {
        public List<Integer> Len = new ArrayList<>();
        public List<String> Type = new ArrayList<>();
        public List<byte[]> Date = new ArrayList<>();

        @Override
        public String toString() {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < Len.size(); i++) {
                sb.append("   " + Len.get(i) + "   ");
                sb.append("   " + Type.get(i) + "   ");
                sb.append("   " + Date.get(i));
                sb.append("\n");
            }

            return sb.toString();
        }
    }


    /**
     * 解析广播包
     *
     * @param scanDate
     * @return
     */
    public static ParseBean ParseHead(byte[] scanDate) {
        System.out.print("数据是  " + Arrays.asList().toString());
        ParseBean parseBean = new ParseBean();
        int len = 0;
        int type = 0;
        int dateLen = 0;
        int index = 0;
        while (scanDate[index] != 0) {
            len = 0xff & scanDate[index];
//            MyLog.E("长度1是  " + len);
            index += 1;
//            MyLog.E("index 是" + index);
            type = 0xff & scanDate[index];
            String sType = Rsa.byteToHexString(scanDate[index]);
//            MyLog.E("类型是  " + type) ;
            dateLen = len - 1;
            byte[] tem = new byte[dateLen];
            for (int j = 0, i = index + 1; i < index + dateLen + 1; i++, j++) {
                tem[j] = scanDate[i];
            }
            parseBean.Date.add(tem);
            parseBean.Len.add(len);
            parseBean.Type.add("" + sType);
            index += (dateLen + 1);
        }
        return parseBean;
    }


    public static OpenLockRecord oldParseData(byte[] data) {
        //0  1  2  3  4  5  6  7  8  9  10 11 12 13 14 15 16 17 18 19
        //5f 51 04 1c c3 80 64 00 02 09 ff ff 19 05 14 16 55 04 00 00
        int openType1 = data[8] & 0xff;
        int userNumber = data[9] & 0xff;
        int index = data[7] & 0xff;
        String year = Rsa.byteToHexString(data[12]);
        String mouth = Rsa.byteToHexString(data[13]);
        String day = Rsa.byteToHexString(data[14]);
        String hour = Rsa.byteToHexString(data[15]);
        String min = Rsa.byteToHexString(data[16]);
        String second = Rsa.byteToHexString(data[17]);
        //yyyy-MM-dd HH:mm:ss
        String openTime = "20" + year + "-" + mouth + "-" + day + " " + hour + ":" + min + ":" + second;
        String openType = "";
        switch (openType1) {
            case 1:
                openType = PASSWORD;  //密码
                break;
            case 2:
                openType = FINGERPRINT;  //指纹
                break;
            case 3:
                openType = RFID;  //卡片
                break;
            case 4: //机械钥匙
                openType = MANUAL;
                break;
            case 5://遥控开门
                openType = RF;
                break;
            case 6:  //一键开启
                openType = ONE_KEY_OPEN;
                break;
            case 7: //APP 开启
                openType = PHONE;
                break;
            case 8:  //人脸
                openType = FACE_RECOGNITION;
                break;
            case 9:  //静脉
                openType = FINGER_VEIN;
                break;
            default:
                openType = UNKNOWN_OPEN;
                break;

        }
        //String user_num, String open_type, String open_time, int index
        OpenLockRecord openLockRecord = new OpenLockRecord(userNumber > 9 ? "" + userNumber : "0" + userNumber, openType, openTime, index);
        return openLockRecord;
    }

    /**
     * @param payload
     * @return
     */
    public static int DEFINE_TIME = 946684800 - 28800;      //1970-200年的时间 秒数 时差28800

    public static OpenLockRecord parseLockRecord(byte[] payload) {
        OpenLockRecord lockRecord;
        //0  1  2  3  4  5  6  7  8  9  10 11 12 13 14 15 16 17 18 19
        //5f 51 04 1c c3 80 64 00 02 09 ff ff 19 05 14 16 55 04 00 00
        byte[] openLockTime = new byte[4];

        System.arraycopy(payload, 6, openLockTime, 0, 4);
        long time = Rsa.bytes2Int(openLockTime);
        //开门时间秒
        long openTimes = time + DEFINE_TIME;
        String openDoorTime = DateUtils.getDateTimeFromMillisecond(openTimes * 1000);//要上传的开锁时间

        /**
         * 0x00：Keypad键盘
         * 0x01：RF遥控
         * 0x02：Manual手工
         * 0x03：RFID卡片
         * 0x04：Fingerprint指纹
         * 0x05：Voice语音
         * 0x06：Finger Vein指静脉
         * 0x07：Face Recognition人脸识别
         * 0xFF：不确定
         */
        String openType = "";
        switch (payload[3] & 0xff) {
            case 0:
                openType = PASSWORD;
                break;
            case 1:
                openType = RF;
                break;
            case 2:
                openType = MANUAL;
                break;
            case 3:
                openType = RFID;
                break;
            case 4:
                openType = FINGERPRINT;
                break;
            case 5:
                openType = VOICE;
                break;
            case 6:
                openType = FINGER_VEIN;
                break;
            case 7:
                openType = FACE_RECOGNITION;
                break;
            default:
                if ((payload[5] & 0xff) == 103) {
                    openType = PHONE;
                } else {
                    openType = UNKNOWN_OPEN;
                }
                break;
        }
        int number = (payload[5] & 0xff);
        lockRecord = new OpenLockRecord(number > 9 ? number + "" : "0" + number, openType, openDoorTime, (payload[1]) & 0xff);
        return lockRecord;
    }


    public static WarringRecord parseWarringRecord(byte[] payload) {
        WarringRecord warringRecord;
        byte[] openLockTime = new byte[4];
        System.arraycopy(payload, 6, openLockTime, 0, 4);
        long time = Rsa.bytes2Int(openLockTime);
        //警报时间  毫秒
        long openTimes = (time + DEFINE_TIME) * 1000;
        int warringType = payload[4] & 0xff;

        if ((payload[5] & 0xff) == 103) {
            warringRecord = new WarringRecord(warringType, openTimes);
        } else {
            warringRecord = new WarringRecord(warringType, openTimes);
        }
        return warringRecord;
    }


    public static String parseWarring(Context context, byte[] payload, String nickName) {
        String head = MyApplication.getInstance().getString(R.string.device) + " " + nickName + " ";
        StringBuffer buffer = new StringBuffer();

        LogUtils.e("收到报警信息   " + Integer.toBinaryString(payload[4] & 0xff) + "   " + Integer.toBinaryString(payload[5] & 0xff));

        int state0 = (payload[4] & 0b00000001) == 0b00000001 ? 1 : 0;
        if (state0 == 1) {
            buffer.append(context.getString(R.string.warring_low_power) + " ");
        }
        int state1 = (payload[4] & 0b00000010) == 0b00000010 ? 1 : 0;
        if (state1 == 1) {
            buffer.append(context.getString(R.string.warring_lock) + " ");
        }
        int state2 = (payload[4] & 0b00000100) == 0b00000100 ? 1 : 0;
        if (state2 == 1) {
            buffer = new StringBuffer();
            buffer.append(context.getString(R.string.open_door_fail_many_time) + " ");
        }
        int state3 = (payload[4] & 0b00001000) == 0b00001000 ? 1 : 0;
        if (state3 == 1) {
            buffer.append(context.getString(R.string.warring_defence) + " ");
        }

        int state4 = (payload[4] & 0b00010000) == 0b00010000 ? 1 : 0;
        if (state4 == 1) {
            buffer.append(context.getString(R.string.warring_temp_exception) + " ");
        }
        int state5 = (payload[4] & 0b00100000) == 0b00100000 ? 1 : 0;
        if (state5 == 1) {
            buffer.append(context.getString(R.string.warring_force) + " ");  //todo 此处需要提示吗
        }

        int state6 = (payload[4] & 0b01000000) == 0b01000000 ? 1 : 0;
        if (state6 == 1) {
            buffer.append(context.getString(R.string.warring_reset) + " ");
        }

        int state7 = (payload[4] & 0b10000000) == 0b10000000 ? 1 : 0;
        if (state7 == 1) {
            buffer.append(context.getString(R.string.warring_force_open) + " ");
        }

        int state8 = (payload[5] & 0b00000001) == 0b00000001 ? 1 : 0;
        if (state8 == 1) {
            buffer.append(context.getString(R.string.warring_key_remain) + " ");
        }

        int state9 = (payload[5] & 0b00000010) == 0b00000010 ? 1 : 0;
        if (state9 == 1) {
            buffer.append(context.getString(R.string.warring_safe) + " ");
        }

        int state10 = (payload[5] & 0b00000100) == 0b00000100 ? 1 : 0;
        if (state10 == 1) {
            buffer.append(context.getString(R.string.warring_unlock) + " ");
        }

        int state11 = (payload[5] & 0b00001000) == 0b00001000 ? 1 : 0;
        if (state11 == 1) {
            buffer.append(context.getString(R.string.warrning_open) + " ");
        }

        int state12 = (payload[5] & 0b00010000) == 0b00010000 ? 1 : 0;
        if (state12 == 1) {
            buffer.append(context.getString(R.string.warrning_lock_block) + " ");
        }

        int state13 = (payload[5] & 0b00100000) == 0b00100000 ? 1 : 0;
        if (state13 == 1) {
            buffer.append(context.getString(R.string.warrning_machine_key_open) + " ");
        }


        LogUtils.e("收到报警信息   " + buffer.toString());

        if (TextUtils.isEmpty(buffer.toString())) {
            return null;
        }
        return head + buffer.toString();
    }

    public static OperationLockRecord parseOperationRecord(byte[] payload) {
        int eventType = payload[5] & 0xff;
        byte[] byteIndex = new byte[2];
        System.arraycopy(payload, 2, byteIndex, 0, 2);
        int index = Rsa.bytesToInt(byteIndex);

        OperationLockRecord operationLockRecord = null;
        /**
         * 0x01：Operation操作(动作类)
         * 0x02：Program程序(用户管理类)
         * 0x03：Alarm
         *type-source-code 都是int值 以中划线分割
         * */
        LogUtils.e("eventType " + eventType + " index  " + index);
        if (1 == eventType || 2 == eventType) {
            int eventSource1 = payload[6] & 0xff;
            int eventCode = payload[7] & 0xff;
            int userId1 = payload[8] & 0xff;
            byte[] openLockTime = new byte[4];

            System.arraycopy(payload, 9, openLockTime, 0, 4);
            long time = Rsa.bytes2Int(openLockTime);
            //开门时间秒
            long openTimes = time + DEFINE_TIME;
            String openDoorTime = DateUtils.getDateTimeFromMillisecond(openTimes * 1000);//要上传的开锁时间
            operationLockRecord = new OperationLockRecord(eventType, eventSource1, eventCode, userId1, openDoorTime, MyApplication.getInstance().getUid());

        } else if (3 == eventType) {
            operationLockRecord = new OperationLockRecord(eventType, 0, 0, 0, "", MyApplication.getInstance().getUid());
        }
        return operationLockRecord;
    }

    public static boolean isMac(String mac) {
        if (TextUtils.isEmpty(mac)) {
            return false;
        }
        return false;
    }


    /**
     * 0x00：Keypad键盘
     * 0x01：RF遥控
     * 0x02：Manual手工
     * 0x03：RFID卡片
     * 0x04：Fingerprint指纹
     * 0x05：Voice语音
     * 0x06：Finger Vein指静脉
     * 0x07：Face Recognition人脸识别
     * 0xFF：不确定
     */
    public static String getOpenLockEventSourceContent(int eventSource, int userId) {
        String openType = "";
        switch (eventSource & 0xff) {
            case 0:
                openType = PASSWORD;
                break;
            case 1:
                openType = RF;
                break;
            case 2:
                openType = MANUAL;
                break;
            case 3:
                openType = RFID;
                break;
            case 4:
                openType = FINGERPRINT;
                break;
            case 5:
                openType = VOICE;
                break;
            case 6:
                openType = FINGER_VEIN;
                break;
            case 7:
                openType = FACE_RECOGNITION;
                break;
            default:
                if (userId == 103) {
                    openType = PHONE;
                }
                break;

        }
        return openType;
    }

    public static String getOperationEventSourceContent(int eventSource) {
        String openType = "";
        switch (eventSource & 0xff) {
            case 0:
                openType = DOOR_LOCK;
                break;
            case 8:
                openType = APP;
                break;
        }
        return openType;
    }


    /**
     * 0x00：UnknownOrMfgSpecific(保留)
     * 0x01：MasterCodeChanged管理员密码修改
     * 0x02：PINCodeAdded	密码添加
     * 0x03：PINCodeDeleted	密码删除
     * 0x04：PINCodeChanged	密码修改(不支持)
     * 0x05：RFIDCodeAdded	RFID添加
     * 0x06：RFIDCodeDeleted	RFID删除
     * 0x07：FingerprintAdded	指纹添加
     * 0x08：FingerprintDeleted	指纹删除
     * 0x09：APP添加
     * 0x0A：APP删除
     * 0x0B：人脸添加
     * 0x0C：人脸删除
     * 0xD：指静脉添加
     * 0xE：指静脉删除
     */
    public static String getOperationProgramContent(int eventCode) {
        String openType = "";
        switch (eventCode) {
            case 0:
                openType = RETAIN;
                break;
            case 1:
                openType = ADMINISTRATOR_PASSWORD_MODIFY;
                break;
            case 2:
                openType = PASSWORD_ADD;
                break;
            case 3:
                openType = PASSWORD_DELETE;
                break;
            case 4:
                openType = "密码修改(不支持)";
                break;
            case 5:
                openType = "卡片添加";
                break;
            case 6:
                openType = "卡片删除";
                break;
            case 7:
                openType = FINGERPRINT_ADD;
                break;
            case 8:
                openType = FINGERPRINT_DELETE;
                break;
//            case 9:
//                openType = APP_ADD;
//                break;
//            case 10:
//                openType = APP_DELETE;
//                break;
            case 11:
                openType = FACE_ADD;
                break;
            case 12:
                openType = FACE_DELETE;
                break;
            case 13:
                openType = INTRAVENOUS_ADD;
                break;
            case 14:
                openType = INTRAVENOUS_DELETE;
                break;

        }
        return openType;
    }


    public static String getNickName(OperationLockRecord record, GetPasswordResult passwordResults) {
        String nickName = "";
        int eventType = record.getEventType();
        int eventSource = record.getEventSource();
        int eventCode = record.getEventCode();
        int uNum = record.getUserNum();
        nickName = uNum == 103 ? "App" : record.getUserNum() > 9 ? record.getUserNum() + "" : "0" + record.getUserNum();
        if (1 == eventType) {  //开门关门类型
            switch (eventSource) {
                case 0x00://：Keypad键盘  密码开锁
                    if (passwordResults != null && passwordResults.getData() != null && passwordResults.getData().getPwdList() != null) {
//                        if (passwordResults != null ) {
                        List<ForeverPassword> pwdList = passwordResults.getData().getPwdList();
                        for (ForeverPassword password : pwdList) {
                            if (Integer.parseInt(password.getNum()) == uNum) {
                                nickName = password.getNickName();
                            }
                        }
                        List<GetPasswordResult.DataBean.TempPassword> tempPwdList = passwordResults.getData().getTempPwdList();
                        for (GetPasswordResult.DataBean.TempPassword password : tempPwdList) {
                            if (Integer.parseInt(password.getNum()) == uNum) {
                                nickName = password.getNickName();
                            }
                        }
                    }
                    break;

                case 0x03://：RFID卡片
//                    if (passwordResults != null) {
                    if (passwordResults != null&&passwordResults.getData()!=null&&passwordResults.getData().getCardList()!=null) {
                        List<GetPasswordResult.DataBean.Card> cards = passwordResults.getData().getCardList();
                        for (GetPasswordResult.DataBean.Card password : cards) {
                            if (Integer.parseInt(password.getNum()) == uNum) {
                                nickName = password.getNickName();
                            }
                        }
                    }

                    break;
                case 0x04://：Fingerprint指纹
                    if (passwordResults != null&&passwordResults.getData()!=null&&passwordResults.getData().getFingerprintList()!=null) {
                        List<GetPasswordResult.DataBean.Fingerprint> fingerprints = passwordResults.getData().getFingerprintList();
                        for (GetPasswordResult.DataBean.Fingerprint password : fingerprints) {
                            if (Integer.parseInt(password.getNum()) == uNum) {
                                nickName = password.getNickName();
                            }
                        }
                    }
                    break;
            }
        } else if (2 == eventType) {  //
            nickName = BleUtil.getOperationProgramContent(eventCode);
        }
        return nickName;
    }


    public static String getOpenType(OperationLockRecord record) {
        String openType = "";
        int eventType = record.getEventType();
        int eventSource = record.getEventSource();
        int eventCode = record.getEventCode();
        int uNum = record.getUserNum();
        openType = uNum == 103 ? "App" : record.getUserNum() > 9 ? record.getUserNum() + "" : "0" + record.getUserNum();
        if (1 == eventType) {  //开门关门类型
            switch (eventSource) {
                case 0x00:// "密码"  break;
                    openType = MyApplication.getInstance().getString(R.string.password_open);
                    break;
                case 0x01:// "遥控";  break;
                    openType = MyApplication.getInstance().getString(R.string.rf_open);
                    break;
                case 0x02:// = "手动";  break;
                    openType = MyApplication.getInstance().getString(R.string.manual_open);
                    break;
                case 0x03://= "卡片";  break;
                    openType = MyApplication.getInstance().getString(R.string.rfid_open);
                    break;
                case 0x04://= "指纹";  break;
                    openType = MyApplication.getInstance().getString(R.string.fingerprint_open);
                    break;
                case 0x05://= "语音";  break;
                    openType = MyApplication.getInstance().getString(R.string.voice_open);
                    break;
                case 0x06:// = "静脉";  break;
                    openType = MyApplication.getInstance().getString(R.string.finger_vein_open);
                    break;
                case 0x07:// = "人脸";  break;
                    openType = MyApplication.getInstance().getString(R.string.face_recognition_open);
                    break;
                case 0x08://= "手机";  break;
                    openType = MyApplication.getInstance().getString(R.string.app_open);
                    break;
                case 0xFF:
                    if (uNum == 103) {
                        openType = MyApplication.getInstance().getString(R.string.app_open);
                    } else {
                        openType = MyApplication.getInstance().getString(R.string.unknown_open);
                    }
                    break;
            }
        } else if (2 == eventType) {
            openType = BleUtil.getOperationProgramContent(eventCode);
        }
        return openType;
    }


}
