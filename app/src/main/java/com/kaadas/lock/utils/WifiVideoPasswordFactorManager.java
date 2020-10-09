package com.kaadas.lock.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.CRC32;

public class WifiVideoPasswordFactorManager {


    private static WifiVideoPasswordFactorManager instance;

    public static WifiVideoPasswordFactorManager getInstance() {
        if (instance == null) {
            instance = new WifiVideoPasswordFactorManager();
            LogUtils.e("--Kaadas--初始化OfflinePasswordFactorManager");
        }
        return instance;
    }

    public static FactorResult parsePasswordData(String adminPassword, byte[] data){
        FactorResult result = new FactorResult();
        result.data = data;
        result.func = data[33] & 0xff;

        String temp = "";
        for (int i = 0; i < adminPassword.length(); i++) {
            temp += "0" + adminPassword.charAt(i);
        }

        byte[] t = Rsa.hex2byte(temp);
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            result.result = -1;
        }

        messageDigest.update(t);
        byte[] adminKey = messageDigest.digest();

        byte[] content = new byte[32];
        //Object src,  int  srcPos,    Object dest, int destPos,    int length
        System.arraycopy(data, 0, content, 0, content.length);

        byte[] decrypt = Rsa.decrypt(content, adminKey);

        byte[] pwd = new byte[28];
        byte[] crc = new byte[4];


        System.arraycopy(decrypt, 0, pwd, 0, pwd.length);
        System.arraycopy(decrypt, 28, crc, 0, crc.length);

        CRC32 crc32 = new CRC32();
        crc32.update(pwd);
        long localCrc = crc32.getValue();
        byte[] bytes = Rsa.int2BytesArray((int) localCrc);
        LogUtils.e("shulan -- localCrc " + localCrc);
        LogUtils.e("shulan 校验和 本地CRC  " + Rsa.bytesToHexString(bytes) + "   锁端CRC  " + Rsa.bytesToHexString(crc));
        if (bytes[0] != crc[0] || bytes[1] != crc[1] || bytes[2] != crc[2] || bytes[3] != crc[3]) { //校验失败
            result.result = -2;
            return result;
        }
        result.password = pwd;
        return result;
    }

    public static FactorResult parsePasswordData(String adminPassword, String str){
        FactorResult result = new FactorResult();
//        WifiResult wifiResult = new WifiResult();
//        wifiResult.data = data;
//        wifiResult.func = data[45] & 0xff;
        String s = str.substring(0,64) + str.substring(str.length() - 2);
        LogUtils.e("shulan ----------s-->" + s);
        LogUtils.e("shulan ----------s.size-->" + s.length());

        byte[] data = Rsa.hexString2Bytes(s);

        result.wifiSn = str.substring(65,77);
        LogUtils.e("shulan wifiResult.result.wifiSn==" + result.wifiSn);

        result.func = data[data.length -1] & 0xFF;
        LogUtils.e("shulan wifiResult.result.func==" + result.func);

        String temp = "";
        for (int i = 0; i < adminPassword.length(); i++) {
            temp += "0" + adminPassword.charAt(i);
        }
        LogUtils.e("shulan temp==" + temp);


        byte[] t = Rsa.hex2byte(temp);
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            result.result = -1;
        }
        messageDigest.update(t);
        byte[] adminKey = messageDigest.digest();
        LogUtils.e("shulan 管理密码管理  Sha256之后是 " + Rsa.bytesToHexString(adminKey));
        LogUtils.e("shulan 管理密码管理  adminKey.length Sha256之后是 " + adminKey.length);

        byte[] content = new byte[32];
        //Object src,  int  srcPos,    Object dest, int destPos,    int length
        System.arraycopy(data, 0, content, 0, content.length);

        byte[] decrypt = Rsa.decrypt(content, adminKey);
        LogUtils.e("shulan 解密之后的数据是  " + Rsa.bytesToHexString(adminKey));
        LogUtils.e("shulan decrypt 解密之后的数据是  " + decrypt.length);

        byte[] pwd = new byte[28];
        byte[] crc = new byte[4];

        System.arraycopy(decrypt, 0, pwd, 0, pwd.length);
        System.arraycopy(decrypt, 28, crc, 0, crc.length);

        CRC32 crc32 = new CRC32();
        crc32.update(pwd);
        long localCrc = crc32.getValue();
        byte[] bytes = Rsa.int2BytesArray((int) localCrc);
        LogUtils.e("shulan -- localCrc " + localCrc);
        LogUtils.e("shulan 校验和 本地CRC  " + Rsa.bytesToHexString(bytes) + "   锁端CRC  " + Rsa.bytesToHexString(crc));
        if (bytes[0] != crc[0] || bytes[1] != crc[1] || bytes[2] != crc[2] || bytes[3] != crc[3]) { //校验失败
            result.result = -2;
            return result;
        }
        result.result = 0;
        result.password = pwd;
        return result;
    }

    public static class FactorResult{
        public int result;  // -1 SHA256失败  -2校验失败  0成功
        public String wifiSn;
        public byte[] password;
        public byte[] data;
        public int func;

        @Override
        public String toString() {
            return "FactorResult{" +
                    "result=" + result +
                    ", wifiSn=" + new String(wifiSn) +
                    ", password=" + new String(password) +
                    ", data=" + new  String(data) +
                    ", func=" + func +
                    '}';
        }
    }

}


