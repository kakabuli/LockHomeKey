package com.kaadas.lock.activity.device.wifilock.password;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.Rsa;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.CRC32;

public class Test {


    public static void main(String[] args) {
//        String adminPassword = "147147";
//        compCrc();
//        testSha256();
//        byte[] data = Rsa.hex2byte2("47a98fd4 b04e8d8b 791b70d6 34219eb3 8056d6ac 092d2ac7 8a1d8cb1 5f620d42 57463031 31393338 31303030 33");
//        System.out.println("数据是   " + new String(Rsa.hex2byte2("47a98fd4 b04e8d8b 791b70d6 34219eb3 8056d6ac 092d2ac7 8a1d8cb1 5f620d42 57463031 31393338 31303030 33")));
////        byte[] data = Rsa.hex2byte2("9C C9 E8 86 17 0A 8F 4E 0A 20 75 3C 5A 6F 61 09 46 C5 86 94 7B 34 43 56 10 A2 CF EC 6D 30 87 DE 57 46 30 31 31 39 33 38 31 30 30 30 33");
//        System.out.println("数据是   " + Rsa.bytesToHexString(data));
//        MessageDigest messageDigest = null;
//        try {
//            messageDigest = MessageDigest.getInstance("SHA-256");
//            String t = "";
//            for (int i = 0; i < adminPassword.length(); i++) {
//                t += "0" + adminPassword.charAt(i);
//            }
//            byte[] bPwd = Rsa.hex2byte(t);
//            messageDigest.update(bPwd);
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//
//
//        byte[] adminKey = messageDigest.digest();
//        System.out.println("SHA256管理密码   " + Rsa.bytesToHexString(adminKey));
//
//        byte[] content = new byte[32];
//        byte[] sn = new byte[13];
//        //Object src,  int  srcPos,    Object dest, int destPos,    int length
//        System.arraycopy(data, 0, content, 0, content.length);
//        System.arraycopy(data, 32, sn, 0, sn.length);
//        byte[] decrypt = Rsa.decrypt(content, adminKey);
//        System.out.println("解密后的数据是   " + Rsa.bytesToHexString(decrypt));
//        byte[] pwd = new byte[28];
//        byte[] crc = new byte[4];
//
//
//        System.arraycopy(decrypt, 0, pwd, 0, pwd.length);
//        System.arraycopy(decrypt, 28, crc, 0, crc.length);
//        System.out.println("解密后的数据是 pwd  " + Rsa.bytesToHexString(pwd));
//        System.out.println("解密后的数据是 crc  " + Rsa.bytesToHexString(crc));
//        CRC32 crc32 = new CRC32();
//        crc32.update(pwd);
//        long localCrc = crc32.getValue();
//        byte[] bytes = Rsa.int2BytesArray((int) localCrc);
//        if (bytes[0] != crc[0] || bytes[1] != crc[1] || bytes[2] != crc[2] || bytes[3] != crc[3]) {
////            System.out.println("校验和 错误  本地CRC" + localCrc + "   锁端CRC  " + lockCrc);
//            System.out.println("校验和 错误  本地CRC  " + Rsa.bytesToHexString(bytes) + "   锁端CRC  " + Rsa.bytesToHexString(crc));
//            return;
//        }
//        String wifiSn = new String(sn);
//        String randomCode = Rsa.bytesToHexString(pwd);
//        System.out.println("设备返回的随机码是  16进制 " + Rsa.bytesToHexString(pwd));
//        System.out.println("设备返回的随机码是   长度是 " + randomCode.length() + "  字符串  " + randomCode);
//        System.out.println("读取数据成功");


//        getPassword();
        createPassword();
    }

    public static void compCrc() {
        byte[] data = Rsa.hex2byte2("92 D1 8E 71 8D 2A BF 60 50 A4 E5 64 37 08 2B C1 FB 68 AD 43 E8 35 E6 F9 67 07 DE 1D");
        CRC32 crc32 = new CRC32();
        crc32.update(data);
        long localCrc = crc32.getValue();
        byte[] data2 = Rsa.hex2byte2("27 EC C5 F1");
        int i = Rsa.byteArrayToInt(data2);
        long x = Rsa.bytes2Int(data2);
        System.out.println("data算出校验和 0   " + localCrc);

        System.out.println("data算出校验和 1   " + Rsa.bytesToHexString(Rsa.int2BytesArray((int) localCrc)));
        System.out.println("data算出校验和 1   " + Rsa.bytesToHexString(Rsa.long2Bytes(localCrc)));
        System.out.println("data算出校验和 2   " + i);
        System.out.println("data算出校验和 3   " + x);
    }

    public static void testSha256() {
        MessageDigest messageDigest;
        String s = "147147";
        String temp = "";
        for (int i = 0; i < s.length(); i++) {
            temp += "0" + s.charAt(i);
        }
        byte[] data = Rsa.hex2byte(temp);
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(data);
            System.out.println("   testSha256 数据是   " + Rsa.bytesToHexString(messageDigest.digest()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static void createPassword() {

        String str = "REXWIFI";
        String ssid = "kaadas";
        String password = "kaadas668";
        byte[] bHead = str.getBytes();
        byte[] bSsid = ssid.getBytes();
        byte[] bPassword = password.getBytes();

        byte[] result = new byte[bHead.length + bSsid.length + bPassword.length + 2];
        System.arraycopy(bHead, 0, result, 0, bHead.length);

        result[bHead.length] = (byte) (bSsid.length & 0xff);
        System.arraycopy(bSsid, 0, result, bHead.length, bSsid.length);

        result[bHead.length + bSsid.length + 1] = (byte) (bPassword.length & 0xff);
        System.arraycopy(bPassword, 0, result, bHead.length + bSsid.length + 1, bPassword.length);

        System.out.println("结果是   " + Rsa.bytesToHexString(result) );
    }


    private static String getPassword() {
        String wifiSN = "WF01193810004";
        String randomCode = "8a4ad8eda904e7d860177583fb25faf8e6622aeff7d72977a857c7ee";
        String time = (System.currentTimeMillis() / 1000 / 60 / 5) + "";
        String content = wifiSN + randomCode + time;
        System.out.println("拼接的数据是  " + content);
        System.out.println("当前时间戳  " + (System.currentTimeMillis() / 1000 / 60 / 5) + "");
        byte[] data = content.toUpperCase().getBytes();
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(data);
            byte[] digest = messageDigest.digest();
            System.out.println("Sha256之后的数据是     " + Rsa.bytesToHexString(digest));
            byte[] temp = new byte[4];
            System.arraycopy(digest, 0, temp, 0, 4);
            System.out.println("前四个字节是     " + Rsa.bytesToHexString(temp));
            long l = Rsa.getInt(temp);
            System.out.println("获取的整数是     " + l);
            String text = (l % 1000000) + "";
            System.out.println("转换之后的数据是     ");
            for (int i = 0; i < (6 - text.length()); i++) {
                text = "0" + text;
            }
            System.out.println("  生成的密码是  " + text);
            return text;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
