package com.kaadas.lock.utils;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.CRC32;

public class OfflinePasswordFactorManager {

    private String TAG = "OfflinePasswordFactorManager";
    private static OfflinePasswordFactorManager instance;

    public static OfflinePasswordFactorManager getInstance() {
        if (instance == null) {
            instance = new OfflinePasswordFactorManager();
            LogUtils.e("--Kaadas--初始化OfflinePasswordFactorManager");
        }
        return instance;
    }

    public static OfflinePasswordFactorResult parseOfflinePasswordFactorData(String adminPassword, byte[] data) {
        OfflinePasswordFactorResult wifiResult = new OfflinePasswordFactorResult();
        wifiResult.data = data;
        wifiResult.func = data[45] & 0xff;

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
            wifiResult.result = -1;
        }
        messageDigest.update(t);
        byte[] adminKey = messageDigest.digest();
        LogUtils.e("--kaadas--管理密码管理  Sha256之后是 " + Rsa.bytesToHexString(adminKey));

        byte[] content = new byte[32];
        byte[] sn = new byte[13];
        //Object src,  int  srcPos,    Object dest, int destPos,    int length
        System.arraycopy(data, 0, content, 0, content.length);
        System.arraycopy(data, 32, sn, 0, sn.length);

        byte[] decrypt = Rsa.decrypt(content, adminKey);
        LogUtils.e("--kaadas--解密之后的数据是  " + Rsa.bytesToHexString(adminKey));

        byte[] pwd = new byte[28];
        byte[] crc = new byte[4];

        System.arraycopy(decrypt, 0, pwd, 0, pwd.length);
        System.arraycopy(decrypt, 28, crc, 0, crc.length);

        LogUtils.e("--kaadas--随机数明文是  " + Rsa.bytesToHexString(pwd));
        LogUtils.e("--kaadas--RCR明文是  " + Rsa.bytesToHexString(crc));
        CRC32 crc32 = new CRC32();
        crc32.update(pwd);
        long localCrc = crc32.getValue();
        byte[] bytes = Rsa.int2BytesArray((int) localCrc);
        LogUtils.e("--kaadas--密码因子校验--本地CRC  " + Rsa.bytesToHexString(bytes) + "   锁端CRC  " + Rsa.bytesToHexString(crc));
        if (bytes[0] != crc[0] || bytes[1] != crc[1] || bytes[2] != crc[2] || bytes[3] != crc[3]) { //校验失败
//            MyLog.getInstance().save("--kaadas调试--配网过程密码因子校验--校验失败");
            wifiResult.result = -2;
            return wifiResult;
        }
//        MyLog.getInstance().save("--kaadas调试--配网过程密码因子校验--校验成功");
        MyLog.getInstance().save("--kaadas调试--配网过程解密后的密码因子==" + Rsa.bytesToHexString(pwd));
        wifiResult.password = pwd;
        wifiResult.wifiSn = sn;
        return wifiResult;
    }

    public static class  OfflinePasswordFactorResult {
        public int result;  // -1 SHA256失败  -2校验失败  0成功
        public byte[] wifiSn;
        public byte[] password;
        public byte[] data;
        public int func;

        @Override
        public String toString() {
            return "WifiResult{" +
                    "result=" + result +
                    ", wifiSn=" + new String(wifiSn) +
                    ", password=" + new String(password) +
                    ", data=" + new  String(data) +
                    ", func=" + func +
                    '}';
        }
    }

}
